package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Brand : Parcelable {

    var name: String? = null
    var barndId: String? = null
    var isPosted: Boolean = false

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        barndId = `in`.readString()
        isPosted = `in`.readBoolean()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(barndId)
        dest.writeBoolean(isPosted)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Brand> = object : Parcelable.Creator<Brand> {
            override fun createFromParcel(`in`: Parcel): Brand {
                return Brand(`in`)
            }

            override fun newArray(size: Int): Array<Brand?> {
                return arrayOfNulls<Brand>(size)
            }
        }
    }
}