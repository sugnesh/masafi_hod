package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Campaign : Parcelable {

    var compaignId: String? = null
    var feedback: String? = null
    var customerId: String? = null
    var images: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        compaignId = `in`.readString()
        feedback = `in`.readString()
        customerId = `in`.readString()
        images = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(compaignId)
        dest.writeString(feedback)
        dest.writeString(customerId)
        dest.writeString(images)

    }

    companion object {
        val CREATOR: Parcelable.Creator<Campaign> = object : Parcelable.Creator<Campaign> {
            override fun createFromParcel(`in`: Parcel): Campaign {
                return Campaign(`in`)
            }

            override fun newArray(size: Int): Array<Campaign?> {
                return arrayOfNulls<Campaign>(size)
            }
        }
    }
}