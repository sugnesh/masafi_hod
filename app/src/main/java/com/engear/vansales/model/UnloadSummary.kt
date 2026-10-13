package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class UnloadSummary : Parcelable {


    var itemCode: String? = null
    var name: String? = null
    var uom: String? = null
    var loadQty: String? = null
    var salesQty: String? = null
    var returnQty: String? = null
    var variance: String? = null
    var unloadQty: String? = null
    var freshQty: String? = null
    var expiryhQty: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        itemCode = `in`.readString()
        name = `in`.readString()
        uom = `in`.readString()
        loadQty = `in`.readString()
        salesQty = `in`.readString()
        returnQty = `in`.readString()
        variance = `in`.readString()
        unloadQty = `in`.readString()
        freshQty = `in`.readString()
        expiryhQty = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(itemCode)
        dest.writeString(name)
        dest.writeString(uom)
        dest.writeString(loadQty)
        dest.writeString(salesQty)
        dest.writeString(returnQty)
        dest.writeString(variance)
        dest.writeString(unloadQty)
        dest.writeString(freshQty)
        dest.writeString(expiryhQty)
    }

    companion object {
        val CREATOR: Parcelable.Creator<UnloadSummary> = object : Parcelable.Creator<UnloadSummary> {
            override fun createFromParcel(`in`: Parcel): UnloadSummary {
                return UnloadSummary(`in`)
            }

            override fun newArray(size: Int): Array<UnloadSummary?> {
                return arrayOfNulls<UnloadSummary>(size)
            }
        }
    }
}