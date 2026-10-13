package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class OverDueInvoice : Parcelable {
    var collectionNo: String? = null
    var customerName: String? = null
    var invoiceNo: String? = null
    var invoiceDueDate: String? = null
    var customerId: String? = null
    var amountDue: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        collectionNo = `in`.readString()
        customerName = `in`.readString()
        invoiceNo = `in`.readString()
        customerId = `in`.readString()
        invoiceDueDate = `in`.readString()
        amountDue = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(collectionNo)
        dest.writeString(customerName)
        dest.writeString(invoiceNo)
        dest.writeString(customerId)
        dest.writeString(invoiceDueDate)
        dest.writeString(amountDue)

    }

    companion object {
        val CREATOR: Parcelable.Creator<OverDueInvoice> = object : Parcelable.Creator<OverDueInvoice> {
            override fun createFromParcel(`in`: Parcel): OverDueInvoice {
                return OverDueInvoice(`in`)
            }

            override fun newArray(size: Int): Array<OverDueInvoice?> {
                return arrayOfNulls<OverDueInvoice>(size)
            }
        }
    }
}