package com.example.medikit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String SAVE_CAPTURE_URI = "capture_uri";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private Uri pendingCaptureUri;
    private long requestId = 0;

    private ImageView preview;
    private View introCard, previewCard, loadingContainer, resultCard;
    private LinearLayout initialActions;
    private ProgressBar progress;
    private TextView textTipo, textPorcentaje, textRecomendacion, txtEstado;
    private MaterialButton btnNueva;

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (Boolean.TRUE.equals(success) && pendingCaptureUri != null) {
                    loadAndAnalyze(pendingCaptureUri);
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) loadAndAnalyze(uri);
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(SAVE_CAPTURE_URI);
            if (saved != null) pendingCaptureUri = Uri.parse(saved);
        }

        preview = findViewById(R.id.imagenTomada);
        introCard = findViewById(R.id.introCard);
        previewCard = findViewById(R.id.previewCard);
        initialActions = findViewById(R.id.initialActions);
        loadingContainer = findViewById(R.id.loadingContainer);
        resultCard = findViewById(R.id.resultCard);
        textTipo = findViewById(R.id.textTipo);
        textPorcentaje = findViewById(R.id.textPorcentaje);
        textRecomendacion = findViewById(R.id.textRecomendacion);
        txtEstado = findViewById(R.id.txtEstado);
        progress = findViewById(R.id.progresoCoincidencia);
        btnNueva = findViewById(R.id.btnNueva);

        findViewById(R.id.btnAnalizar).setOnClickListener(v -> openCamera());
        findViewById(R.id.btnGaleria).setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnNueva.setOnClickListener(v -> reset());
        reset();
    }

    private void openCamera() {
        try {
            File folder = new File(getCacheDir(), "captures");
            if (!folder.exists() && !folder.mkdirs()) throw new IOException("No se pudo crear directorio de cámara");
            File image = File.createTempFile("medikit_", ".jpg", folder);
            pendingCaptureUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", image);
            cameraLauncher.launch(pendingCaptureUri);
        } catch (Exception ex) {
            showError("No se pudo abrir la cámara: " + ex.getMessage());
        }
    }

    private void loadAndAnalyze(Uri uri) {
        long current = ++requestId;
        introCard.setVisibility(View.GONE);
        initialActions.setVisibility(View.GONE);
        previewCard.setVisibility(View.GONE);
        resultCard.setVisibility(View.GONE);
        txtEstado.setVisibility(View.GONE);
        btnNueva.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
        worker.execute(() -> {
            try {
                Bitmap bitmap = decodeBitmap(uri);
                Detector.ResultadoIA result = Detector.analizarImagen(getApplicationContext(), bitmap);
                if (bitmap == null) throw new IOException("Imagen no válida");
                runOnUiThread(() -> {
                    if (current != requestId || isFinishing() || isDestroyed()) return;
                    preview.setImageBitmap(bitmap);
                    previewCard.setVisibility(View.VISIBLE);
                    loadingContainer.setVisibility(View.GONE);
                    resultCard.setVisibility(View.VISIBLE);
                    btnNueva.setVisibility(View.VISIBLE);
                    String clase = result.getClase();
                    float score = result.getConfianza();
                    textTipo.setText(clase);
                    textPorcentaje.setText(String.format(Locale.getDefault(), "%.1f %%", 100f * score));
                    progress.setProgress(Math.round(score * 100));
                    textRecomendacion.setText(Recomendaciones.para(clase, score));
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (current != requestId || isFinishing() || isDestroyed()) return;
                    showError("No se pudo analizar la imagen: " + e.getMessage());
                });
            }
        });
    }

    private Bitmap decodeBitmap(Uri uri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            if (stream == null) throw new IOException("No es posible abrir la imagen");
            BitmapFactory.decodeStream(stream, null, options);
        }
        if (options.outWidth <= 0 || options.outHeight <= 0) throw new IOException("Imagen dañada o no compatible");
        int sample = 1;
        while (options.outWidth / sample > 1280 || options.outHeight / sample > 1280) sample *= 2;
        options.inSampleSize = sample;
        options.inJustDecodeBounds = false;
        Bitmap bitmap;
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            if (stream == null) throw new IOException("No es posible abrir la imagen");
            bitmap = BitmapFactory.decodeStream(stream, null, options);
        }
        if (bitmap == null) throw new IOException("No es posible decodificar la imagen");
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            if (stream != null) {
                int orientation = new ExifInterface(stream).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int degrees = orientation == ExifInterface.ORIENTATION_ROTATE_90 ? 90
                        : orientation == ExifInterface.ORIENTATION_ROTATE_180 ? 180
                        : orientation == ExifInterface.ORIENTATION_ROTATE_270 ? 270 : 0;
                if (degrees != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degrees);
                    Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (oriented != bitmap) bitmap.recycle();
                    bitmap = oriented;
                }
            }
        } catch (IOException ignored) {
            // EXIF is optional; still allow inference for valid images.
        }
        return bitmap;
    }

    private void showError(String message) {
        loadingContainer.setVisibility(View.GONE);
        txtEstado.setText(message);
        txtEstado.setVisibility(View.VISIBLE);
        btnNueva.setVisibility(View.VISIBLE);
    }

    private void reset() {
        requestId++;
        introCard.setVisibility(View.VISIBLE);
        initialActions.setVisibility(View.VISIBLE);
        previewCard.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        resultCard.setVisibility(View.GONE);
        txtEstado.setVisibility(View.GONE);
        btnNueva.setVisibility(View.GONE);
        preview.setImageDrawable(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (pendingCaptureUri != null) outState.putString(SAVE_CAPTURE_URI, pendingCaptureUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        worker.shutdown();
        super.onDestroy();
    }
}
