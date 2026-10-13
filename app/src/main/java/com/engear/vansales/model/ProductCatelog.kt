package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class ProductCatelog : Parcelable {
    var itemId: String? = null
    var itemCode: String? = null
    var itemName: String? = null
    var image: String? = null
    var netWeight: String? = null
    var uomId: String? = null
    var uomName: String? = null
    var price: String? = null
    var shelfLife: String? = null
    var flawer: String? = null
    var ingredients: String? = null
    var energy: String? = null
    var fat: String? = null
    var protein: String? = null
    var carbohydrate: String? = null
    var calcium: String? = null
    var sodium: String? = null
    var potassium: String? = null
    var crude_fibre: String? = null
    var vitamin: String? = null
    var image_string: String? = null
    var description: String? = null
    var isCart: String? = null
    var qty: String? = null
    var mUOMList: ArrayList<String> = ArrayList()
    var vatPercent: String? = null


    constructor() {}
    public constructor(`in`: Parcel) {
        var itemId= `in`.readString()
        var itemCode= `in`.readString()
        var itemName= `in`.readString()
        var image= `in`.readString()
        var netWeight= `in`.readString()
        var uomId= `in`.readString()
        var uomName= `in`.readString()
        var price= `in`.readString()
        var shelfLife= `in`.readString()
        var flawer= `in`.readString()
        var ingredients= `in`.readString()
        var energy= `in`.readString()
        var fat= `in`.readString()
        var protein= `in`.readString()
        var carbohydrate= `in`.readString()
        var calcium= `in`.readString()
        var sodium= `in`.readString()
        var potassium= `in`.readString()
        var crude_fibre= `in`.readString()
        var vitamin= `in`.readString()
        var image_string= `in`.readString()
        var description= `in`.readString()
        var isCart= `in`.readString()
        var qty= `in`.readString()
        var vatPercent= `in`.readString()
        mUOMList = `in`.readList(null!!,null) as ArrayList<String>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(itemId)
        dest.writeString(itemCode)
        dest.writeString(itemName)
        dest.writeString(image)
        dest.writeString(netWeight)
        dest.writeString(uomId)
        dest.writeString(uomName)
        dest.writeString(price)
        dest.writeString(shelfLife)
        dest.writeString(flawer)
        dest.writeString(ingredients)
        dest.writeString(energy)
        dest.writeString(fat)
        dest.writeString(protein)
        dest.writeString(carbohydrate)
        dest.writeString(calcium)
        dest.writeString(sodium)
        dest.writeString(potassium)
        dest.writeString(crude_fibre)
        dest.writeString(vitamin)
        dest.writeString(image_string)
        dest.writeString(description)
        dest.writeString(isCart)
        dest.writeString(qty)
        dest.writeString(vatPercent)
        dest.writeList(mUOMList as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<ProductCatelog> = object : Parcelable.Creator<ProductCatelog> {
            override fun createFromParcel(`in`: Parcel): ProductCatelog {
                return ProductCatelog(`in`)
            }

            override fun newArray(size: Int): Array<ProductCatelog?> {
                return arrayOfNulls<ProductCatelog>(size)
            }
        }
    }
}