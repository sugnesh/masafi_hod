package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Distribution : Parcelable {

    var distributionId: String? = null
    var name: String? = null
    var customer: String? = null
    var uniqueId: String? = null
    var isPosted: Boolean = false

    constructor() {}
    protected constructor(`in`: Parcel) {
        distributionId = `in`.readString()
        name = `in`.readString()
        customer = `in`.readString()
        uniqueId = `in`.readString()
        isPosted = `in`.readBoolean()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(distributionId)
        dest.writeString(name)
        dest.writeString(customer)
        dest.writeString(uniqueId)
        dest.writeBoolean(isPosted)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Distribution> = object : Parcelable.Creator<Distribution> {
            override fun createFromParcel(`in`: Parcel): Distribution {
                return Distribution(`in`)
            }

            override fun newArray(size: Int): Array<Distribution?> {
                return arrayOfNulls<Distribution>(size)
            }
        }
    }
}