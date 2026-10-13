package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class SurveyPost : Parcelable {

    var surveyId: String? = null
    var uniqueId: String? = null
    var customerId: String? = null
    var customerName: String? = null
    var email: String? = null
    var phone: String? = null
    var surveyTypeId: String? = null
    var arrQuestion: ArrayList<Question>? = ArrayList()

    constructor() {}
    protected constructor(`in`: Parcel) {
        surveyId = `in`.readString()
        uniqueId = `in`.readString()
        customerId = `in`.readString()
        customerName = `in`.readString()
        email = `in`.readString()
        phone = `in`.readString()
        surveyTypeId = `in`.readString()
        arrQuestion = `in`.readList(null!!, null) as ArrayList<Question>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(surveyId)
        dest.writeString(uniqueId)
        dest.writeString(customerId)
        dest.writeString(customerName)
        dest.writeString(email)
        dest.writeString(phone)
        dest.writeString(surveyTypeId)
        dest.writeList(arrQuestion as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<SurveyPost> = object : Parcelable.Creator<SurveyPost> {
            override fun createFromParcel(`in`: Parcel): SurveyPost {
                return SurveyPost(`in`)
            }

            override fun newArray(size: Int): Array<SurveyPost?> {
                return arrayOfNulls<SurveyPost>(size)
            }
        }
    }
}