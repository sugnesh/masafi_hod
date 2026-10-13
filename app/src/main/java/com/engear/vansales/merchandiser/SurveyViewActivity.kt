package com.engear.vansales.merchandiser

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivitySurveyViewBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Question
import com.engear.vansales.model.SurveyPost
import com.engear.vansales.model.Transaction
import com.engear.vansales.utils.Utils


class SurveyViewActivity : BaseActivity() {

    lateinit var binding: ActivitySurveyViewBinding
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var mType: String? = null
    var assetId: String? = ""
    var toolId: String? = ""
    var surveyId: String? = null
    var surveyTypeId: String? = null
    var surveyName: String? = null
    var allAnswer: ArrayList<Question> = ArrayList()
    var allData: ArrayList<Question> = ArrayList()
    private val allViewInstance: ArrayList<View> = ArrayList()
    var surveyPost: SurveyPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyViewBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {

        customerCode = intent.getStringExtra("customer_code")
        mType = intent.getStringExtra("Type")
        surveyId = intent.getStringExtra("surveyId")
        surveyTypeId = intent.getStringExtra("surveyTypeId")
        surveyName = intent.getStringExtra("surveyName")
        if (customerCode != null)
            customerObj = db!!.getCustomerDetail(customerCode!!)

        setToolbarTitle(surveyName!!)

        if (mType == "Tool") {
            assetId = intent.getStringExtra("assetId")
        } else if (mType == "Asset") {
            toolId = intent.getStringExtra("toolId")
        }

        allData = ArrayList()
        allData = db!!.getSurveyQuestion(surveyId!!)
        setQuestionData()

    }

    private fun setClickEvent() {
        binding.btnSubmit.setOnClickListener {

            val alertDialogBuilder =
                android.app.AlertDialog.Builder(this@SurveyViewActivity)
            alertDialogBuilder.setTitle("Alert")
                .setMessage("Are you sure you want to submit this survey?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    addSurvey()
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }

            alertDialogBuilder.show()
        }
    }

