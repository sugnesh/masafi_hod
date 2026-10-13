package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Invoice : Parcelable {
    var invoiceNo: String? = null
    var orderType: String? = null
    var invoiceDate: String? = null
    var invoiceType: String? = null
    var deliveryId: String? = null
    var orderId: String? = null
    var customerId: String? = null
    var gross: String? = null
    var discount: String? = null
    var net: String? = null
    var vat: String? = null
    var total: String? = null
    var comment: String? = null
    var excAmt: String? = null
    var dueDate: String? = null
    var payemntId: String? = null
    var lobId: String? = null
    var customerCode: String? = null
    var customerName: String? = null
    var paidAmount: String? = null
    var lpoNo: String? = null
    var roundTo: String? = null
    var isExchange: String? = null
    var exchangeNo: String? = null
    var isAccept: String? = null
    var isStamp: String? = null
    var isInvoiceCancel: String? = null
    var isTopup: String? = null
    var mDelvieryCharge: String? = null
    var mRewards: String? = null
    var isCoupon: String? = null
    var isCouponSale: String? = null
    var couponId: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var punchTime: String? = null
    var pType: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        invoiceNo = `in`.readString()
        invoiceType = `in`.readString()
        invoiceDate = `in`.readString()
        deliveryId = `in`.readString()
        orderId = `in`.readString()
        customerId = `in`.readString()
        gross = `in`.readString()
        discount = `in`.readString()
        net = `in`.readString()
        vat = `in`.readString()
        total = `in`.readString()
        comment = `in`.readString()
        orderType = `in`.readString()
        dueDate = `in`.readString()
        payemntId = `in`.readString()
        lobId = `in`.readString()
        customerCode = `in`.readString()
        customerName = `in`.readString()
        paidAmount = `in`.readString()
        lpoNo = `in`.readString()
        roundTo = `in`.readString()
        isExchange = `in`.readString()
        exchangeNo = `in`.readString()
        excAmt = `in`.readString()
        isAccept = `in`.readString()
        isStamp=`in`.readString()
        isInvoiceCancel=`in`.readString()
        isTopup=`in`.readString()
        mDelvieryCharge=`in`.readString()
        isCoupon=`in`.readString()
        isCouponSale=`in`.readString()
        mRewards=`in`.readString()
        couponId=`in`.readString()
        latitude=`in`.readString()
        longitude=`in`.readString()
        punchTime=`in`.readString()
        pType=`in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(invoiceNo)
        dest.writeString(invoiceType)
        dest.writeString(invoiceDate)
        dest.writeString(deliveryId)
        dest.writeString(orderId)
        dest.writeString(customerId)
        dest.writeString(gross)
        dest.writeString(discount)
        dest.writeString(net)
        dest.writeString(vat)
        dest.writeString(total)
        dest.writeString(comment)
        dest.writeString(orderType)
        dest.writeString(dueDate)
        dest.writeString(payemntId)
        dest.writeString(lobId)
        dest.writeString(customerCode)
        dest.writeString(customerName)
        dest.writeString(paidAmount)
        dest.writeString(lpoNo)
        dest.writeString(roundTo)
        dest.writeString(isExchange)
        dest.writeString(exchangeNo)
        dest.writeString(excAmt)
        dest.writeString(isAccept)
        dest.writeString(isStamp)
        dest.writeString(isInvoiceCancel)
        dest.writeString(isTopup)
        dest.writeString(mDelvieryCharge)
        dest.writeString(isCoupon)
        dest.writeString(isCouponSale)
        dest.writeString(couponId)
        dest.writeString(mRewards)
        dest.writeString(latitude)
        dest.writeString(longitude)
        dest.writeString(punchTime)
        dest.writeString(pType)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Invoice> = object : Parcelable.Creator<Invoice> {
            override fun createFromParcel(`in`: Parcel): Invoice {
                return Invoice(`in`)
            }

            override fun newArray(size: Int): Array<Invoice?> {
                return arrayOfNulls<Invoice>(size)
            }
        }
    }
}