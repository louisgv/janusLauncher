package com.blueberry.lab.januslauncher

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.RelativeLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class MainActivity : FragmentActivity() {

    private val MODEL_SIZE = 28
    private val RESET_INTERVAL = 1800L
    private val INPUT_SIZE = 224
    private val IMAGE_MEAN = 117
    private val IMAGE_STD = 1f
    private val INPUT_NAME = "batch_normalization_1/keras_learning_phase"
    private val OUTPUT_NAME = "main_output/Softmax"

    private val MODEL_FILE = "file:///android_asset/graph.pb"
    private val LABEL_FILE = "file:///android_asset/labels.txt"

    lateinit var pad: DrawingPad
    lateinit var appListFragment: AppListFragment
    lateinit var appListQueryEdit: EditText

    private val stackOfFilteredListOfAppModels = Stack<List<AppModel>>()

    private val padParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
    )

    private lateinit var classifier : TensorFlowEMNISTClassifier

    private var previousQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pad = DrawingPad(this.applicationContext, MODEL_SIZE, RESET_INTERVAL)
        addContentView(pad, padParams)

        appListFragment = fragmentManager.findFragmentById(R.id.app_list) as AppListFragment

        appListQueryEdit = findViewById(R.id.app_list_query_edit)

        appListQueryEdit.addTextChangedListener { onQueryChanged(it) }

        classifier = TensorFlowEMNISTClassifier(
                assets,
                MODEL_FILE,
                LABEL_FILE,
                INPUT_SIZE,
                IMAGE_MEAN,
                IMAGE_STD,
                INPUT_NAME,
                OUTPUT_NAME
        )
    }

    private fun onQueryChanged(query: String) {
        if (previousQuery.isEmpty() && stackOfFilteredListOfAppModels.isEmpty()) {
            stackOfFilteredListOfAppModels.push(appListFragment.listOfAppModels)
        }

        val filteredListOfAppModels = if (previousQuery.length < query.length) {
            stackOfFilteredListOfAppModels.push(
                    stackOfFilteredListOfAppModels.peek()
                        .filter { appModel -> appModel.label.contains(query, ignoreCase = true) }
                        .sortedWith(compareBy { appModel -> -appModel.label.indexOf(query) })
            )
        } else {
            stackOfFilteredListOfAppModels.pop()
            stackOfFilteredListOfAppModels.peek()
        }

        previousQuery = query

        appListFragment.appListAdapter.setData(filteredListOfAppModels)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        pad.onDrawing(event)

        val originalResult = super.dispatchTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                return false
            }

            MotionEvent.ACTION_UP -> {
                doAsync {
                    val results = classifier.recognizeImage(pad.getBitmap())

                    val bestPredicted = results[0].title
                    uiThread {
                        onQueryChanged(previousQuery + bestPredicted)
                    }
                }
            }
        }

        return originalResult
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
