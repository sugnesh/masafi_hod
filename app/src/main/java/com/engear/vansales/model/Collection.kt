package com.engear.vansales.model

import android.os.Parcel
import android.os.Parcelable

open class Collection : Parcelable {

    var collectionNo: String? = null
    var cashType: String? = null
    var paidAmt: String? = null
    var allocatAmt: String? = null
    var shelfAmt: String? = null
    var discountAmt: String? = null
    var paymentType: String? = null
    var collectionType: String? = null
    var customerId: String? = null
    var chequeNo: String? = null
    var chequeDate: String? = null
    var bank: String? = null
    var invNo: String? = null
    var lobId: String? = null
    var rebateAmt: String? = null
    var rebateVatAmt: String? = null
    var isRebateVat: String? = null
    var comment: String? = null
    var onAccountEnable: String? = null
    var chequeImage: String? = null
    var chequeImage2: String? = null
    var bankCharge: String? = null
    var taxamount: String? = null
    var mBonous: String? = null
    var isTopup: String? = null
    var paymentRefrence: String? = null
    var prepaidRef: String? = null
    var source: String? = null
    var isPartial: String? = null
    var cardType: String? = null
    var cardCharge: String? = null
    var transactionId: String? = null

    constructor() {}
    protected constructor(`in`: Parcel) {
        collectionNo = `in`.readString()
        cashType = `in`.readString()
        paidAmt = `in`.readString()
        allocatAmt = `in`.readString()
        shelfAmt = `in`.readString()
        customerId = `in`.readString()
        discountAmt = `in`.readString()
        paymentType = `in`.readString()
        collectionType = `in`.readString()
        chequeNo = `in`.readString()
        chequeDate = `in`.readString()
        bank = `in`.readString()
        invNo = `in`.readString()
        lobId = `in`.readString()
        rebateAmt = `in`.readString()
        rebateVatAmt = `in`.readString()
        isRebateVat = `in`.readString()
        comment = `in`.readString()
        onAccountEnable=`in`.readString()
        chequeImage=`in`.readString()
        chequeImage2=`in`.readString()
        bankCharge=`in`.readString()
        taxamount=`in`.readString()
        mBonous=`in`.readString()
        isTopup=`in`.readString()
        paymentRefrence=`in`.readString()
        prepaidRef=`in`.readString()
        source=`in`.readString()
        isPartial=`in`.readString()
        cardType=`in`.readString()
        cardCharge=`in`.readString()
        transactionId=`in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(collectionNo)
        dest.writeString(cashType)
        dest.writeString(paidAmt)
        dest.writeString(allocatAmt)
        dest.writeString(shelfAmt)
        dest.writeString(customerId)
        dest.writeString(discountAmt)
        dest.writeString(paymentType)
        dest.writeString(collectionType)
        dest.writeString(chequeNo)
        dest.writeString(chequeDate)
        dest.writeString(bank)
        dest.writeString(invNo)
        dest.writeString(lobId)
        dest.writeString(rebateAmt)
        dest.writeString(rebateVatAmt)
        dest.writeString(isRebateVat)
        dest.writeString(comment)
        dest.writeString(onAccountEnable)
        dest.writeString(chequeImage)
        dest.writeString(chequeImage2)
        dest.writeString(bankCharge)
        dest.writeString(taxamount)
        dest.writeString(mBonous)
        dest.writeString(isTopup)
        dest.writeString(paymentRefrence)
        dest.writeString(source)
        dest.writeString(prepaidRef)
        dest.writeString(isPartial)
        dest.writeString(cardType)
        dest.writeString(cardCharge)
        dest.writeString(transactionId)

    }

    companion object {
        val CREATOR: Parcelable.Creator<Collection> = object : Parcelable.Creator<Collection> {
            override fun createFromParcel(`in`: Parcel): Collection {
                return Collection(`in`)
            }

            override fun newArray(size: Int): Array<Collection?> {
                return arrayOfNulls<Collection>(size)
            }
        }
    }
}