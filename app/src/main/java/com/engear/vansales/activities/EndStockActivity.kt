package com.engear.vansales.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.UnloadItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityEndStockBinding
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils

class EndStockActivity : BaseActivity() {

    lateinit var binding: ActivityEndStockBinding
    private var strType: String = ""
    private var strRType: String = ""
    private var strUnloadType: String = ""
    var arrItemList: MutableList<UomData>? = ArrayList()

    companion object {
        var progressDialog: ProgressDialog? = null
        var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndStockBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@EndStockActivity,
            "On End Stock Screen "
        )
        init()
    }

    private fun init() {
        progressDialog = ProgressDialog(this)
        if (intent != null) {
            strType = intent.getStringExtra("Type")!!
        }

        if (strType == "End Inventory") {
            strUnloadType = "4"
            var unloadNo = db!!.getUnloadTypeNumber(strUnloadType)
            arrItemList = db!!.getUnloadDetail(strUnloadType, unloadNo)
        } else if (strType == "Fresh") {
            strUnloadType = "1"
            var unloadNo = db!!.getUnloadTypeNumber(strUnloadType)
            arrItemList = db!!.getUnloadDetail("1", unloadNo)
        } else if (strType == "Audit") {
            strUnloadType = "4"
            var unloadNo = db!!.getUnloadTypeNumber(strUnloadType)
            arrItemList = db!!.getUnloadDetail(strUnloadType, unloadNo)
        } else if (strType == "Bad Return") {
            strRType = "Return"
//            if (strRType == "Damage") {
//                arrItemList = db!!.getBadReturnDamageItem()
//            } else {
//                arrItemList = db!!.getBadReturnExpiryItem()
//            }

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                arrItemList = db!!.getBadReturnExpiryPickItem()
            } else {
                arrItemList = db!!.getBadReturnExpiryItem()
            }
        } else if (strType == "Variance") {
            strUnloadType = "7"
            var unloadNo = db!!.getUnloadTypeNumber(strUnloadType)
            arrItemList = db!!.getUnloadDetail("7", unloadNo)
        } else {
            strUnloadType = "2"
            arrItemList = db!!.getUnloadVarianceDetail()
        }

        setToolbarTitle(strType)
        setUpRecycler()

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = UnloadItemAdapter(this, arrItemList!!)
    }

}