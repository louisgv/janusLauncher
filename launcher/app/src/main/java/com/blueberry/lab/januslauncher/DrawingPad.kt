package com.blueberry.lab.januslauncher

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View






/**
 * Created by jojo on 8/11/17.
 */

class DrawingPad(context: Context, val modelSize: Int = 28, val resetInterval: Long = 1800) : View(context) {

    private val canvasMatrix = Matrix()
    private val invertedCanvasMatrix = Matrix()
    private val scaleMatrix = Matrix()

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

    private val renderPath = Path()
    private val renderPaint = Paint()

    private val processingPath = Path()
    private val processingPaint = Paint()


    init {
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode = false

        isDrawingCacheEnabled = true

        renderPaint.color = Color.DKGRAY
        renderPaint.isAntiAlias = true
        renderPaint.strokeWidth = 18f
        renderPaint.style = Paint.Style.STROKE
        renderPaint.strokeJoin = Paint.Join.BEVEL
        renderPaint.strokeCap = Paint.Cap.ROUND

        processingPaint.color = Color.BLACK
        processingPaint.isAntiAlias = true
        processingPaint.strokeWidth = 1f
        processingPaint.style = Paint.Style.STROKE
        processingPaint.strokeJoin = Paint.Join.BEVEL
        processingPaint.strokeCap = Paint.Cap.ROUND
    }

    fun getBitmap() : Bitmap {
        val bitmap = Bitmap.createBitmap(modelSize, modelSize, Bitmap.Config.ARGB_8888)
        val processingCanvas = Canvas(bitmap)

        scaleMatrix.setScale(modelSize /width.toFloat(), modelSize /height.toFloat(), 0f, 0f)

        processingPath.transform(scaleMatrix)

        processingCanvas.save()
        processingCanvas.drawColor(Color.WHITE)
        processingCanvas.drawPath(processingPath, processingPaint)
        processingCanvas.restore()

        processingPath.reset()

        return bitmap
    }

    override fun onDraw(renderCanvas: Canvas) {
        renderCanvas.drawPath(renderPath, renderPaint)
        super.onDraw(renderCanvas)
    }

    private fun reset() {
        StrokeUtils.reset()
        renderPath.reset()
        postInvalidate()
    }

    fun onDrawing(event: MotionEvent?) {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderPath.moveTo(x, y)

                handler.removeCallbacks(resetRunnable)

                handler.postDelayed(resetRunnable, resetInterval)
            }

            MotionEvent.ACTION_MOVE -> {
                renderPath.lineTo(x, y)
            }

            else -> {
                // Get bitmap
                StrokeUtils.addStroke()

                processingPath.set(renderPath)

//                val rectF = RectF()
//                renderPath.computeBounds(rectF, true)
//                scaleMatrix.setScale(modelSize/rectF.width(), modelSize/rectF.height(), 0f, 0f)
//                renderPath.transform(scaleMatrix)

                if (StrokeUtils.shouldCleanUp()) {
                    reset()
                }
            }
        }
        postInvalidate()
    }
}