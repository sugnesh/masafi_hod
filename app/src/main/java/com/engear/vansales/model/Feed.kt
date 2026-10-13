package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Feed : Parcelable {

    var name: String? = null
    var address: String? = null
    var cust_id: String? = null
    var desc: String? = null
    var type = 0
    var date: String? = null
    var inv_no: String? = null
    var inv_type: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        address = `in`.readString()
        cust_id = `in`.readString()
        desc = `in`.readString()
        type = `in`.readInt()
        date = `in`.readString()
        inv_no = `in`.readString()
        inv_type = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(address)
        dest.writeString(cust_id)
        dest.writeString(desc)
        dest.writeInt(type)
        dest.writeString(date)
        dest.writeString(inv_no)
        dest.writeString(inv_type)

    }

    companion object {
        val CREATOR: Parcelable.Creator<Feed> = object : Parcelable.Creator<Feed> {
            override fun createFromParcel(`in`: Parcel): Feed {
                return Feed(`in`)
            }

            override fun newArray(size: Int): Array<Feed?> {
                return arrayOfNulls<Feed>(size)
            }
        }
    }
}