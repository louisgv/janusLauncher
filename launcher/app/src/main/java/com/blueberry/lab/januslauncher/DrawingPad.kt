package com.blueberry.lab.januslauncher

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View


/**
 * Created by jojo on 8/11/17.
 */

class DrawingPad(context: Context) : View(context) {

    lateinit var bitmap : Bitmap

    private val resetInterval : Long = 1800

    private val resetRunnable = Runnable {
        run {
            reset()
        }
    }

    companion object StrokeUtils {
        private val MAXIMUM_STROKE = 9
        private var currentStrokeCount = 0

        fun addStroke(): Unit {
            currentStrokeCount++
        }

        fun shouldCleanUp(): Boolean {
            return currentStrokeCount > MAXIMUM_STROKE
        }

        fun reset(): Unit {
            currentStrokeCount = 0
        }
    }

    private val path = Path()

    private val paint = Paint()

    init {
        isClickable = false
        isFocusable = false
        isFocusableInTouchMode= false


        isDrawingCacheEnabled = true
        paint.color = Color.DKGRAY
        paint.isAntiAlias = true
        paint.strokeWidth = 18f
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.BEVEL
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }

    private fun reset(){
        StrokeUtils.reset()
        path.reset()
        postInvalidate()
    }

    fun onDrawing(event: MotionEvent?): Unit {
        val x = event!!.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)

                handler.removeCallbacks(resetRunnable)

                handler.postDelayed(resetRunnable, resetInterval)
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }

            else -> {
                // Get bitmap
                bitmap = Bitmap.createBitmap(drawingCache)

                StrokeUtils.addStroke()

                if (StrokeUtils.shouldCleanUp()) {
                    reset()
                }
            }
        }
        postInvalidate()
    }
}