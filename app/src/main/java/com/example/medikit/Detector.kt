package com.example.medikit

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/** Recovered weights from the user's original MEDIKIT.apk (2025 build). */
object Detector {
    private val labels = arrayOf(
        "Alergias", "Cortaduras", "Esguinces", "Fracturas",
        "Hematomas", "No lesiones", "Quemaduras", "Raspaduras"
    )
    private const val SIZE = 224
    private val lock = Any()
    private var interpreter: Interpreter? = null

    // Kotlin automatically generates getClase() and getConfianza() for Java.
    data class ResultadoIA(val clase: String, val confianza: Float)

    @JvmStatic
    fun analizarImagen(context: Context, bitmap: Bitmap): ResultadoIA = synchronized(lock) {
        val model = interpreter ?: createInterpreter(context.applicationContext).also { interpreter = it }
        require(model.getInputTensor(0).dataType() == DataType.FLOAT32) { "Input must be FLOAT32" }
        require(model.getOutputTensor(0).dataType() == DataType.FLOAT32) { "Output must be FLOAT32" }
        require(model.getInputTensor(0).shape().contentEquals(intArrayOf(1, SIZE, SIZE, 3))) {
            "Unexpected model input shape"
        }
        require(model.getOutputTensor(0).shape().contentEquals(intArrayOf(1, labels.size))) {
            "Unexpected model output shape"
        }

        val input = toByteBuffer(bitmap)
        val output = Array(1) { FloatArray(labels.size) }
        model.run(input, output)
        val probabilities = output[0]
        require(probabilities.all { it.isFinite() && it in 0f..1f }) { "Invalid model output" }
        val i = probabilities.indices.maxByOrNull { probabilities[it] }
            ?: throw IllegalStateException("No predictions were returned")
        ResultadoIA(labels[i], probabilities[i])
    }

    private fun createInterpreter(context: Context): Interpreter {
        val mappedModel = context.assets.openFd("modelo_lesiones.tflite").use { fd ->
            FileInputStream(fd.fileDescriptor).use { stream ->
                stream.channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
            }
        }
        return Interpreter(mappedModel, Interpreter.Options().setNumThreads(2))
    }

    private fun toByteBuffer(bitmap: Bitmap): ByteBuffer {
        val scaled = Bitmap.createScaledBitmap(bitmap, SIZE, SIZE, true)
        val bytes = ByteBuffer.allocateDirect(4 * SIZE * SIZE * 3).order(ByteOrder.nativeOrder())
        val pixels = IntArray(SIZE * SIZE)
        scaled.getPixels(pixels, 0, SIZE, 0, 0, SIZE, SIZE)
        for (pixel in pixels) {
            bytes.putFloat(((pixel shr 16) and 0xff) / 255f) // R
            bytes.putFloat(((pixel shr 8) and 0xff) / 255f)  // G
            bytes.putFloat((pixel and 0xff) / 255f)         // B
        }
        bytes.rewind()
        if (scaled !== bitmap) scaled.recycle()
        return bytes
    }
}
