package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class DistributionItem : Parcelable {

    var distributionId: String? = null
    var customerId: String? = null
    var itemId: String? = null
    var itemName: String? = null
    var itemCode: String? = null
    var itembarcode: String? = null
    var uomId: String? = null
    var uomName: String? = null
    var capacity: String? = null
    var saleable: String? = null
    var toFill: String? = null
    var categoryId: String? = null
    var brand: String? = null
    var group: String? = null
    var outOfStock: String? = null
    var totalFacing: String? = null
    var actualFacing: String? = null
    var score: String? = null
    var arrDer: MutableList<HashMap<String, String>>? = ArrayList()
    var arrExp: MutableList<HashMap<String, String>>? = ArrayList()

    constructor() {}
    protected constructor(`in`: Parcel) {
        distributionId = `in`.readString()
        customerId = `in`.readString()
        itemId = `in`.readString()
        itembarcode = `in`.readString()
        itemName = `in`.readString()
        itemCode = `in`.readString()
        uomId = `in`.readString()
        uomName = `in`.readString()
        capacity = `in`.readString()
        saleable = `in`.readString()
        toFill = `in`.readString()
        categoryId = `in`.readString()
        brand = `in`.readString()
        group = `in`.readString()
        outOfStock = `in`.readString()
        totalFacing = `in`.readString()
        actualFacing = `in`.readString()
        score = `in`.readString()
        arrDer = `in`.readList(null!!,null) as ArrayList<HashMap<String, String>>
        arrExp = `in`.readList(null!!,null) as ArrayList<HashMap<String, String>>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(distributionId)
        dest.writeString(customerId)
        dest.writeString(itemId)
        dest.writeString(itembarcode)
        dest.writeString(itemName)
        dest.writeString(itemCode)
        dest.writeString(uomId)
        dest.writeString(uomName)
        dest.writeString(capacity)
        dest.writeString(saleable)
        dest.writeString(toFill)
        dest.writeString(categoryId)
        dest.writeString(brand)
        dest.writeString(group)
        dest.writeString(outOfStock)
        dest.writeString(totalFacing)
        dest.writeString(actualFacing)
        dest.writeString(score)
        dest.writeList(arrDer as List<*>?)
        dest.writeList(arrExp as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<DistributionItem> = object : Parcelable.Creator<DistributionItem> {
            override fun createFromParcel(`in`: Parcel): DistributionItem {
                return DistributionItem(`in`)
            }

            override fun newArray(size: Int): Array<DistributionItem?> {
                return arrayOfNulls<DistributionItem>(size)
            }
        }
    }
}