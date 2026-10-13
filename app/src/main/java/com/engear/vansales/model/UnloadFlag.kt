package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class UnloadFlag : Parcelable {
    var unloadType: String? = null
    var unloadFlag: String? = null
    var unloadTypeCode: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        unloadType = `in`.readString()
        unloadFlag = `in`.readString()
        unloadTypeCode = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(unloadType)
        dest.writeString(unloadFlag)
        dest.writeString(unloadTypeCode)
    }

    companion object {
        val CREATOR: Parcelable.Creator<UnloadFlag> = object : Parcelable.Creator<UnloadFlag> {
            override fun createFromParcel(`in`: Parcel): UnloadFlag {
                return UnloadFlag(`in`)
            }

            override fun newArray(size: Int): Array<UnloadFlag?> {
                return arrayOfNulls<UnloadFlag>(size)
            }
        }
    }
}