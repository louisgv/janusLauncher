package com.blueberry.lab.januslauncher

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*




/**
 * Created by jojo on 8/23/17.
 */

class TensorFlowEMNISTClassifier(assetManager: AssetManager,
                                 modelFilename: String,
                                 labelFilename: String,
                                 private val inputSize: Int,
                                 private val inputName: String,
                                 private val outputName: String) : Classifier {
    private val TAG = "TensorFlowEMNIST"

    private var logStats = false

    private val MAX_RESULTS = 3
    private val THRESHOLD = 0.1f

    private val inferenceInterface = TensorFlowInferenceInterface(assetManager, modelFilename)

    // Pre-allocated buffers.
    private val labels = Vector<String>()

    private val operation = inferenceInterface.graphOperation(outputName)
    private val numClasses = operation.output(0).shape().size(1).toInt()

    private val outputs = FloatArray(numClasses)

    private val outputNames = arrayOf(outputName)

    private val networkStructure = longArrayOf(1, inputSize.toLong(), inputSize.toLong(), 1)

    init {
        val br: BufferedReader?
        val actualFilename = labelFilename.split("file:///android_asset/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        try {
            br = BufferedReader(InputStreamReader(assetManager.open(actualFilename)))
            var line: String?
            do {
                line = br.readLine()
                labels.add(line)
            } while (line != null)
            br.close()

        } catch (e: IOException) {
            throw RuntimeException("Problem reading label file!", e)
        }
    }

    override val statString: String
        get() = inferenceInterface.statString

    override fun close() {
        inferenceInterface.close()
    }

    override fun enableStatLogging(debug: Boolean) {
        logStats = debug
    }

    override fun recognizeImage(bitmap: Bitmap): List<Recognition> {
        val pixels = getPixelData(bitmap)

        inferenceInterface.feed(inputName, pixels, *networkStructure)

        inferenceInterface.run(outputNames, logStats)

        inferenceInterface.fetch(outputName, outputs)

        val pq = PriorityQueue(
                MAX_RESULTS,
                Comparator<Recognition> { lhs, rhs ->
                    // Intentionally reversed to put high confidence at the head of the queue.
                    java.lang.Float.compare(rhs.confidence!!, lhs.confidence!!)
                })

        (0 until outputs.size)
                .asSequence()
                .filter { outputs[it] > THRESHOLD }
                .mapTo(pq) {
                    Recognition(
                            "" + it, if (labels.size > it) labels[it] else "unknown", outputs[it])
                }

        return (0 until Math.min(pq.size, MAX_RESULTS))
                .map {
                    pq.poll()
                }
    }

    private fun getPixelData(bitmap: Bitmap): FloatArray {
        val width = bitmap.width
        val height = bitmap.height

        // Get 28x28 pixel data from bitmap
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val retPixels = FloatArray(pixels.size)
        for (i in pixels.indices) {
            // Set 0 for white and 255 for black pixel
            val pix = pixels[i]
            val b = pix and 0xff
            retPixels[i] = ((0xff - b) / 255.0).toFloat()
        }

        return retPixels
    }

}