package com.engear.vansales.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.engear.vansales.adapter.FilterTypeAdapter
import com.engear.vansales.adapter.FilterTypeDataAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityFilterBinding
import com.engear.vansales.interfaces.OnFilterClickListener
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.utils.Utils

class FilterActivity : BaseActivity(), OnItemClickListener, OnFilterClickListener {
    lateinit var binding: ActivityFilterBinding
    var typeList: ArrayList<String> = ArrayList()
    var typeDataList: ArrayList<HashMap<String, String>> = ArrayList()
    var selectedPosition: Int = 0
    var mType: String = ""
    var mTag: String = ""

    companion object {
        var selectedList: ArrayList<HashMap<String, String>> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@FilterActivity,
            "On Filter Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Select Filter")

        mType = intent.getStringExtra("Type")!!

        if (mType == "Item") {
            typeList.add("Category")
            typeList.add("Brand")
            typeList.add("Item Group")
            mTag = "Category"
            typeDataList.clear()
            typeDataList = db!!.getItemCategory()
            checkSelected()
        } else {
            typeList.add("Region")
            typeList.add("Sales Org.")
            typeList.add("Channel")
            typeList.add("Payment Term")
            mTag = "Region"
            typeDataList.clear()
            typeDataList = db!!.getRegionList()
        }

        setUpRecycler()

        if (selectedList.isNotEmpty()) {
            binding.btnClear.visibility = View.VISIBLE
        }
        binding.btnApply.setOnClickListener {
            Utils.logData(
                this@FilterActivity,
                "On Button Apply click  "
            )
            if (selectedList.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please apply any one filter",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val returnIntent = Intent()
                returnIntent.putExtra("itemList", Gson().toJson(selectedList))
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        binding.btnClear.setOnClickListener {
            Utils.logData(
                this@FilterActivity,
                "On clear button click"
            )
            val returnIntent = Intent()
            returnIntent.putExtra("isClear", "true")
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }

    private fun setUpRecycler() {
        binding.rvFilterTypeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterTypeList.adapter = FilterTypeAdapter(this, typeList, selectedPosition)

        binding.rvFilterTypeDataList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterTypeDataList.adapter = FilterTypeDataAdapter(this, typeDataList)

    }

    override fun onItemsClick(position: Int, tag: String) {
        selectedPosition = position
        binding.rvFilterTypeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterTypeList.adapter = FilterTypeAdapter(this, typeList, selectedPosition)

        typeDataList.clear()

        mTag = tag
        if (tag == "Category") {
            typeDataList = db!!.getItemCategory()
        } else if (tag == "Brand") {
            typeDataList = db!!.getItemBrand()
        } else if (tag == "Item Group") {
            typeDataList = db!!.getItemGroup()
        } else if (tag == "Region") {
            typeDataList = db!!.getRegionList()
        } else if (tag == "Sales Org.") {
            typeDataList = db!!.getSalesORGList()
        } else if (tag == "Channel") {
            typeDataList = db!!.getChannelList()
        } else if (tag == "Payment Term") {
            typeDataList = db!!.getPaymentTermList()
        } else {

        }

        checkSelected()

        binding.rvFilterTypeDataList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvFilterTypeDataList.adapter = FilterTypeDataAdapter(this, typeDataList)

    }

    fun checkSelected() {
        for (mTypeData in typeDataList) {
            var index = typeDataList.indexOf(mTypeData)
            for (mData in selectedList) {
                if (mTag == mData["Type"]) {
                    if (mTypeData["Id"] == mData["Id"]) {
                        mTypeData["isSelect"] = "true"
                        break
                    }
                }
            }

            typeDataList.set(index, mTypeData)
        }

    }

    override fun onFilterClick(position: Int, mId: String, tag: Boolean) {

        var isContain = false
        var index = 0

        for (mData in selectedList) {

            if (mData["Id"] == mId) {
                isContain = true
                index = selectedList.indexOf(mData)
                break
            }
        }

        if (isContain) {
            selectedList.removeAt(index)
        } else {
            selectedList.add(typeDataList[position])
        }

        if (selectedList.isNotEmpty()) {
            binding.btnClear.visibility = View.VISIBLE
        } else {
            binding.btnClear.visibility = View.GONE
        }
    }
}