package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class DailySummary : Parcelable {
    var customerId: String? = null
    var customerCode: String? = null
    var customerName: String? = null
    var isPlann: String? = null
    var netSale: String? = null
    var totalSale: String? = null
    var cashAmount: String? = null
    var chaqueAmount: String? = null
    var returnAmount: String? = null
    var focAmt: String? = null
    var invCNT: String? = null
    var revCNT: String? = null
    var conCNT: String? = null
    var DI: String? = null
    var DIB: String? = null
    var APP: String? = null
    var APPB: String? = null
    var WOD: String? = null
    var MP: String? = null
    var MPC: String? = null
    var WP: String? = null
    var WPC: String? = null
    var CHAP: String? = null
    var CHAPC: String? = null
    var IDAP: String? = null
    var PRI: String? = null
    var PATH: String? = null
    var VADA: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        customerId = `in`.readString()
        customerCode = `in`.readString()
        customerName = `in`.readString()
        isPlann = `in`.readString()
        netSale = `in`.readString()
        totalSale = `in`.readString()
        cashAmount = `in`.readString()
        chaqueAmount = `in`.readString()
        returnAmount = `in`.readString()
        focAmt = `in`.readString()
        invCNT = `in`.readString()
        revCNT = `in`.readString()
        conCNT = `in`.readString()
        DI = `in`.readString()
        DIB = `in`.readString()
        APP = `in`.readString()
        APPB = `in`.readString()
        WOD = `in`.readString()
        MP = `in`.readString()
        MPC = `in`.readString()
        WP = `in`.readString()
        WPC = `in`.readString()
        CHAP = `in`.readString()
        CHAPC = `in`.readString()
        IDAP = `in`.readString()
        PRI = `in`.readString()
        PATH = `in`.readString()
        VADA = `in`.readString()

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(customerId)
        dest.writeString(customerCode)
        dest.writeString(customerName)
        dest.writeString(isPlann)
        dest.writeString(netSale)
        dest.writeString(totalSale)
        dest.writeString(cashAmount)
        dest.writeString(chaqueAmount)
        dest.writeString(returnAmount)
        dest.writeString(focAmt)
        dest.writeString(invCNT)
        dest.writeString(revCNT)
        dest.writeString(conCNT)
        dest.writeString(DI)
        dest.writeString(DIB)
        dest.writeString(APP)
        dest.writeString(APPB)
        dest.writeString(WOD)
        dest.writeString(MP)
        dest.writeString(MPC)
        dest.writeString(WP)
        dest.writeString(WPC)
        dest.writeString(CHAP)
        dest.writeString(CHAPC)
        dest.writeString(IDAP)
        dest.writeString(PRI)
        dest.writeString(PATH)
        dest.writeString(VADA)

    }

    companion object {
        val CREATOR: Parcelable.Creator<DailySummary> = object : Parcelable.Creator<DailySummary> {
            override fun createFromParcel(`in`: Parcel): DailySummary {
                return DailySummary(`in`)
            }

            override fun newArray(size: Int): Array<DailySummary?> {
                return arrayOfNulls<DailySummary>(size)
            }
        }
    }
}