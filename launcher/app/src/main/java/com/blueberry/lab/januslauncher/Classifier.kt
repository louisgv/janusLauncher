package com.blueberry.lab.januslauncher

/**
 * Created by jojo on 8/23/17.
 */

import android.graphics.Bitmap

/**
 * Generic interface for interacting with different recognition engines.
 */
interface Classifier {
    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */

    fun recognizeImage(bitmap: Bitmap): List<Recognition>

    fun enableStatLogging(debug: Boolean)

    val statString: String

    fun close()
}
