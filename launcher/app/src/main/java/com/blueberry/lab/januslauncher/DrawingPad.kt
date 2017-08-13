package com.blueberry.lab.januslauncher

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

/**
 * Created by jojo on 8/11/17.
 */

class DrawingPad(context: Context) : View(context) {

    val resetInterval : Long = 1800

    val resetRunnable = Runnable {
        run {
            reset()
        }
    }

    companion object StrokeUtils {
        val MAXIMUM_STROKE = 9
        var currentStrokeCount = 0

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

    val path = Path()

    val paint = Paint()

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

    fun reset(){
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
                StrokeUtils.addStroke()

                if (StrokeUtils.shouldCleanUp()) {
                    reset()
                }
            }
        }
        postInvalidate()
    }
}