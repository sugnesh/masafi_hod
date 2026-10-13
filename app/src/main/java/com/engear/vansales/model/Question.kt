package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Question : Parcelable {

    var questionId: String? = null
    var surveyId: String? = null
    var title: String? = null
    var questionType: String? = null
    var arrValue: ArrayList<String>? = ArrayList()
    var answer: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        questionId = `in`.readString()
        surveyId = `in`.readString()
        title = `in`.readString()
        questionType = `in`.readString()
        answer = `in`.readString()
        arrValue = `in`.readList(null!!,null) as ArrayList<String>
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(questionId)
        dest.writeString(surveyId)
        dest.writeString(title)
        dest.writeString(questionType)
        dest.writeString(answer)
        dest.writeList(arrValue as List<*>?)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Question> = object : Parcelable.Creator<Question> {
            override fun createFromParcel(`in`: Parcel): Question {
                return Question(`in`)
            }

            override fun newArray(size: Int): Array<Question?> {
                return arrayOfNulls<Question>(size)
            }
        }
    }
}