package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Category : Parcelable {

    var name: String? = null
    var categoryId: String? = null
    var isPosted: Boolean = false

    constructor() {}
    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        categoryId = `in`.readString()
        isPosted = `in`.readBoolean()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(categoryId)
        dest.writeBoolean(isPosted)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category> {
            override fun createFromParcel(`in`: Parcel): Category {
                return Category(`in`)
            }

            override fun newArray(size: Int): Array<Category?> {
                return arrayOfNulls<Category>(size)
            }
        }
    }
}