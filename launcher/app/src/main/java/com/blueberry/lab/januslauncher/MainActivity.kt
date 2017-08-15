package com.blueberry.lab.januslauncher

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher

import android.view.MotionEvent
import android.widget.EditText
import android.widget.RelativeLayout

class MainActivity : FragmentActivity() {

    lateinit var pad : DrawingPad
    lateinit var appListFragment : AppListFragment
    lateinit var appListQueryEdit : EditText

    val padParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pad = DrawingPad(this.applicationContext)
        addContentView(pad, padParams)

        appListFragment = fragmentManager.findFragmentById(R.id.app_list) as AppListFragment

        appListQueryEdit = findViewById(R.id.app_list_query_edit)

        appListQueryEdit.addTextChangedListener {onQueryChanged(it)}
    }

    fun onQueryChanged(filterString: String): Unit {
        val filteredListOfAppModels = AppsLoader(appListFragment.activity).listOfAppModels
                .filter { appModel -> appModel.label.contains(filterString, ignoreCase = true) }

        appListFragment.appListAdapter.setData(filteredListOfAppModels)
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

private fun EditText.addTextChangedListener(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
