package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class DistributionImage : Parcelable {

    var distributionId: String? = null
    var images: String? = null
    var customer: String? = null
    var uniqueId: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        distributionId = `in`.readString()
        images = `in`.readString()
        customer = `in`.readString()
        uniqueId = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(distributionId)
        dest.writeString(images)
        dest.writeString(customer)
        dest.writeString(uniqueId)
    }

    companion object {
        val CREATOR: Parcelable.Creator<DistributionImage> = object : Parcelable.Creator<DistributionImage> {
            override fun createFromParcel(`in`: Parcel): DistributionImage {
                return DistributionImage(`in`)
            }

            override fun newArray(size: Int): Array<DistributionImage?> {
                return arrayOfNulls<DistributionImage>(size)
            }
        }
    }
}