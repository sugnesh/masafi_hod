package com.engear.vansales.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.R
import com.engear.vansales.adapter.LoadOrderItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityLoadRequestBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.Order
import com.engear.vansales.utils.Utils

class LoadRequestActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityLoadRequestBinding
    private var orderList: ArrayList<Order>? = ArrayList()
    private var mAdapter : LoadOrderItemAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadRequestBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@LoadRequestActivity,
            "On Load request Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Load Request")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@LoadRequestActivity,
                "On Add button click"
            )
            startActivity(Intent(this@LoadRequestActivity, OrderRequestActivity::class.java).apply {
                this.putExtra(
                    "customer_code",
                    mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.depotId
                )
                this.putExtra("type", "load")
            })
        }

    }

    private fun getAllOrder() {
        orderList = ArrayList()
        orderList = db!!.getAllLoadOrder()

        setUpRecycler()
    }

    private fun setUpRecycler() {
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = LoadOrderItemAdapter(this, orderList)
        binding.rvItemList.adapter =mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        startActivity(Intent(this, OrderDetailActivity::class.java).apply {
            this.putExtra("order_no", orderList!![position].orderNo)
            this.putExtra("type", "load")
            this.putExtra("isAccept", orderList!![position].isDamaged)
        })
    }

    override fun onResume() {
        super.onResume()
        getAllOrder()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_customer, menu)
        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu!!.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu!!.findItem(R.id.action_search).icon = drawable

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

}