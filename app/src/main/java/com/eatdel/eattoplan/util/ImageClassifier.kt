package com.eatdel.eattoplan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifier(private val context: Context) {

    private val MODEL_NAME = "food_model.tflite"
    private val interpreter: Interpreter
    private val labels: List<String>
    private val imageSize = 224
    private val numChannels = 3

    init {
        val model = loadModel(MODEL_NAME)
        interpreter = Interpreter(model)
        labels = context.assets.open("labels.txt").bufferedReader().readLines()
    }

    fun classify(bitmap: Bitmap): String {
        val input = convertBitmapToByteBuffer(bitmap)
        val output = Array(1) { FloatArray(labels.size) }

        interpreter.run(input, output)

        val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        return labels.getOrElse(maxIdx) { "알 수 없음" }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        val buffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * numChannels)
        buffer.order(ByteOrder.nativeOrder())

        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = resized.getPixel(x, y)
                buffer.putFloat(Color.red(pixel) / 255.0f)
                buffer.putFloat(Color.green(pixel) / 255.0f)
                buffer.putFloat(Color.blue(pixel) / 255.0f)
            }
        }
        return buffer
    }

    private fun loadModel(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        return inputStream.channel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }
}
