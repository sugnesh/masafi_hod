package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Survey : Parcelable {

    var uniqueId: String? = null
    var surveyId: String? = null
    var surveyName: String? = null
    var surveyType: String? = null
    var customerId: String? = null
    var toolId: String? = null
    var assetId: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        uniqueId = `in`.readString()
        surveyId = `in`.readString()
        customerId = `in`.readString()
        surveyName = `in`.readString()
        surveyType = `in`.readString()
        toolId = `in`.readString()
        assetId = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uniqueId)
        dest.writeString(surveyId)
        dest.writeString(customerId)
        dest.writeString(surveyName)
        dest.writeString(surveyType)
        dest.writeString(toolId)
        dest.writeString(assetId)

    }

    companion object {
        val CREATOR: Parcelable.Creator<Survey> = object : Parcelable.Creator<Survey> {
            override fun createFromParcel(`in`: Parcel): Survey {
                return Survey(`in`)
            }

            override fun newArray(size: Int): Array<Survey?> {
                return arrayOfNulls<Survey>(size)
            }
        }
    }
}