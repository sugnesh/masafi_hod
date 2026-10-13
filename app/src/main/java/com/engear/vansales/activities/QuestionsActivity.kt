package com.engear.vansales.activities

import android.R
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.*
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityQuestionsBinding
import com.engear.vansales.utils.Utils

class QuestionsActivity : BaseActivity() {
    lateinit var binding: ActivityQuestionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuestionsBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()

        addTextViews()

        addCheckBoxes()

        addRadioButtons()

        addEditTexts()
    }

    private fun init() {
        setToolbarTitle("Questions")
        Utils.logData(
            this@QuestionsActivity,
            "On Question Screen "
        )
    }

    private fun addRadioButtons() {

        //RadioButtons are always added inside a RadioGroup
        val radioGroup = RadioGroup(this)
        radioGroup.orientation = LinearLayout.HORIZONTAL
        binding.linearLayout.addView(radioGroup)
        for (i in 1..3) {
            val radioButton = RadioButton(this)
            radioButton.text = "Option $i"
            radioGroup.addView(radioButton)
            setRadioButtonAttributes(radioButton)
        }
        addLineSeperator()
    }

    private fun addTextViews() {
        //Adding a LinearLayout with HORIZONTAL orientation
        val textLinearLayout = LinearLayout(this)
        textLinearLayout.orientation = LinearLayout.HORIZONTAL
        binding.linearLayout.addView(textLinearLayout)
        for (i in 1..3) {
            val textView = TextView(this)
            textView.text = "TextView $i"
            setTextViewAttributes(textView)
            textLinearLayout.addView(textView)
        }
        addLineSeperator()
    }

    private fun addCheckBoxes() {
        val checkBoxLayout = LinearLayout(this)
        checkBoxLayout.orientation = LinearLayout.VERTICAL
        binding.linearLayout.addView(checkBoxLayout)
        for (i in 1..3) {
            val checkBox = CheckBox(this)
            checkBox.text = "CheckBox $i"
            setCheckBoxAttributes(checkBox)
            checkBoxLayout.addView(checkBox)
        }
        addLineSeperator()
    }

    private fun addEditTexts() {
        val editTextLayout = LinearLayout(this)
        editTextLayout.orientation = LinearLayout.VERTICAL
        binding.linearLayout.addView(editTextLayout)
        for (i in 1..3) {
            val editText = EditText(this)
            editText.hint = "EditText $i"
            setEditTextAttributes(editText)
            editTextLayout.addView(editText)
        }
        addLineSeperator()
    }

    private fun setEditTextAttributes(editText: EditText) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            0
        )
        editText.layoutParams = params
    }

    private fun setCheckBoxAttributes(checkBox: CheckBox) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            0
        )
        checkBox.layoutParams = params

        //This is used to place the checkbox on the right side of the textview
        //By default, the checkbox is placed at the left side
        val typedValue = TypedValue()
        theme.resolveAttribute(
            R.attr.listChoiceIndicatorMultiple,
            typedValue, true
        )
        checkBox.buttonDrawable = null
        checkBox.setCompoundDrawablesWithIntrinsicBounds(
            0, 0,
            typedValue.resourceId, 0
        )
    }

    private fun setTextViewAttributes(textView: TextView) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            0, 0
        )
        textView.setTextColor(Color.BLACK)
        textView.layoutParams = params
    }

    private fun setRadioButtonAttributes(radioButton: RadioButton) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(
            convertDpToPixel(16f),
            convertDpToPixel(16f),
            0, 0
        )
        radioButton.layoutParams = params
    }

    //This function to convert DPs to pixels
    private fun convertDpToPixel(dp: Float): Int {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

    private fun addLineSeperator() {
        val lineLayout = LinearLayout(this)
        lineLayout.setBackgroundColor(Color.GRAY)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        )
        params.setMargins(0, convertDpToPixel(10f), 0, convertDpToPixel(10f))
        lineLayout.layoutParams = params
        binding.linearLayout.addView(lineLayout)
    }
}