package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class SupervisorCategory : Parcelable {

    var categoryId: String? = null
    var category: String? = null


    constructor() {}
    protected constructor(`in`: Parcel) {
        categoryId = `in`.readString()
        category = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(categoryId)
        dest.writeString(category)
    }

    companion object {
        val CREATOR: Parcelable.Creator<SupervisorCategory> =
            object : Parcelable.Creator<SupervisorCategory> {
                override fun createFromParcel(`in`: Parcel): SupervisorCategory {
                    return SupervisorCategory(`in`)
                }

                override fun newArray(size: Int): Array<SupervisorCategory?> {
                    return arrayOfNulls<SupervisorCategory>(size)
                }
            }
    }
}