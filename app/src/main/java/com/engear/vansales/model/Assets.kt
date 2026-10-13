package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Assets : Parcelable {

    var assetsId: String? = null
    var name: String? = null
    var customer: String? = null
    var uniqueId: String? = null
    var image: String? = null
    var assetsCode: String? = null
    var type: String? = null
    var feedBack: String? = null
    var postImages: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        assetsId = `in`.readString()
        name = `in`.readString()
        customer = `in`.readString()
        uniqueId = `in`.readString()
        image = `in`.readString()
        assetsCode = `in`.readString()
        type = `in`.readString()
        feedBack = `in`.readString()
        postImages = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(assetsId)
        dest.writeString(name)
        dest.writeString(customer)
        dest.writeString(uniqueId)
        dest.writeString(image)
        dest.writeString(assetsCode)
        dest.writeString(type)
        dest.writeString(feedBack)
        dest.writeString(postImages)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Assets> = object : Parcelable.Creator<Assets> {
            override fun createFromParcel(`in`: Parcel): Assets {
                return Assets(`in`)
            }

            override fun newArray(size: Int): Array<Assets?> {
                return arrayOfNulls<Assets>(size)
            }
        }
    }
}