    private fun addSurvey() {


        if (mType == "Tool") {

            getAnswer()

            if (allAnswer.size > 0) {

                var mComplaint = SurveyPost()
                mComplaint.uniqueId = Utils.getSurveyNo(mPreferenceManager!!.getUserId()!!)
                mComplaint.surveyId = surveyId
                mComplaint.surveyTypeId = surveyTypeId
                mComplaint.customerId = customerObj!!.userDetail.id.toString()
                mComplaint.arrQuestion = allAnswer
                mComplaint.customerName = ""
                mComplaint.email = ""
                mComplaint.phone = ""
                db!!.insertSurveyPost(mComplaint)

                //INSERT TRANSACTION
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_DISTRIBUTION_SURVEY
                transaction.tr_date_time =
                    Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerCode
                transaction.tr_customer_name =
                    customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = mComplaint.uniqueId
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = ""
                transaction.tr_printData = ""
                transaction.tr_is_posted = "No"

                db!!.insertTransaction(transaction)

            }

            db!!.updateCustomerTransaction(
                customerObj!!.userDetail!!.id.toString(),
                "merchandising"
            )

            finish()
        } else if (mType == "Asset") {

            getAnswer()

            if (allAnswer.size > 0) {

                var mComplaint = SurveyPost()
                mComplaint.uniqueId = Utils.getSurveyNo(mPreferenceManager!!.getUserId()!!)
                mComplaint.surveyId = surveyId
                mComplaint.surveyTypeId = surveyTypeId
                mComplaint.customerId = customerObj!!.userDetail.id.toString()
                mComplaint.arrQuestion = allAnswer
                mComplaint.customerName = ""
                mComplaint.email = ""
                mComplaint.phone = ""
                db!!.insertSurveyPost(mComplaint)

                //INSERT TRANSACTION
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ASSETS_SURVEY
                transaction.tr_date_time =
                    Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerCode
                transaction.tr_customer_name =
                    customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = mComplaint.uniqueId
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = ""
                transaction.tr_printData = ""
                transaction.tr_is_posted = "No"

                db!!.insertTransaction(transaction)

            }

            db!!.updateCustomerTransaction(
                customerObj!!.userDetail!!.id.toString(),
                "merchandising"
            )

            finish()
        } else if (mType == "Consumer") {

            getAnswer()

            if (allAnswer.size > 0) {

                var mComplaint = SurveyPost()
                mComplaint.uniqueId = Utils.getSurveyNo(mPreferenceManager!!.getUserId()!!)
                mComplaint.surveyId = surveyId
                mComplaint.surveyTypeId = surveyTypeId
                mComplaint.customerId = customerObj!!.userDetail.id.toString()
                mComplaint.arrQuestion = allAnswer
                mComplaint.customerName = ""
                mComplaint.email = ""
                mComplaint.phone = ""
                db!!.insertSurveyPost(mComplaint)

                //INSERT TRANSACTION
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_CONS_SURVEY
                transaction.tr_date_time =
                    Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = customerCode
                transaction.tr_customer_name =
                    customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = ""
                transaction.tr_order_id = mComplaint.uniqueId
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = ""
                transaction.tr_printData = ""
                transaction.tr_is_posted = "No"

                db!!.insertTransaction(transaction)

            }

            db!!.updateCustomerTransaction(
                customerObj!!.userDetail!!.id.toString(),
                "merchandising"
            )

            finish()

        } else {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.sensory_customer)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val metrics = resources.displayMetrics
            val screenWidth = (metrics.widthPixels * 0.95).toInt()
            dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            val btnSubmit = dialog.findViewById(R.id.btnSubmit) as TextView
            val edtCustomerName = dialog.findViewById(R.id.edtCustomerName) as EditText
            val edtCustomerEmail = dialog.findViewById(R.id.edtCustomerEmail) as EditText
            val edtCustomerPhone = dialog.findViewById(R.id.edtCustomerPhone) as EditText
            val edtCustomerAddress = dialog.findViewById(R.id.edtCustomerAddress) as EditText

            btnSubmit.setOnClickListener {

                if (edtCustomerName.text.toString().isEmpty()) {
                    edtCustomerName.requestFocus()
                    edtCustomerName.error = "Enter Customer Name"
                } else if (edtCustomerEmail.text.toString().isEmpty()) {
                    edtCustomerEmail.requestFocus()
                    edtCustomerEmail.error = "Enter Customer Email"
                } else if (edtCustomerPhone.text.toString().isEmpty()) {
                    edtCustomerPhone.requestFocus()
                    edtCustomerPhone.error = "Enter Customer Phone"
                } else {

                    getAnswer()

                    if (allAnswer.size > 0) {

                        var mComplaint = SurveyPost()
                        mComplaint.uniqueId = Utils.getSurveyNo(mPreferenceManager!!.getUserId()!!)
                        mComplaint.surveyId = surveyId
                        mComplaint.surveyTypeId = surveyTypeId
                        mComplaint.customerId = "0"
                        mComplaint.arrQuestion = allAnswer
                        mComplaint.customerName = edtCustomerName.text.toString()
                        mComplaint.email = edtCustomerEmail.text.toString()
                        mComplaint.phone = edtCustomerPhone.text.toString()
                        db!!.insertSurveyPost(mComplaint)

                        //INSERT TRANSACTION
                        val transaction = Transaction()
                        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_SENSURY_SURVEY
                        transaction.tr_date_time =
                            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                        transaction.tr_customer_num = ""
                        transaction.tr_customer_name = ""
                        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                        transaction.tr_invoice_id = ""
                        transaction.tr_order_id = mComplaint.uniqueId
                        transaction.tr_collection_id = ""
                        transaction.tr_pyament_id = ""
                        transaction.tr_printData = ""
                        transaction.tr_is_posted = "No"

                        db!!.insertTransaction(transaction)

                    }
                    finish()
                }
            }

            dialog.show()
        }

    }

    private fun getAnswer() {

        allAnswer = ArrayList()

        var i = 0
        for (mQuestion in allData) {
            if (mQuestion.questionType == "radio") {
                val radioGroup = allViewInstance[i] as RadioGroup
                val selectedRadioBtn =
                    findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                Log.d("variant_name", selectedRadioBtn.tag.toString() + "")
                val quest = Question()
                quest.questionId = mQuestion.questionId
                quest.surveyId = surveyId
                quest.answer = selectedRadioBtn.tag.toString()
                allAnswer.add(quest)
            } else if (mQuestion.questionType == "checkbox") {
                val lytView = allViewInstance[i] as LinearLayout
                var anse = ""
                for (j in 0 until lytView.childCount) {
                    val nextChild = lytView.getChildAt(j)
                    if (nextChild is CheckBox) {
                        if ((nextChild as CheckBox).isChecked) {
                            Log.d("variant_name", (nextChild as CheckBox).getTag().toString() + "")
                            if (anse.equals("", ignoreCase = true)) {
                                anse = (nextChild as CheckBox).getTag().toString()
                            } else {
                                anse = anse + "," + (nextChild as CheckBox).getTag().toString()
                            }
                        }
                    }
                }
                val quest = Question()
                quest.questionId = mQuestion.questionId
                quest.surveyId = surveyId
                quest.answer = anse
                allAnswer.add(quest)
            } else if (mQuestion.questionType == "select") {
                val tempEditBox = allViewInstance[i] as Spinner
                Log.d("variant_name", tempEditBox.selectedItem.toString() + "")
                val quest = Question()
                quest.questionId = mQuestion.questionId
                quest.surveyId = surveyId
                quest.answer = tempEditBox.selectedItem.toString()
                allAnswer.add(quest)
            } else if (mQuestion.questionType == "text") {
                val tempEditBox = allViewInstance[i] as EditText
                Log.d("variant_name", tempEditBox.text.toString() + "")
                val quest = Question()
                quest.questionId = mQuestion.questionId
                quest.surveyId = surveyId
                quest.answer = tempEditBox.text.toString()
                allAnswer.add(quest)
            } else if (mQuestion.questionType == "textarea") {
                val tempEditBox = allViewInstance[i] as EditText
                Log.d("variant_name", tempEditBox.text.toString() + "")
                val quest = Question()
                quest.questionId = mQuestion.questionId
                quest.surveyId = surveyId
                quest.answer = tempEditBox.text.toString()
                allAnswer.add(quest)
            }
            i++
        }
    }

    private fun setQuestionData() {
        var index = 0
        for (mQuestion in allData) {

            val vi =
                applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v: View = vi.inflate(R.layout.row_stock_questions, null)
            val textView = v.findViewById(R.id.tvQuestion) as TextView
            val layoutOptions = v.findViewById(R.id.layout_options) as LinearLayout
            textView.text = mQuestion.title

            if (mQuestion.questionType == "radio") {
                val rg = RadioGroup(this)
                allViewInstance.add(rg)
                var index = 0
                for (mValue in mQuestion.arrValue!!) {
                    val rb = RadioButton(this)
                    rg.addView(rb)
                    if (index == 0)
                        rb.setChecked(true)
                    rb.setTag(mValue)
                    rb.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    rb.setText(mValue)
                    index++
                }

                layoutOptions.addView(rg)
            } else if (mQuestion.questionType == "checkbox") {
                for (mValue in mQuestion.arrValue!!) {
                    val chk = CheckBox(this)
                    chk.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    chk.setTag(mValue)
                    chk.setText(mValue)
                    chk.setOnCheckedChangeListener { buttonView, isChecked ->
                        chk.isChecked = isChecked
                    }
                    layoutOptions.addView(chk)
                }
                allViewInstance.add(layoutOptions)
            } else if (mQuestion.questionType == "select") {
                var spinnerArrayAdapter: ArrayAdapter<String>? = null
                spinnerArrayAdapter = ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    mQuestion.arrValue!!
                )
                val p = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val spinner = Spinner(this)
                spinner.setAdapter(spinnerArrayAdapter)
                spinner.setSelection(0, false)
                spinner.setLayoutParams(p)
                allViewInstance.add(spinner)
                layoutOptions.addView(spinner)
            } else if (mQuestion.questionType == "text") {
                val et = EditText(this)
                val p = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                et.setLayoutParams(p)
                et.setImeOptions(EditorInfo.IME_ACTION_DONE)
                allViewInstance.add(et)
                layoutOptions.addView(et)
            } else if (mQuestion.questionType == "textarea") {
                val p = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val et = EditText(this)
                et.setLayoutParams(p)
                allViewInstance.add(et)
                layoutOptions.addView(et)
            }

            binding.rvList.addView(v)
            index++
        }
    }
}