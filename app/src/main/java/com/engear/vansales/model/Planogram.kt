package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Planogram : Parcelable {

    var distributionId: String? = null
    var planogramId: String? = null
    var toolName: String? = null
    var name: String? = null
    var customer: String? = null
    var uniqueId: String? = null
    var beforeImage: String? = null
    var afterImage: String? = null
    var comment: String? = null
    var arrImages: ArrayList<String> = ArrayList()

    constructor() {}
    protected constructor(`in`: Parcel) {
        distributionId = `in`.readString()
        planogramId = `in`.readString()
        toolName = `in`.readString()
        name = `in`.readString()
        beforeImage = `in`.readString()
        afterImage = `in`.readString()
        comment = `in`.readString()
        customer = `in`.readString()
        uniqueId = `in`.readString()
        arrImages = `in`.readList(null!!, null) as ArrayList<String>

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(planogramId)
        dest.writeString(distributionId)
        dest.writeString(name)
        dest.writeString(toolName)
        dest.writeString(customer)
        dest.writeString(uniqueId)
        dest.writeString(beforeImage)
        dest.writeString(afterImage)
        dest.writeString(comment)
        dest.writeList(arrImages as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Planogram> = object : Parcelable.Creator<Planogram> {
            override fun createFromParcel(`in`: Parcel): Planogram {
                return Planogram(`in`)
            }

            override fun newArray(size: Int): Array<Planogram?> {
                return arrayOfNulls<Planogram>(size)
            }
        }
    }
}