package com.example.medikit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int REQUEST_GALLERY = 2;

    private ImageView imageView;
    private TextView txtResultado;
    private ProgressBar progresoCarga;
    private Button btnAnalizar, btnNueva, btnGaleria;
    private Bitmap fotoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAnalizar = findViewById(R.id.btnAnalizar);
        btnNueva = findViewById(R.id.btnNueva);
        btnGaleria = findViewById(R.id.btnGaleria);
        imageView = findViewById(R.id.imagenTomada);
        txtResultado = findViewById(R.id.txtResultado);
        progresoCarga = findViewById(R.id.progresoCarga);

        btnAnalizar.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                abrirCamara();
            }
        });

        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        });

        btnNueva.setOnClickListener(v -> reiniciarAnalisis());
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void reiniciarAnalisis() {
        fotoActual = null;
        imageView.setImageBitmap(null);
        txtResultado.setText("Resultado:");
        btnNueva.setVisibility(View.GONE);
        btnAnalizar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                txtResultado.setText("Permiso de cámara denegado.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == CAMERA_REQUEST) {
                    fotoActual = (Bitmap) data.getExtras().get("data");
                } else if (requestCode == REQUEST_GALLERY) {
                    fotoActual = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                }
                imageView.setImageBitmap(fotoActual);
                analizarFoto(fotoActual);
            } catch (IOException e) {
                txtResultado.setText("Error al procesar la imagen.");
            }
        }
    }

    private void analizarFoto(Bitmap bitmap) {
        progresoCarga.setVisibility(View.VISIBLE);
        btnAnalizar.setVisibility(View.GONE);
        txtResultado.setText("Analizando imagen...");

        new Thread(() -> {
            String resultado = Detector.analizarImagen(this, bitmap);
            runOnUiThread(() -> {
                progresoCarga.setVisibility(View.GONE);
                btnNueva.setVisibility(View.VISIBLE);
                txtResultado.setText("Resultado: " + resultado);
            });
        }).start();
    }
}