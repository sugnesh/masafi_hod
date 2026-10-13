package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Transaction : Parcelable {
    var tr_id: String? = null
    var tr_type: String? = null
    var tr_date_time: String? = null
    var tr_customer_num: String? = null
    var tr_customer_name: String? = null
    var tr_salesman_id: String? = null
    var tr_invoice_id: String? = null
    var tr_order_id: String? = null
    var tr_collection_id: String? = null
    var tr_pyament_id: String? = null
    var tr_is_posted: String? = null
    var tr_printData: String? = null
    var tr_return_type: String? = null
    var tr_message: String? = null
    var tr_isCheck: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        tr_id = `in`.readString()
        tr_type = `in`.readString()
        tr_date_time = `in`.readString()
        tr_customer_num = `in`.readString()
        tr_customer_name = `in`.readString()
        tr_salesman_id = `in`.readString()
        tr_invoice_id = `in`.readString()
        tr_order_id = `in`.readString()
        tr_collection_id = `in`.readString()
        tr_pyament_id = `in`.readString()
        tr_is_posted = `in`.readString()
        tr_printData = `in`.readString()
        tr_return_type = `in`.readString()
        tr_message = `in`.readString()
        tr_isCheck = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(tr_id)
        dest.writeString(tr_type)
        dest.writeString(tr_date_time)
        dest.writeString(tr_customer_num)
        dest.writeString(tr_customer_name)
        dest.writeString(tr_salesman_id)
        dest.writeString(tr_invoice_id)
        dest.writeString(tr_order_id)
        dest.writeString(tr_collection_id)
        dest.writeString(tr_pyament_id)
        dest.writeString(tr_is_posted)
        dest.writeString(tr_printData)
        dest.writeString(tr_return_type)
        dest.writeString(tr_isCheck)
        dest.writeString(tr_message)
    }

    companion object {
        val CREATOR: Parcelable.Creator<Transaction> = object : Parcelable.Creator<Transaction> {
            override fun createFromParcel(`in`: Parcel): Transaction {
                return Transaction(`in`)
            }

            override fun newArray(size: Int): Array<Transaction?> {
                return arrayOfNulls<Transaction>(size)
            }
        }
    }
}