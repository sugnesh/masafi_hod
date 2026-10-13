package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class CategoryData : Parcelable {

    var catId: String? = null
    var name: String? = null
    var isClick: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        catId = `in`.readString()
        name = `in`.readString()
        isClick = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(catId)
        dest.writeString(name)
        dest.writeString(isClick)
    }

    companion object {
        val CREATOR: Parcelable.Creator<CategoryData> = object : Parcelable.Creator<CategoryData> {
            override fun createFromParcel(`in`: Parcel): CategoryData {
                return CategoryData(`in`)
            }

            override fun newArray(size: Int): Array<CategoryData?> {
                return arrayOfNulls<CategoryData>(size)
            }
        }
    }
}