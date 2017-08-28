package com.blueberry.lab.januslauncher

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View


/**
 * Created by jojo on 8/11/17.
 */

class DrawingPad(context: Context) : View(context) {
    private val RESET_INTERVAL: Long = 1800

    private val MODEL_SIZE = 28

    private val canvasMatrix = Matrix()
    private val invertedCanvasMatrix = Matrix()

    private val resetRunnable = Runnable {
        run {
            reset()
        }
    }

    companion object StrokeUtils {
        private val MAXIMUM_STROKE = 9
        private var currentStrokeCount = 0

        fun addStroke() {
            currentStrokeCount++
        }

        fun shouldCleanUp(): Boolean {
            return currentStrokeCount > MAXIMUM_STROKE
        }

        fun reset() {
            currentStrokeCount = 0
        }
    }

    private val path = Path()

    private val paint = Paint()

    init {
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode = false

        isDrawingCacheEnabled = true

        paint.color = Color.DKGRAY
        paint.isAntiAlias = true
        paint.strokeWidth = 18f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.BEVEL
        paint.strokeCap = Paint.Cap.ROUND

        val width = width.toFloat()
        val height = height.toFloat()

        // Model (bitmap) size
        val modelWidth = MODEL_SIZE
        val modelHeight = MODEL_SIZE

        val scaleW = width / modelWidth
        val scaleH = height / modelHeight

        var scale = scaleW
        if (scale > scaleH) {
            scale = scaleH
        }

        val newCx = modelWidth * scale / 2
        val newCy = modelHeight * scale / 2
        val dx = width / 2 - newCx
        val dy = height / 2 - newCy

        canvasMatrix.setScale(scale, scale)
        canvasMatrix.postTranslate(dx, dy)
        canvasMatrix.invert(invertedCanvasMatrix)
    }

    fun getBitmap() : Bitmap {
        val bitmap = Bitmap.createBitmap(MODEL_SIZE, MODEL_SIZE, Bitmap.Config.ARGB_8888)
        val processingCanvas = Canvas(bitmap)
        processingCanvas.drawPath(path, paint)
        return bitmap
    }

    override fun onDraw(renderCanvas: Canvas) {
        renderCanvas.drawPath(path, paint)
    }

    private fun reset() {
        StrokeUtils.reset()
        path.reset()
        postInvalidate()
    }

    fun onDrawing(event: MotionEvent?) {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)

                handler.removeCallbacks(resetRunnable)

                handler.postDelayed(resetRunnable, RESET_INTERVAL)
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }

            else -> {
                // Get bitmap
                StrokeUtils.addStroke()

                if (StrokeUtils.shouldCleanUp()) {
                    reset()
                }
            }
        }
        postInvalidate()
    }
}