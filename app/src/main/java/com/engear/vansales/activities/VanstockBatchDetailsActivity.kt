package com.engear.vansales.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.HighLightArrayAdapter
import com.engear.vansales.adapter.InputDialogUomAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.ActivityInputOrderQtyDialogBinding
import com.engear.vansales.databinding.ActivityVanBatchDetailsDialogBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.*
import com.engear.vansales.utils.DBUtils.mPreferenceManager
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class VanstockBatchDetailsActivity : Activity() {
    lateinit var binding: ActivityVanBatchDetailsDialogBinding

    var db: DatabaseHandler? = null
    var allLoadBatchItem: MutableList<BatchdetailsList> = mutableListOf()
    var id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setFinishOnTouchOutside(false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_van_batch_details_dialog)

        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.90).toInt()
        val screenHeight = (metrics.heightPixels * 0.90).toInt()

        window.setLayout(screenWidth, screenHeight)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        db = DatabaseHandler(this)
        Utils.logData(
            this@VanstockBatchDetailsActivity,
            "On Input order qty dialog Screen "
        )
        init()

    }

    private fun init() {

        mPreferenceManager = SharedPreferenceManager(this)
        id = intent.getStringExtra("id")
        allLoadBatchItem= db!!.getAllVanBatchItemByID(mItemID = id.toString())


        binding.btnClose.setOnClickListener {
            finish()
        }



       // setUpRecycler()

    }



    private fun setUpRecycler() {

       /* binding.rvBatchList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBatchList.adapter = InputDialogUomAdapter(this, allLoadBatchItem)*/
    }


}