package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class DepositeSummary : Parcelable {
    var invoiceNo: String? = null
    var invoiceAmount: String? = null
    var ivvoiceDate: String? = null
    var collectionNo: String? = null
    var collectionType: String? = null
    var customerCode: String? = null
    var customerName: String? = null
    var chequeNo: String? = null
    var chequeDate: String? = null
    var amountPaid: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        invoiceNo = `in`.readString()
        invoiceAmount = `in`.readString()
        ivvoiceDate = `in`.readString()
        collectionNo = `in`.readString()
        collectionType = `in`.readString()
        customerCode = `in`.readString()
        customerName = `in`.readString()
        chequeNo = `in`.readString()
        chequeDate = `in`.readString()
        amountPaid = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(invoiceNo)
        dest.writeString(invoiceAmount)
        dest.writeString(ivvoiceDate)
        dest.writeString(collectionNo)
        dest.writeString(collectionType)
        dest.writeString(customerCode)
        dest.writeString(customerName)
        dest.writeString(chequeNo)
        dest.writeString(chequeDate)
        dest.writeString(amountPaid)

    }

    companion object {
        val CREATOR: Parcelable.Creator<DepositeSummary> = object : Parcelable.Creator<DepositeSummary> {
            override fun createFromParcel(`in`: Parcel): DepositeSummary {
                return DepositeSummary(`in`)
            }

            override fun newArray(size: Int): Array<DepositeSummary?> {
                return arrayOfNulls<DepositeSummary>(size)
            }
        }
    }
}