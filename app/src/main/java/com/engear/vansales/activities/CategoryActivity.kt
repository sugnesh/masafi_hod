package com.engear.vansales.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityCategoryBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.merchandiser.DistributionListActivity
import com.engear.vansales.merchandiser.InventoryCheckActivity
import com.engear.vansales.model.Category
import com.engear.vansales.model.CustomerData
import com.engear.vansales.utils.Utils

class CategoryActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityCategoryBinding
    var customerCode: String? = null
    private var customerObj: CustomerData.DataCustomer? = null
    var strType: String = ""
    var strSubType: String = ""
    var brandId: String = ""
    var headBrandId: String = ""
    private var allData: ArrayList<Category> = ArrayList()
    var mAdapter: CategoryAdapter? = null

    companion object {
        var isRePlanishmentCompleted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@CategoryActivity,
            "On Category Screen "
        )
        init()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Category Screen"
        )
        strType = intent.getStringExtra("Type")!!
        strSubType = intent.getStringExtra("SubType")!!

//        if (strSubType == "SubBrand") {
//            headBrandId = intent.getStringExtra("HeadBrand")
//        }

        brandId = intent.getStringExtra("BrandId")!!
        customerCode = intent.getStringExtra("customer_code")
        headBrandId = intent.getStringExtra("HeadBrand")!!
        customerObj = db!!.getCustomerDetail(customerCode!!)

        if (strType == "Display Tool") {
            setToolbarTitle("Display Tool Categories")
        } else if (strType == "Inventory") {
            setToolbarTitle("Inventory Categories")
        } else {
            setToolbarTitle("Replenishment Categories")
        }

        getData()
    }

    private fun getData() {
        allData = ArrayList()
        if (strType == "Inventory") {
            allData = db!!.getCustomerInvCategoryList(
                customerObj!!.userDetail.id.toString(),
                brandId,
                headBrandId,
                "0"
            )
        }else  if (strType == "Display Tool") {
            allData = db!!.getCustomerDisplayCategoryList(
                customerObj!!.userDetail.id.toString(),
                brandId,
                headBrandId,
                "0"
            )
        } else {
//            allData = db!!.getCustomerReplanishCategoryList(
//                customerObj!!.userDetail.id.toString(),
//                brandId,
//                headBrandId,
//                "0"
//            )
        }
        setAdapter()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun setAdapter() {
        binding.rvList.layoutManager = GridLayoutManager(applicationContext, 2)
        mAdapter = CategoryAdapter(this, allData)
        binding.rvList.adapter = mAdapter
    }

    override fun onItemsClick(position: Int, tag: String) {
        Utils.logData(
            me,
            "Select " + allData[position].name
        )
        if (db!!.checkCategorySubCategoryExist(allData[position].categoryId.toString())) {
//            startActivity(Intent(me, SubCategoryActivity::class.java).apply {
//                this.putExtra("customer_code", customerCode)
//                this.putExtra("Type", strType)
//                this.putExtra("BrandId", brandId)
//                this.putExtra("HeadBrand", headBrandId)
//                this.putExtra("SubType", strSubType)
//                this.putExtra("categoryId", allData[position].categoryId.toString())
//            })
        } else {
            if (strType == "Display Tool") {
                startActivity(Intent(me, DistributionListActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("CategoryId", allData[position].categoryId)
                    this.putExtra("BrandId", brandId)
                    this.putExtra("HeadBrandId", brandId)
                    this.putExtra("HeadCategoryId", allData[position].categoryId)
                })
            } else if (strType == "Inventory") {
                startActivity(Intent(me, InventoryCheckActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("CategoryId", allData[position].categoryId)
                    this.putExtra("BrandId", brandId)
                    this.putExtra("HeadBrandId", brandId)
                    this.putExtra("HeadCategoryId", allData[position].categoryId)
                })
            } else {
                //showDialog(allData[position])
            }
        }
    }

//    private fun showDialog(mCategory: Category) {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(true)
//        val binding: DialogReplishmentMenuBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(this),
//            R.layout.dialog_replishment_menu,
//            null,
//            false
//        )
//        dialog.setContentView(binding.root)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val metrics = resources.displayMetrics
//        val screenWidth = (metrics.widthPixels * 0.95).toInt()
//        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//        binding.tvCategory.text = mCategory.name
//
//        var isSOS = false
////        if (db!!.checkSOSCustomerPosted(
////                customerObj!!.userDetail.id.toString(),
////                brandId, mCategory.categoryId.toString()
////            )
////        ) {
////            isSOS = true
////            binding.checkSOS.alpha = 1.0f
////        } else {
////            isSOS = false
////            binding.checkSOS.alpha = 0.5f
////        }
////
////        var isSOD = false
////        if (db!!.checkSODCustomerPosted(
////                customerObj!!.userDetail.id.toString(),
////                brandId, mCategory.categoryId.toString()
////            )
////        ) {
////            isSOD = true
////            binding.checkSOD.alpha = 1.0f
////        } else {
////            isSOD = false
////            binding.checkSOD.alpha = 0.5f
////        }
////
////        var isSOA = false
////        if (db!!.checkSOACustomerPosted(
////                customerObj!!.userDetail.id.toString(),
////                brandId, mCategory.categoryId.toString()
////            )
////        ) {
////            isSOA = true
////            binding.checkSOA.alpha = 1.0f
////        } else {
////            isSOA = false
////            binding.checkSOA.alpha = 0.5f
////        }
////
////        var isPrice = false
////        if (db!!.checkPriceCustomerPosted(
////                customerObj!!.userDetail.id.toString(),
////                brandId, mCategory.categoryId.toString()
////            )
////        ) {
////            isPrice = true
////            binding.checkPrice.alpha = 1.0f
////        } else {
////            isPrice = false
////            binding.checkPrice.alpha = 0.5f
////        }
////
////        binding.viewPriceCheck.setOnClickListener {
////            startActivity(Intent(me, PriceCheckActivity::class.java).apply {
////                this.putExtra("customer_code", customerCode)
////                this.putExtra("CategoryId", mCategory.categoryId)
////                this.putExtra("BrandId", brandId)
////                this.putExtra("SubType", strSubType)
////                this.putExtra("HeadBrand", headBrandId)
////                this.putExtra("HeadCategoryId",mCategory.categoryId)
////            })
////            dialog.dismiss()
////        }
////
////        binding.viewSOACheck.setOnClickListener {
////            if (isPrice) {
////                startActivity(Intent(me, SOACheckActivity::class.java).apply {
////                    this.putExtra("customer_code", customerCode)
////                    this.putExtra("CategoryId", mCategory.categoryId)
////                    this.putExtra("BrandId", brandId)
////                    this.putExtra("SubType", strSubType)
////                })
////                dialog.dismiss()
////            }
////        }
////
////        binding.viewSODCheck.setOnClickListener {
////            if (isSOA) {
////                startActivity(Intent(me, SODCheckActivity::class.java).apply {
////                    this.putExtra("customer_code", customerCode)
////                    this.putExtra("CategoryId", mCategory.categoryId)
////                    this.putExtra("BrandId", brandId)
////                    this.putExtra("SubType", strSubType)
////                })
////                dialog.dismiss()
////            }
////        }
////
////        binding.viewSOSCheck.setOnClickListener {
////            if (isSOD) {
////                startActivity(Intent(me, SOSCheckActivity::class.java).apply {
////                    this.putExtra("customer_code", customerCode)
////                    this.putExtra("CategoryId", mCategory.categoryId)
////                    this.putExtra("BrandId", brandId)
////                    this.putExtra("SubType", strSubType)
////                })
////                dialog.dismiss()
////            }
////        }
////
////        binding.viewPromotion.setOnClickListener {
////            if (isSOS) {
////                startActivity(Intent(me, PromotionActivity::class.java).apply {
////                    this.putExtra("customer_code", customerCode)
////                    this.putExtra("CategoryId", mCategory.categoryId)
////                    this.putExtra("BrandId", brandId)
////                })
////                dialog.dismiss()
////            }
////        }
//
//        binding.btnConfirm.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
}