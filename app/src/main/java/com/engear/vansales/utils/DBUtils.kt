package com.engear.vansales.utils

import android.content.Context
import com.engear.vansales.common.SharedPreferenceManager

object DBUtils {
    var mPreferenceManager: SharedPreferenceManager? = null

    fun getNextIndex(context: Context,type: String?): String? {
        mPreferenceManager = SharedPreferenceManager(context)

        var lastNo = ""
        val padLenth: String
        val strPadd: String
        var `val` = 0
        val str: String
        when (type) {
            "Customer" -> {
                str = mPreferenceManager!!.getString("")!!
            }
        }
        return lastNo
    }

}