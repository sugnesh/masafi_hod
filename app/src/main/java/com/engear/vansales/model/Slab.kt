package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Slab : Parcelable {

    var planId: String? = null
    var fromQty: String? = null
    var toQty: String? = null
    var offerQty: String? = null
    var offerUOM: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        planId = `in`.readString()
        fromQty = `in`.readString()
        toQty = `in`.readString()
        offerQty = `in`.readString()
        offerUOM = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(planId)
        dest.writeString(fromQty)
        dest.writeString(toQty)
        dest.writeString(offerQty)
        dest.writeString(offerUOM)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Slab> = object : Parcelable.Creator<Slab> {
            override fun createFromParcel(`in`: Parcel): Slab {
                return Slab(`in`)
            }

            override fun newArray(size: Int): Array<Slab?> {
                return arrayOfNulls<Slab>(size)
            }
        }
    }
}