package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class CardFail : Parcelable {

    var transactionId: String? = null
    var customer_id: String? = null
    var amount: String? = null
    var mStatus: String? = null
    var mTYpe: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        transactionId = `in`.readString()
        customer_id = `in`.readString()
        amount = `in`.readString()
        mStatus = `in`.readString()
        mTYpe = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(transactionId)
        dest.writeString(customer_id)
        dest.writeString(amount)
        dest.writeString(mStatus)
        dest.writeString(mTYpe)

    }

    companion object {
        val CREATOR: Parcelable.Creator<CardFail> = object : Parcelable.Creator<CardFail> {
            override fun createFromParcel(`in`: Parcel): CardFail {
                return CardFail(`in`)
            }

            override fun newArray(size: Int): Array<CardFail?> {
                return arrayOfNulls<CardFail>(size)
            }
        }
    }
}