package com.engear.vansales.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.OrderRTCRequestItemAdapter
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityAddMoreItemBinding
import com.engear.vansales.databinding.ActivitySelectUnloadBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.UomData
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.HashMap

class AddMoreItemActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityAddMoreItemBinding
    var allItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var saleItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var itemPosition = -1
    private val intentCode = 100
    private val filterCode = 200
    var mAdapter: OrderRequestItemAdapter? = null
    var customerCode: String? = null
    var customerObj: CustomerData.DataCustomer? = null
    var customerPortItem = ""
    var mType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityAddMoreItemBinding.inflate(layoutInflater, baseBinding.frameContainer, true)
        me = this
        init()
    }

    private fun init() {
        Utils.logData(
            this@AddMoreItemActivity,
            "On AddMore Screen "
        )
        setToolbarTitle("Select Item")

        mType = intent.getStringExtra("type")!!
        customerCode = intent.getStringExtra("customer_code")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        if (mType == "sales") {
            allItemsList = db!!.getAllFreshSaleItem()
        } else {
            allItemsList = db!!.getAllItem()
        }
        setUpRecycler()

        binding.btnDone.setOnClickListener {
            Utils.logData(
                this@AddMoreItemActivity,
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
        mAdapter = OrderRequestItemAdapter(this, allItemsList!!,"")
        binding.rvList.adapter = mAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_load, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu!!.findItem(R.id.action_search)) as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)
        val searchEditText =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.hint = "Search"

        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)

        val searchIcon =
            searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon
            .setImageResource(R.drawable.ic_search)

        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.imageTintList =
            ColorStateList.valueOf(color)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                mAdapter!!.filter!!.filter(s)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = getItemPosition(position)
            openQuantityDialog()
        }
    }

    fun getItemPosition(itemId: Int): Int {
        var position = 0
        for (mItem in allItemsList!!) {
            if (mItem.id == itemId) {
                position = allItemsList!!.indexOf(mItem)
                break
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
                this.putExtra("cust_id", customerObj!!.userDetail.id.toString())
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
                this.putExtra(
                    "display_uom",
                    allItemsList!![itemPosition].displayUnitId
                )
                this.putExtra("type", "sales")
                this.putExtra("lobCode", "")
                this.putExtra("isCoupon", "0")
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
        } else if (requestCode == filterCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data!!.getStringExtra("isClear") != null) {
                    allItemsList!!.clear()
                    allItemsList = db!!.getAllFreshSaleItem()
                    setUpRecycler()
                } else {
                    val strObj = data!!.getStringExtra("itemList")
                    val listOfUomType: Type =
                        object : TypeToken<List<HashMap<String, String>?>?>() {}.type

                    var mList =
                        Gson().fromJson(strObj, listOfUomType) as ArrayList<HashMap<String, String>>

                    var brand = ""
                    var category = ""
                    var itemGroup = ""
                    for (mDat in mList) {
                        if (mDat["Type"] == "Brand") {
                            if (brand.isEmpty()) {
                                brand = mDat["Id"]!!
                            } else {
                                brand = brand + "," + mDat["Id"]!!
                            }
                        } else if (mDat["Type"] == "Category") {
                            if (category.isEmpty()) {
                                category = mDat["Id"]!!
                            } else {
                                category = category + "," + mDat["Id"]!!
                            }
                        } else if (mDat["Type"] == "Item Group") {
                            if (itemGroup.isEmpty()) {
                                itemGroup = mDat["Id"]!!
                            } else {
                                itemGroup = itemGroup + "," + mDat["Id"]!!
                            }
                        }
                    }

                    allItemsList!!.clear()
                    setUpRecycler()
                }
            }
        }
    }
}