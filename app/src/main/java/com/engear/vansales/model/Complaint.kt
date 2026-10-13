package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Complaint : Parcelable {

    var complaintId: String? = null
    var title: String? = null
    var category: String? = null
    var item: String? = null
    var itemName: String? = null
    var categoryName: String? = null
    var customerId: String? = null
    var description: String? = null
    var status: String? = null
    var images: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        complaintId = `in`.readString()
        title = `in`.readString()
        category = `in`.readString()
        item = `in`.readString()
        itemName = `in`.readString()
        categoryName = `in`.readString()
        customerId = `in`.readString()
        description = `in`.readString()
        status = `in`.readString()
        images = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(complaintId)
        dest.writeString(title)
        dest.writeString(category)
        dest.writeString(item)
        dest.writeString(itemName)
        dest.writeString(categoryName)
        dest.writeString(images)
        dest.writeString(customerId)
        dest.writeString(description)
        dest.writeString(status)
        dest.writeString(images)

    }

    companion object {
        val CREATOR: Parcelable.Creator<Complaint> = object : Parcelable.Creator<Complaint> {
            override fun createFromParcel(`in`: Parcel): Complaint {
                return Complaint(`in`)
            }

            override fun newArray(size: Int): Array<Complaint?> {
                return arrayOfNulls<Complaint>(size)
            }
        }
    }
}