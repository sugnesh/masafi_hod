package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.DivisionItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityDivisionItemBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.DailyFeildRequestModel
import com.engear.vansales.model.SupervisorCategory
import com.google.gson.Gson


class DivisionItemActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityDivisionItemBinding
    private var lobId = ""
    private var customerid = ""
    private var routeId = ""

    private var dailyDataCustomer: DailyFeildRequestModel.Customer =
        DailyFeildRequestModel.Customer()
    private var itemList: ArrayList<SupervisorCategory>? = null
    private var requestitemList: ArrayList<DailyFeildRequestModel.Customer.DailyActivityDetail>? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_division_item)
        binding = ActivityDivisionItemBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )



        init()
    }

    fun init() {
        setToolbarTitle("Items")
        requestitemList = ArrayList()
        itemList = ArrayList()
        lobId = intent.getStringExtra("lobId")!!
        routeId = intent.getStringExtra("routeId")!!
        customerid = intent.getStringExtra("customerid")!!
        var data = db!!.getDailyFeild(customerid, routeId)
        if (data != null) {
            Log.e("dailyfeild", Gson().toJson(data))
            setData()
        }
        getCategoryItemData()

        binding.btnSubmit.setOnClickListener {

            setModelData()


            val intentMessage = Intent()
            intentMessage.putExtra("dailydata", dailyDataCustomer)
            setResult(11, intentMessage)
            finish()

        }
        dailyDataCustomer!!.offShelfDisplay = "poor"
        binding.rgOffShelfDisplay.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbPoorOSd -> {
                    dailyDataCustomer!!.offShelfDisplay = "poor"
                }
                R.id.rbgoodOSd -> {
                    dailyDataCustomer!!.offShelfDisplay = "good"
                }
                R.id.rbexceOSd -> {
                    dailyDataCustomer!!.offShelfDisplay = "excellent"
                }
            }
        }
        dailyDataCustomer!!.shelfDisplay = "poor"
        binding.rgShelfDisplay.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbPoorSd -> {
                    dailyDataCustomer!!.shelfDisplay = "poor"
                }
                R.id.rbgoodSd -> {
                    dailyDataCustomer!!.shelfDisplay = "good"
                }
                R.id.rbexceSd -> {
                    dailyDataCustomer!!.shelfDisplay = "excellent"
                }


            }
        }

    }

    private fun setData() {
        var data = db!!.getDailyFeild(customerid, routeId)

        binding.etRemark.setText(data!!.remarks)

        binding.etOppertunite.setText(data!!.opportunity)
        binding!!.etOutoffstock.setText(data!!.outOfStock)


        when (data.offShelfDisplay) {
            "poor" -> {
                binding.rbPoorOSd.isChecked = true
            }
            "good" -> {
                binding.rbgoodOSd.isChecked = true
            }
            "excellent" -> {
                binding.rbexceOSd.isChecked = true

            }


        }
      when (data.shelfDisplay) {
          "poor" -> {
              binding.rbPoorSd.isChecked = true
          }
          "good" -> {
              binding.rbgoodSd.isChecked = true
          }
          "excellent" -> {
              binding.rbexceSd.isChecked = true

          }
        }


    }

    private fun getCategoryItemData() {
        itemList = ArrayList()
        itemList = db!!.getSPCategory(lobId)

        setAdapter()

    }

    private fun setModelData() {

        dailyDataCustomer!!.customerId = customerid.toInt()
        dailyDataCustomer!!.remarks = binding.etRemark.text.toString()

        dailyDataCustomer!!.opportunity = binding.etOppertunite.text.toString()
        dailyDataCustomer!!.outOfStock = binding!!.etOutoffstock.text.toString()
        dailyDataCustomer!!.dailyActivityDetails = requestitemList!!
        db!!.insertDailyFeild(dailyDataCustomer, routeId)
//        Log.e("dailyfeildss", Gson().toJson(dailyDataCustomer))

    }

    private fun setAdapter() {
        binding.recyclerViewLoadItem.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewLoadItem.adapter = DivisionItemAdapter(this, itemList!!, this)

    }

    override fun onItemsClick(position: Int, tag: String) {

        try {
            Log.e("groupdata", "$position-- $tag")
            var data = DailyFeildRequestModel.Customer.DailyActivityDetail(
                itemList!![position].categoryId!!.toInt(),
                tag.toInt()
            )
            if (requestitemList!!.size > 0) {
                for (i in requestitemList!!.indices) {
                    if (requestitemList!![i].supervisorCategoryId == data.supervisorCategoryId) {
                        requestitemList!!.removeAt(i)
                        break
                    }
                }
                requestitemList!!.add(data)
            } else {
                requestitemList!!.add(
                    data
                )
            }
        } catch (e: Exception) {
            e.toString()
        }

    }
}