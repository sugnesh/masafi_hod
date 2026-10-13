package com.engear.vansales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.engear.vansales.R

class ComplaintPagerAdaptor(context: Context, arrData: ArrayList<String>) : PagerAdapter() {
    private val context: Context = context
    private var layoutInflater: LayoutInflater? = null
    private var arrData: ArrayList<String>? = null

    init {
        this.arrData = arrData
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return arrData!!.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.custom_layout, null)
        val imageView = view.findViewById(R.id.img_recommendedImage) as ImageView

        Glide.with(context)
            .load(arrData!![position])
            .into(imageView)

        //imageView.setImageURI(Uri.parse(arrData!![position]))
        val vp = container as ViewPager
        vp.addView(view, 0)
        /* imageView.setOnClickListener(object:View.OnClickListener() {
             fun onClick(v:View) {
                // PhotoFullPopupWindow(context, R.layout.popup_photo_full, view, arrData.get(position), null)
             }
         })*/
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}