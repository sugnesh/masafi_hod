package com.engear.vansales.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.OrderRTCRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySelectUnloadBinding
import com.engear.vansales.interfaces.OnUnloadItemClickListener
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class SelectUnloadActivity : BaseActivity(), OnUnloadItemClickListener {

    lateinit var binding: ActivitySelectUnloadBinding
    var allItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var saleItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    private val intentCode = 100
    var mType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivitySelectUnloadBinding.inflate(layoutInflater, baseBinding.frameContainer, true)
        me = this
        init()
    }

    private fun init() {
        Utils.logData(
            this@SelectUnloadActivity,
            "On fresh inload Screen "
        )
        setToolbarTitle("Fresh Unload")

        allItemsList = db!!.getAllSaleItem()
        setUpRecycler()

        mType = intent.getStringExtra("Type")!!

        binding.btnDone.setOnClickListener {
            Utils.logData(
                this@SelectUnloadActivity,
                "On done button click"
            )
            saleItemsList!!.clear()

            for (i in allItemsList!!) {
                if (i.itemSelectUOM != null) {
                    if (i.itemSelectUOM!!.isNotEmpty()) {
                        saleItemsList!!.add(i)
                    }
                }
            }

            if (saleItemsList!!.isNotEmpty()) {
                val returnIntent = Intent()
                returnIntent.putExtra("ItemList", Gson().toJson(saleItemsList))
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            } else {
                Toast.makeText(this, "Please Add Item!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = OrderRTCRequestItemAdapter(this, allItemsList!!)
    }

    fun getItemPosition(itemId: Int, isResale: String): Int {

        var position = 0

        for (mItem in allItemsList!!) {
            if (mItem.id == itemId) {
                if (isResale == mItem.itemWeight) {
                    position = allItemsList!!.indexOf(mItem)
                    break
                }
            }
        }
        return position
    }

    private fun openQuantityDialog() {
        if (allItemsList!![itemPosition].itemUOM == null)
            allItemsList!![itemPosition].itemUOM = ArrayList()

        startActivityForResult(
            Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                this.putExtra("item_code", allItemsList!![itemPosition].itemCode)
                this.putExtra("item_id", allItemsList!![itemPosition].id.toString())
                this.putExtra("cust_id", "")
                this.putExtra(
                    "uom_list",
                    Gson().toJson(allItemsList!![itemPosition].itemSelectUOM)
                )
                this.putExtra(
                    "available_qty",
                    "0"
                )
                this.putExtra(
                    "available_uom",
                    allItemsList!![itemPosition].vanUOM
                )
                this.putExtra("type", "Unload")
                this.putExtra("unloadType", mType)
                this.putExtra(
                    "display_uom",
                    allItemsList!![itemPosition].displayUnitId
                )
                this.putExtra("lobCode", "")
                this.putExtra("isReSale", allItemsList!![itemPosition].itemWeight)
            },
            intentCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {

                allItemsList!![itemPosition].itemSelectUOM = emptyList()

                val strObj = data!!.getStringExtra("uomList")
                val listOfUomType: Type =
                    object : TypeToken<List<UomData?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                allItemsList!![itemPosition].itemSelectUOM = mList

                binding.rvList.adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onUnloadItemsClick(position: Int, tag: String, isResale: String) {
        if (tag == "dialog") {
            itemPosition = getItemPosition(position, isResale)
            openQuantityDialog()
        }
    }

}