package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Order : Parcelable {
    var orderNo: String? = null
    var addressId: String? = null
    var orderType: String? = null
    var orderDate: String? = null
    var deliveryDate: String? = null
    var customerId: String? = null
    var gross: String? = null
    var discount: String? = null
    var net: String? = null
    var excAmt: String? = null
    var vat: String? = null
    var total: String? = null
    var comment: String? = null
    var dueDate: String? = null
    var payemntId: String? = null
    var invoiceId: String? = null
    var reason: String? = null
    var isDamaged: String? = null
    var lob_id: String? = null
    var isExchange: String? = null
    var exchangeNo: String? = null
    var isRequested: String? = null
    var lpoNo: String? = null
    var uuid: String? = null
    var status: String? = null
    var invoiceNo: String? = null
    var invoiceAmt: String? = null
    var returnImage: String? = null
    var returnImage2: String? = null
    var returnImage3: String? = null
    var rID: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        orderNo = `in`.readString()
        addressId = `in`.readString()
        orderDate = `in`.readString()
        deliveryDate = `in`.readString()
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
        invoiceId = `in`.readString()
        reason = `in`.readString()
        isDamaged = `in`.readString()
        lob_id = `in`.readString()
        isExchange = `in`.readString()
        exchangeNo = `in`.readString()
        lpoNo = `in`.readString()
        isRequested = `in`.readString()
        uuid = `in`.readString()
        status = `in`.readString()
        invoiceNo = `in`.readString()
        invoiceAmt = `in`.readString()
        excAmt = `in`.readString()
        returnImage=`in`.readString()
        returnImage2=`in`.readString()
        returnImage3=`in`.readString()
        rID=`in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(orderNo)
        dest.writeString(addressId)
        dest.writeString(orderDate)
        dest.writeString(deliveryDate)
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
        dest.writeString(invoiceId)
        dest.writeString(reason)
        dest.writeString(isDamaged)
        dest.writeString(lob_id)
        dest.writeString(isExchange)
        dest.writeString(exchangeNo)
        dest.writeString(lpoNo)
        dest.writeString(isRequested)
        dest.writeString(uuid)
        dest.writeString(status)
        dest.writeString(invoiceNo)
        dest.writeString(invoiceAmt)
        dest.writeString(excAmt)
        dest.writeString(returnImage)
        dest.writeString(returnImage2)
        dest.writeString(returnImage3)
        dest.writeString(rID)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Order> = object : Parcelable.Creator<Order> {
            override fun createFromParcel(`in`: Parcel): Order {
                return Order(`in`)
            }

            override fun newArray(size: Int): Array<Order?> {
                return arrayOfNulls<Order>(size)
            }
        }
    }
}