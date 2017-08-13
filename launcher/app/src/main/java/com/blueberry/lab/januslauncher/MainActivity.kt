package com.blueberry.lab.januslauncher

import android.os.Bundle
import android.support.v4.app.FragmentActivity

import android.view.MotionEvent
import android.widget.RelativeLayout

class MainActivity : FragmentActivity() {

    lateinit var pad : DrawingPad

    val padParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pad = DrawingPad(this.applicationContext)
        addContentView(pad, padParams)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        pad.onDrawing(event)

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                super.dispatchTouchEvent(event)
                return false
            }
        }

        return super.dispatchTouchEvent(event)
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
