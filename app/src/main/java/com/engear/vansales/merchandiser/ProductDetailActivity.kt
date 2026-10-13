package com.engear.vansales.merchandiser

import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityProductDetailBinding
import com.engear.vansales.model.ProductCatelog

class ProductDetailActivity : BaseActivity() {

    lateinit var binding: ActivityProductDetailBinding
    public var mProduct: ProductCatelog? = null
    private var productId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {

        productId = intent.getStringExtra("product")
        mProduct = db!!.getProductDetail(productId!!)
        setToolbarTitle(mProduct!!.itemName!!)

        Glide.with(this).load(mProduct!!.image_string)
            .into(binding.ivImage)
        binding.item = mProduct
        binding.executePendingBindings()
    }
}