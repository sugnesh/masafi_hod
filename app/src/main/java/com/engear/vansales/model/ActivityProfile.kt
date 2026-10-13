package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class ActivityProfile : Parcelable {

    var name: String? = null
    var status: String? = null
    var priority: String? = null
    var isCompleted: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        status = `in`.readString()
        priority = `in`.readString()
        isCompleted = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(status)
        dest.writeString(priority)
        dest.writeString(isCompleted)

    }

    companion object {
        val CREATOR: Parcelable.Creator<ActivityProfile> = object : Parcelable.Creator<ActivityProfile> {
            override fun createFromParcel(`in`: Parcel): ActivityProfile {
                return ActivityProfile(`in`)
            }

            override fun newArray(size: Int): Array<ActivityProfile?> {
                return arrayOfNulls<ActivityProfile>(size)
            }
        }
    }
}