package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class VisitedList : Parcelable {
    var customerId: String? = null
    var customerCode: String? = null
    var startTime: String? = null
    var endTime: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var name: String? = null
    var status: String? = null
    var reason: String? = null
    var comment: String? = null
    var visitedId: String? = null
    var visitedOffId: String? = null
    var activityName: String? = null
    var activityAction: String? = null
    var activityId: String? = null
    var isSequence: String? = null
    var planId: String? = null
    var completedTask: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        planId = `in`.readString()
        customerId = `in`.readString()
        customerCode = `in`.readString()
        startTime = `in`.readString()
        endTime = `in`.readString()
        latitude = `in`.readString()
        longitude = `in`.readString()
        name = `in`.readString()
        status = `in`.readString()
        reason = `in`.readString()
        comment = `in`.readString()
        visitedId = `in`.readString()
        visitedOffId = `in`.readString()
        activityName = `in`.readString()
        activityId = `in`.readString()
        activityAction = `in`.readString()
        isSequence = `in`.readString()
        completedTask = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(planId)
        dest.writeString(customerId)
        dest.writeString(customerCode)
        dest.writeString(startTime)
        dest.writeString(endTime)
        dest.writeString(latitude)
        dest.writeString(longitude)
        dest.writeString(name)
        dest.writeString(status)
        dest.writeString(reason)
        dest.writeString(comment)
        dest.writeString(visitedId)
        dest.writeString(visitedOffId)
        dest.writeString(activityId)
        dest.writeString(activityName)
        dest.writeString(activityAction)
        dest.writeString(isSequence)
        dest.writeString(completedTask)
    }

    companion object {
        val CREATOR: Parcelable.Creator<VisitedList> = object : Parcelable.Creator<VisitedList> {
            override fun createFromParcel(`in`: Parcel): VisitedList {
                return VisitedList(`in`)
            }

            override fun newArray(size: Int): Array<VisitedList?> {
                return arrayOfNulls<VisitedList>(size)
            }
        }
    }
}