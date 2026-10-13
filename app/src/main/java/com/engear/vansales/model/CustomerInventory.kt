package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class CustomerInventory : Parcelable {

    var uniqueId: String? = null
    var inventoryId: String? = null
    var customerId: String? = null
    var itemId: String? = null
    var itemName: String? = null
    var itemCode: String? = null
    var uomId: String? = null
    var uomName: String? = null
    var refillQty: String? = null
    var capacity: String? = null
    var fillQty: String? = null
    var reorderQty: String? = null
    var totalFillQty: String? = null
    var outOfStock: String? = null
    var itemCategory: String? = null
    var itemBrand: String? = null
    var itemGroup: String? = null
    var mUOMList: ArrayList<String> = ArrayList()
    var arrExpiry: MutableList<HashMap<String, String>>? = ArrayList()
    var arrDer: MutableList<HashMap<String, String>>? = ArrayList()

    constructor() {}
    protected constructor(`in`: Parcel) {
        uniqueId = `in`.readString()
        inventoryId = `in`.readString()
        customerId = `in`.readString()
        itemId = `in`.readString()
        itemName = `in`.readString()
        itemCode = `in`.readString()
        uomId = `in`.readString()
        uomName = `in`.readString()
        totalFillQty = `in`.readString()
        refillQty = `in`.readString()
        capacity = `in`.readString()
        fillQty = `in`.readString()
        reorderQty = `in`.readString()
        outOfStock = `in`.readString()
        itemCategory = `in`.readString()
        itemBrand = `in`.readString()
        itemGroup = `in`.readString()
        mUOMList = `in`.readList(null!!,null) as ArrayList<String>
        arrExpiry = `in`.readList(null!!,null) as ArrayList<HashMap<String, String>>
        arrDer = `in`.readList(null!!,null) as ArrayList<HashMap<String, String>>

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uniqueId)
        dest.writeString(inventoryId)
        dest.writeString(customerId)
        dest.writeString(itemId)
        dest.writeString(itemName)
        dest.writeString(itemCode)
        dest.writeString(uomId)
        dest.writeString(uomName)
        dest.writeString(totalFillQty)
        dest.writeString(refillQty)
        dest.writeString(capacity)
        dest.writeString(fillQty)
        dest.writeString(reorderQty)
        dest.writeString(outOfStock)
        dest.writeString(itemCategory)
        dest.writeString(itemBrand)
        dest.writeString(itemGroup)
        dest.writeList(mUOMList as List<*>?)
        dest.writeList(arrExpiry as List<*>?)
        dest.writeList(arrDer as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<CustomerInventory> =
            object : Parcelable.Creator<CustomerInventory> {
                override fun createFromParcel(`in`: Parcel): CustomerInventory {
                    return CustomerInventory(`in`)
                }

                override fun newArray(size: Int): Array<CustomerInventory?> {
                    return arrayOfNulls<CustomerInventory>(size)
                }
            }
    }
}