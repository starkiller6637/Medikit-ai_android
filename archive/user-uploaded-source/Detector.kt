package com.example.medikit

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Detector {
    private val clases = listOf(
        "Alergias", "Cortaduras", "Esguinces", "Fracturas",
        "Hematomas", "No lesiones", "Quemaduras", "Raspaduras"
    )

    fun analizarImagen(context: Context, bitmap: Bitmap): String {
        val modeloBytes = context.assets.open("modelo_lesiones.tflite").readBytes()
        val interpreter = Interpreter(modeloBytes)

        val input = bitmapAByteBuffer(bitmap)
        val output = Array(1) { FloatArray(clases.size) }

        interpreter.run(input, output)
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        return clases[maxIndex]
    }

    private fun bitmapAByteBuffer(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(224 * 224)
        resized.getPixels(pixels, 0, 224, 0, 0, 224, 224)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16 and 0xFF) / 255f))
            buffer.putFloat(((pixel shr 8 and 0xFF) / 255f))
            buffer.putFloat(((pixel and 0xFF) / 255f))
        }

        return buffer
    }
}