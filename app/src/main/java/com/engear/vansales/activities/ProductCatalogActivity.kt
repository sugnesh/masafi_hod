package com.engear.vansales.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration
import com.engear.vansales.R
import com.engear.vansales.adapter.ProductAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityProductCatalogBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.CartActivity
import com.engear.vansales.merchandiser.ProductDetailActivity
import com.engear.vansales.model.ProductCatelog
import com.engear.vansales.utils.Utils


class ProductCatalogActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityProductCatalogBinding
    private var allData: ArrayList<ProductCatelog> = ArrayList()
    var mAdapter: ProductAdapter? = null
    var textCartItemCount: TextView? = null
    var mCartItemCount = 0

    companion object {
        var isOrderCreated = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductCatalogBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@ProductCatalogActivity,
            "On Product catelog Screen "
        )
        setToolbarTitle("Product Catalog")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        allData = ArrayList()
        allData = db!!.getAllProductCatelogList()
        setAdapter()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product, menu)

        val menuItem = menu!!.findItem(R.id.action_cart)

        val actionView: View = menuItem.actionView!!
        textCartItemCount = actionView.findViewById(R.id.cart_badge)

        setupBadge()

        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(me, CartActivity::class.java))
                return true
            }
            android.R.id.home -> {
                return true
            }
        }
        return false
    }

    private fun setupBadge() {
        if (textCartItemCount != null) {
            mCartItemCount = db!!.geCartCount().toInt()
            if (mCartItemCount == 0) {
                if (textCartItemCount!!.visibility != View.GONE) {
                    textCartItemCount!!.visibility = View.GONE
                }
            } else {
                textCartItemCount!!.text = Math.min(mCartItemCount, 99).toString()
                if (textCartItemCount!!.visibility != View.VISIBLE) {
                    textCartItemCount!!.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setAdapter() {
        binding.rvList.layoutManager = GridLayoutManager(applicationContext, 2)
        binding.rvList.setItemAnimator(DefaultItemAnimator())
        val horizontalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        val verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider)
        binding.rvList.addItemDecoration(
            GridDividerItemDecoration(
                horizontalDivider,
                verticalDivider,
                2
            )
        )
        mAdapter = ProductAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {

        if (tag == "cart") {
            var isCart = "0"
            var mData = allData[position]
            if (allData[position].isCart == "0") {
                mCartItemCount++
                isCart = "1"
                db!!.insertCart(allData[position])
            } else {
                mCartItemCount--
                isCart = "0"
                db!!.deleteCartItem(allData[position].itemId!!)
            }
            mData.isCart = isCart
            allData[position] = mData
            setupBadge()
            mAdapter!!.notifyDataSetChanged()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var intent = Intent(me, ProductDetailActivity::class.java)
                intent.putExtra("product", allData[position].itemId)
                startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
            } else {
                startActivity(Intent(me, ProductDetailActivity::class.java).apply {
                    this.putExtra("product", allData[position].itemId)
                })
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (isOrderCreated) {
            isOrderCreated = false
            setupBadge()
            allData = ArrayList()
            allData = db!!.getAllProductCatelogList()
            setAdapter()
        }
    }
}