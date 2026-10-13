package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Item : Parcelable {
    var itemId: String? = null
    var itemCode: String? = null
    var itemName: String? = null
    var itemUOMId: String? = null
    var itemUOMName: String? = null
    var itemBatch: String? = null
    var itemPrice: String? = null
    var itemQty: String? = null
    var itemCatId: String? = null
    var itemDisplayUnitId: String? = null
    var itemDisplayUnitName: String? = null
    var itemDisplayUPC: String? = null
    var itemUOMUPC: String? = null
    var itemIsReSale: String? = null
    var itemWHQty: String? = null
    var itemSPQty: String? = null
    var itemVarianceQty: String? = null
    var promoId: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        itemId = `in`.readString()
        itemCode = `in`.readString()
        itemName = `in`.readString()
        itemUOMId = `in`.readString()
        itemUOMName = `in`.readString()
        itemBatch = `in`.readString()
        itemPrice = `in`.readString()
        itemQty = `in`.readString()
        itemCatId = `in`.readString()
        itemDisplayUnitId = `in`.readString()
        itemDisplayUnitName = `in`.readString()
        itemDisplayUPC = `in`.readString()
        itemUOMUPC = `in`.readString()
        itemIsReSale = `in`.readString()
        itemWHQty = `in`.readString()
        itemSPQty = `in`.readString()
        itemVarianceQty = `in`.readString()
        promoId = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(itemId)
        dest.writeString(itemCode)
        dest.writeString(itemName)
        dest.writeString(itemUOMId)
        dest.writeString(itemUOMName)
        dest.writeString(itemBatch)
        dest.writeString(itemPrice)
        dest.writeString(itemQty)
        dest.writeString(itemCatId)
        dest.writeString(itemDisplayUnitId)
        dest.writeString(itemDisplayUnitName)
        dest.writeString(itemDisplayUPC)
        dest.writeString(itemUOMUPC)
        dest.writeString(itemIsReSale)
        dest.writeString(promoId)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Item> = object : Parcelable.Creator<Item> {
            override fun createFromParcel(`in`: Parcel): Item {
                return Item(`in`)
            }

            override fun newArray(size: Int): Array<Item?> {
                return arrayOfNulls<Item>(size)
            }
        }
    }
}