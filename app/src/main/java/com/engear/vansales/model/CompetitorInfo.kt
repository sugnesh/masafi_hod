package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class CompetitorInfo : Parcelable {
    var complaintId: String? = null
    var company: String? = null
    var brand: String? = null
    var item: String? = null
    var price: String? = null
    var note: String? = null
    var images: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        complaintId = `in`.readString()
        company = `in`.readString()
        brand = `in`.readString()
        item = `in`.readString()
        price = `in`.readString()
        note = `in`.readString()
        images = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(complaintId)
        dest.writeString(company)
        dest.writeString(brand)
        dest.writeString(item)
        dest.writeString(price)
        dest.writeString(note)
        dest.writeString(images)

    }

    companion object {
        val CREATOR: Parcelable.Creator<CompetitorInfo> = object : Parcelable.Creator<CompetitorInfo> {
            override fun createFromParcel(`in`: Parcel): CompetitorInfo {
                return CompetitorInfo(`in`)
            }

            override fun newArray(size: Int): Array<CompetitorInfo?> {
                return arrayOfNulls<CompetitorInfo>(size)
            }
        }
    }
}