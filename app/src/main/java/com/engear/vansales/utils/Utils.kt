package com.engear.vansales.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engear.vansales.R
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.model.ActivityProfile
import com.engear.vansales.sync.BackgroundJobLower
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.joda.time.DateTime
import org.joda.time.Days
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URLDecoder
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun checkConnectivity(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            if (Build.VERSION.SDK_INT < 23) {
                if (info == null) {
                    return false
                } else if (info.isConnectedOrConnecting) {
                    return true
                }
            } else {
                val network = cm.activeNetwork
                if (network != null) {
                    val networkCapabilities: NetworkCapabilities =
                        cm.getNetworkCapabilities(network)!!
                    return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || networkCapabilities.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("Error ", e.toString())
        }
        return false
    }// End of checkConnectivity

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun hideSoftKeyboard(me: Activity) {
        if (me.currentFocus != null) {
            val inputMethodManager =
                me.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(me.currentFocus!!.windowToken, 0)
        }
    }

    fun formatDate(date: Date?, format: String?): String? {
        if (date == null) return null
        val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return dateFormat.format(date)
    }

    fun formatTime(date: Date?, format: String?): String? {
        if (date == null) return null
        val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return dateFormat.format(date)
    }

    fun formatLocalTime(dtStart: String?, formats: String?): String? {
        if (dtStart == null) return null
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        );
        var date: Date = Date()
        try {
            date = format.parse(dtStart)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val dateFormat = SimpleDateFormat(formats, Locale.ENGLISH)
        return dateFormat.format(date)
    }


    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun checkPermission(mContext: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId(mContext: Context): String {
        return Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getMaterialColor(context: Context, typeColor: String): Int {
        var returnColor: Int = Color.BLACK
        val arrayId: Int = context.resources.getIdentifier(
            "mdcolor_$typeColor",
            "array",
            context.applicationContext.packageName
        )
        if (arrayId != 0) {
            val colors: TypedArray = context.getResources().obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.BLACK)
            colors.recycle()
        }
        return returnColor
    }

    fun logData(context: Context?, data: String?) {
        val logger = Logger()
        //if (!MyApplication.isUpload) {
        if (context is Activity) {
            logger.appendLog(context as Activity?, data)
        } else {
            logger.appendLogContext(context, data)
        }
        // }
    }

    /** Get Current time in timestamp(milliseconds) */
    fun getCurrentTimeInTimestamp(): Long {
        val timestamp = Timestamp(System.currentTimeMillis())
        return timestamp.time
    }

    /** Get Current time in desire time pattern which provide by your desire time pattern */
    fun getCurrentTimeInPattern(datePattern: String): String {
        val simpleDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        val date = Date()
        return simpleDateFormat.format(date)
    }

    /** Convert timeStamp into date string time */
    fun convertTimestampIntoDateString(timestampInMillis: Long, datePattern: String): String {

        val date =
            Date(if (timestampInMillis.toString().length < 12) timestampInMillis * 1000 else timestampInMillis)
        val simpleDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()

        return simpleDateFormat.format(date)
    }

    /** Convert date String into timeStamp */
    fun convertDateStringIntoTimestamp(dateString: String, datePattern: String): Long {

        val simpleDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()
        val timestamp = Timestamp((simpleDateFormat.parse(dateString) as Date).time / 1000)

        return timestamp.time
    }

    /** Convert date String into another date string(based on date pattern)  */
    fun convertDateString(dateString: String, fromPattern: String, toPattern: String): String {
        return try {
            val date = SimpleDateFormat(fromPattern, Locale.getDefault()).parse(dateString)
            SimpleDateFormat(toPattern, Locale.getDefault()).format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    /** Get Current time in timestamp(milliseconds) */
    fun getWeekNumberOfMonth(): String {
        return Calendar.getInstance().get(Calendar.WEEK_OF_MONTH).toString()
    }

    fun addDays(datePattern: String, oldDate: String?, numberOfDays: Int, dueOn: String): String? {
        var dateFormat = SimpleDateFormat(datePattern)
        val c = Calendar.getInstance()
        try {
            c.time = dateFormat.parse(oldDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (dueOn == "2") {
            if (numberOfDays == 30) {
                c.add(Calendar.MONTH, 1)
                //c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else if (numberOfDays == 45) {
                c.add(Calendar.MONTH, 1)
                //c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
                c.add(Calendar.DAY_OF_YEAR, 15)
            } else if (numberOfDays == 15) {
                //c.add(Calendar.MONTH, 1)
                //c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
                c.add(Calendar.DAY_OF_YEAR, numberOfDays)
            } else if (numberOfDays == 7) {
                //c.add(Calendar.MONTH, 1)
                //c[Calendar.DAY_OF_MONTH] = c.getActualMinimum(Calendar.DAY_OF_MONTH)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
                c.add(Calendar.DAY_OF_YEAR, numberOfDays)
            } else if (numberOfDays == 60) {
                c.add(Calendar.MONTH, 2)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else if (numberOfDays == 75) {
                c.add(Calendar.MONTH, 2)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
                c.add(Calendar.DAY_OF_YEAR, 15)
            } else if (numberOfDays == 90) {
                c.add(Calendar.MONTH, 3)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else if (numberOfDays == 120) {
                c.add(Calendar.MONTH, 4)
                c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else {
                c.add(Calendar.DAY_OF_YEAR, numberOfDays)
            }
        } else {
            c.add(Calendar.DAY_OF_YEAR, numberOfDays)
        }
        val C1 = c
//        if (dueOn == "2") {
//            C1[Calendar.DAY_OF_MONTH] = C1.getActualMaximum(Calendar.DAY_OF_MONTH)
//        }
        dateFormat = SimpleDateFormat(datePattern)
        val newDate = Date(C1.timeInMillis)
        return dateFormat.format(newDate)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.format(c)
    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("hh:mm")
        return df.format(c)
    }

    fun get24CurrentTime(): String {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("HH:mm")
        return df.format(c)
    }

    fun getStringDate(strDate: String): String {
        var dateTime = ""
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = format.parse(strDate)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date != null) {
            if (DateUtils.isToday(date.time)) {
                dateTime = "Today"
            } else if (isYesterday(date)) {
                dateTime = "Yesterday"
            } else {
                dateTime = strDate
            }
        }
        return dateTime
    }

    fun getDateIsStartDate(strDate: String): Boolean {
        var isBefore = false
        var dateTime = ""
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        var CMdate: Date? = null
        val c = Calendar.getInstance()
        try {
            date = format.parse(strDate)
            CMdate = format.parse(getCurrentDate())
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date != null) {
            if (date.before(CMdate)) {
                isBefore = true
            } else if (date == CMdate) {
                isBefore = true
            }
        }
        return isBefore
    }

    fun getCUrrentDateWithTime(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatted = formatter.format(calendar.time)

        return formatted
    }

    fun getDateIsEndDate(strDate: String): Boolean {
        var isBefore = false
        var dateTime = ""
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        var CMdate: Date? = null
        val c = Calendar.getInstance()
        try {
            date = format.parse(strDate)
            CMdate = format.parse(getCurrentDate())
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date != null) {
            if (date.before(CMdate)) {
                isBefore = true
            } else if (date == CMdate) {
                isBefore = true
            }
        }
        return isBefore
    }

    fun String.getDateWithServerTimeStamp(): Date? {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        )
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")  // IMP !!!
        try {
            return dateFormat.parse(this)
        } catch (e: ParseException) {
            return null
        }
    }

    fun getTimeDiff(mDate: String): String {

        var timeDiff = ""

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var d1: Date? = null
        var d2: Date? = null
        try {
            d1 = format.parse(mDate)
            println(d1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cal = Calendar.getInstance()
        d2 = cal.time

        try {
            //in milliseconds
            val diff = d2.time - d1!!.time
            val diffSeconds = diff / 1000 % 60
            val diffMinutes = diff / (60 * 1000) % 60
            val diffHours = diff / (60 * 60 * 1000) % 24
            val diffDays = diff / (24 * 60 * 60 * 1000)
            print("$diffDays days, ")
            print("$diffHours hours, ")
            print("$diffMinutes minutes, ")
            print("$diffSeconds seconds.")

            if (diffDays >= 1) {
                timeDiff = "$diffDays days ago"
            } else {
                if (diffHours >= 1) {
                    timeDiff = "$diffHours hours ago"
                } else {
                    if (diffMinutes >= 1) {
                        timeDiff = "$diffMinutes minutes ago"
                    } else {
                        timeDiff = "$diffSeconds seconds ago"
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return timeDiff
    }

    fun getFormatedStringDate(strDate: String): String {
        var dateTime = ""
        val format = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = format.parse(strDate)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date != null) {
            if (DateUtils.isToday(date.time)) {
                dateTime = "Today"
            } else if (isYesterday(date)) {
                dateTime = "Yesterday"
            } else {
                dateTime = formatTime(date, "dd-MM-yyyy").toString()
            }
        }
        return dateTime
    }

    fun getVatAmt(grossAmt: Double, vatPercent: Double): Double {
        return (grossAmt.div(100)) * vatPercent
    }

    private fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun getLastIndex(mNumber: String, mType: String): String {

        var lastNo = ""
        val padLenth: String
        val strPadd: String
        var `val` = 0

        if (mType == "order") {
            //var mSplit = mNumber.split("O")
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "invoice") {
            var mSplit = mNumber.split("I")
            val lastInvNo: String = mSplit[1]
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mSplit[0] + "I" + strPadd
        } else if (mType == "return") {
            var mSplit = mNumber.split("G")
            val lastInvNo: String = mSplit[1]
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mSplit[0] + "G" + strPadd
        } else if (mType == "collection") {

            var mSplit = mNumber.split("R")
            //var mSplit = mNumber.split("R")

            var lastInvNo = ""
            if (mSplit.size > 2) {
                lastInvNo = mSplit[2]
            } else {
                lastInvNo = mSplit[1]
            }
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            if (mSplit.size > 2) {
                lastNo = "R" + mSplit[1] + "R" + strPadd
            } else {
                lastNo = mSplit[0] + "R" + strPadd
            }

        } else if (mType == "unload") {
            var mSplit = mNumber.split("U")
            val lastInvNo: String = mSplit[1]
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mSplit[0] + "U" + strPadd
        } else if (mType == "Exchange") {
            var mSplit = mNumber.split("E")
            val lastInvNo: String = mSplit[1]
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mSplit[0] + "E" + strPadd
        }

        return lastNo
    }

    fun getLastIndexNew(mNumber: String, mType: String): String {

        var lastNo = ""
        val padLenth: String
        val strPadd: String
        var `val` = 0

        if (mType == "order") {
            //var mSplit = mNumber.split("O")
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "load") {
            //var mSplit = mNumber.split("O")
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "invoice") {
            // var mSplit = mNumber.split("I")
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "return") {
            /*var mSplit = mNumber.split("G")
            val lastInvNo: String = mSplit[1]
            `val` = lastInvNo.toInt()
            `val` += 1
            padLenth = "%0" + lastInvNo.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mSplit[0] + "G" + strPadd*/
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "collection") {

            /*  var mSplit = mNumber.split("R")
              //var mSplit = mNumber.split("R")

              var lastInvNo = ""
              if (mSplit.size > 2) {
                  lastInvNo = mSplit[2]
              } else {
                  lastInvNo = mSplit[1]
              }
              `val` = lastInvNo.toInt()
              `val` += 1
              padLenth = "%0" + lastInvNo.length + "d"
              strPadd = String.format(padLenth, `val`)
              if (mSplit.size > 2) {
                  lastNo = "R" + mSplit[1] + "R" + strPadd
              } else {
                  lastNo = mSplit[0] + "R" + strPadd
              }*/
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd

        } else if (mType == "unload") {
            //  var mSplit = mNumber.split("U")
            // val lastInvNo: String = mSplit[1]
            val lastInvNo: String = mNumber
            `val` = lastInvNo.toInt()
            `val` += 1
            //padLenth = "%0" + lastInvNo.length + "d"
            strPadd = `val`.toString()
            lastNo = strPadd
        } else if (mType == "Exchange") {
            `val` = mNumber.toInt()
            `val` += 1
            padLenth = "%0" + mNumber.length + "d"
            strPadd = String.format(padLenth, `val`)
            lastNo = mNumber
        }

        return lastNo
    }

    private fun getPrefix(
        input: CharSequence
    ): String {
        val sb = StringBuilder(
            input.length /* also inspired by seh's comment */
        )
        for (i in input.indices) {
            val c = input[i]
            if (c.toInt() in 48..57) {
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun stripNonDigits(
        input: CharSequence
    ): String {
        val sb = StringBuilder(
            input.length /* also inspired by seh's comment */
        )
        for (i in input.indices) {
            val c = input[i]
            if (c.toInt() in 48..57) {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun displayAlert(mContext: Context, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("Alert")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

    fun createBackgroundJob(context: Context) {
        val time = (2 * 1000).toLong()
        val intentAlarm = Intent(context, BackgroundJobLower::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time,
            PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }

    fun getUOMQty(mUPC: String, mQty: String): String {
        var mAvailQty = "0"
        try {
            var mAqty = ((mQty.toDouble()) / (mUPC.toDouble())).toString()
            mAvailQty = getFormateQty(mAqty)
        } catch (e: Exception) {

        }

        return mAvailQty

    }

    fun getUOMPrimaryQty(mUPC: String, mQty: String): String {
        var mAvailQty = "0"
        try {
            var mAqty = ((mQty.toDouble()) * (mUPC.toDouble())).toString()
            mAvailQty = getFormateQty(mAqty)
        } catch (e: Exception) {

        }

        return mAvailQty

    }

    fun getDayDifference(mDate: String): Int {
        var mDays = 0

        val format = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        try {
            date1 = format.parse(mDate)
            println(date1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val intdt = DateTime(Date())
        val targetDt: DateTime = DateTime(date1)
        mDays = Days.daysBetween(targetDt.toLocalDate(), intdt.toLocalDate()).days

        return mDays
    }

    fun passEndDate(mDate: String, mEndDate: String): Boolean {
        var mDays = false

        val format = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = format.parse(mDate)
            date2 = format.parse(mEndDate)
            println(date1)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        mDays = date2!!.before(date1)
        return mDays
    }

    fun getDayDifferenceTwoDate(mDate: String, mChequeDate: String): Int {
        var mDays = 0

        val format = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = format.parse(mDate)
            date2 = format.parse(mChequeDate)
            println(date1)
            println(date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val intdt = DateTime(date2)
        val targetDt: DateTime = DateTime(date1)
        mDays = Days.daysBetween(targetDt.toLocalDate(), intdt.toLocalDate()).days

        return mDays
    }


    fun getUOMLowerQty(mUPC: String, mQty: String): String {
        var mLowerQty = "0"

        var mLqty = ((mQty.toDouble()) * (mUPC.toDouble())).toString()
        mLowerQty = getFormateQty(mLqty)
        return mLowerQty
    }

    fun getUOMLowerPrice(mUPC: String, mPrice: String): String {
        var mLowerQty = "0"

        var mLqty = ((mPrice.toDouble()) / (mUPC.toDouble())).toString()
        mLowerQty = mLqty
        return mLowerQty
    }

    fun getFormateQty(mQty: String): String {

        var mfinalQty = "0"
        if (MyApplication.decimalRate == 2) {
            if (mQty.toDouble() > 0) {
                val df = DecimalFormat("#.00")
                df.roundingMode = RoundingMode.HALF_EVEN
                mfinalQty = df.format(mQty.toDouble())
            } else {
                mfinalQty = "0.00"
            }

        } else if (MyApplication.decimalRate == 3) {
            val df = DecimalFormat("#.000")
            df.roundingMode = RoundingMode.HALF_EVEN
            mfinalQty = df.format(mQty)
        } else {
            mfinalQty = mQty
        }

        return mfinalQty
    }


    fun compressImage(mContext: Context, imageUri: String): String {
        val filePath = getRealPathFromURI(mContext, imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        // max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight
        // width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        // this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            // load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth as Float
        val ratioY = actualHeight / options.outHeight as Float
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.getWidth() / 2,
            middleY - bmp.getHeight() / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        // check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: " + orientation)
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: " + orientation)
            } else if (orientation == 3) {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: " + orientation)
            } else if (orientation == 8) {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: " + orientation)
            }
            scaledBitmap = scaledBitmap?.let {
                Bitmap.createBitmap(
                    it, 0, 0,
                    scaledBitmap!!.getWidth(), scaledBitmap!!.height, matrix,
                    true
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        val filename = getFilename()
        try {
            out = FileOutputStream(filename)
            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename
    }

    fun getFilename(): String {
        val storageDir = Environment.getExternalStoragePublicDirectory("SFA")
        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val s = "SFA_$timeStamp.jpg"
        val uriSting = (storageDir.absolutePath + "/" + s)
        return uriSting
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    /**
     * Get real file path from URI
     */
    fun getRealPathFromUri(mContext: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor = mContext.contentResolver.query(contentUri, proj, null, null, null)
            assert(cursor != null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun getRealPathFromURI(mContext: Context, contentURI: String): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = mContext.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun getPromotionaNo(mId: String): String? {
        var complaint = ""
        complaint = "PRO" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getComplaintNo(mId: String): String? {
        var complaint = ""
        complaint = "COM" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getCampaignNo(mId: String): String? {
        var complaint = ""
        complaint = "CAM" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getDistributionImageNo(mId: String): String? {
        var complaint = ""
        complaint = "DIM" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getSurveyNo(mId: String): String? {
        var complaint = ""
        complaint = "SRV" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getAssetsNo(mId: String): String? {
        var complaint = ""
        complaint = "AST" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getPlanogramNo(mId: String): String? {
        var complaint = ""
        complaint = "PLAN" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getCompetitorNo(mId: String): String? {
        var complaint = ""
        complaint = "COMP" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getVisitNo(mId: String): String? {
        var complaint = ""
        complaint = "VISIT" + mId + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getVisitActivityNo(mId: String, mName: String): String? {
        var complaint = ""
        complaint = "ACTIVITY" + mId + mName + getTimeStamp()!!.replace(":", "")
        return complaint
    }

    fun getTimeStamp(): String? {
        val c = Calendar.getInstance().time
        println("Current time => $c")
        val df = SimpleDateFormat("hh:mm:ss")
        return df.format(c)
    }

    fun encoder(filePath: String): String {
        val bm = BitmapFactory.decodeFile(filePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
        val b = baos.toByteArray()

        var base64 = Base64.encodeToString(b, Base64.DEFAULT)
        return base64
    }

//    fun encoderBase64(filePath: String): String {
//
//        var base64 = ""
//        val bm = BitmapFactory.decodeFile(filePath)
//        val baos = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
//        val b = baos.toByteArray()
//
//        base64 = Base64.encodeToString(b, Base64.DEFAULT)
//
//        return base64
//    }

    fun encoderBase64(filePath: String): String {
        val bm = BitmapFactory.decodeFile(filePath)
        val baos = ByteArrayOutputStream()
        var base64 = "";
        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val b = baos.toByteArray()
            try {
                base64 = Base64.encodeToString(b, Base64.DEFAULT)
            } catch (e : OutOfMemoryError) {
                // Log the error to help diagnose it
                //Log.e("MemoryError", "Out of memory while decoding bitmap", e);
                base64 = "";
            }
        } else {
            base64 = ""
        }

        return base64
    }

    fun getCurrency(mActivity: Context): String {
        var currency = ""
        var mPreferenceManager = SharedPreferenceManager(mActivity)
        currency = mPreferenceManager.getString(AppConstants.CURRENCY).toString()

        return currency
    }

    fun getCurrencyFormate(mActivity: Context, mAmount: Float): String {
        var currency = ""
        var mPreferenceManager = SharedPreferenceManager(mActivity)
        var decimal = mPreferenceManager.getString(AppConstants.CURRENCY_DECIMAL).toString()
        var formate = mPreferenceManager.getString(AppConstants.CURRENCY_FORMATE).toString()

        if (decimal == "2") {
            if (formate == "1,234,567.89") {
                val myFormatter = DecimalFormat("###,###.00")
                if (mAmount > 0) {
                    currency = myFormatter.format(mAmount)
                } else {
                    currency = "0.00"
                }
            } else if (formate == "1.234.567,89") {
                val myFormatter = DecimalFormat("###.###,00")
                currency = myFormatter.format(mAmount)
            } else {
                val myFormatter = DecimalFormat("###,###.00")
                if (mAmount > 0) {
                    currency = myFormatter.format(mAmount)
                } else {
                    currency = "0.00"
                }
            }
        } else if (decimal == "3") {
            if (formate == "1,234,567.899") {
                val myFormatter = DecimalFormat("###,###.000")
                if (mAmount > 0) {
                    currency = myFormatter.format(mAmount)
                } else {
                    currency = "0.000"
                }
            } else if (formate == "1.234.567,899") {
                val myFormatter = DecimalFormat("###.###,000")
                currency = myFormatter.format(mAmount)
            } else {
                val myFormatter = DecimalFormat("### ###,000")
                currency = myFormatter.format(mAmount)
            }
        } else {
            val myFormatter = DecimalFormat("###,###.00")
            if (mAmount > 0) {
                currency = myFormatter.format(mAmount)
            } else {
                currency = "0.00"
            }
//            if (formate == "1,234,567") {
//                val myFormatter = DecimalFormat("###,###")
//                currency = myFormatter.format(mAmount)
//            } else if (formate == "1.234.567") {
//                val myFormatter = DecimalFormat("###.###")
//                currency = myFormatter.format(mAmount)
//            } else {
//                val myFormatter = DecimalFormat("### ###")
//                currency = myFormatter.format(mAmount)
//            }
        }
        return currency
    }

    fun getMerchanActivity(): ArrayList<ActivityProfile> {
        var arrData: ArrayList<ActivityProfile> = ArrayList()

        var mProfile = ActivityProfile()
        mProfile.name = "Shelf Display"
        mProfile.priority = "1"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Stock In Store"
        mProfile.priority = "2"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Complain Feedback"
        mProfile.priority = "3"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Planogram"
        mProfile.priority = "4"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Campaign Picture"
        mProfile.priority = "5"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Consumer Survey"
        mProfile.priority = "6"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        mProfile = ActivityProfile()
        mProfile.name = "Asset Tracking"
        mProfile.priority = "7"
        mProfile.isCompleted = "1"
        arrData.add(mProfile)

        return arrData
    }

    var currentBestLocation: Location? = null


    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    /**
     * Provides access to the Fused Location Provider API.
     */
    var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Location Settings API.
     */
    var mSettingsClient: SettingsClient? = null

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    var mLocationRequest: LocationRequest? = null

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    var mLocationSettingsRequest: LocationSettingsRequest? = null

    /**
     * Callback for Location events.
     */
    var mLocationCallback: LocationCallback? = null
    var canGetLocation = false

    fun initialiseLocationVariables(activity: Context?, locationUpdate: LocationUpdate) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mSettingsClient = LocationServices.getSettingsClient(activity)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentBestLocation = locationResult.lastLocation
                Log.d(
                    "Location",
                    "onLocationResult accuracy=" + locationResult.lastLocation!!.accuracy
                )
                Log.d("Location", "onLocationResult latitude=" + currentBestLocation!!.latitude)
                Log.d(
                    "Location",
                    "onLocationResult longitude=" + currentBestLocation!!.longitude
                )
                AppConstants.Accuracy = currentBestLocation!!.accuracy.toString() + ""
                AppConstants.Latitude = currentBestLocation!!.latitude.toString()
                AppConstants.Longitude = currentBestLocation!!.longitude.toString()
                canGetLocation = true
                locationUpdate.onLocationDetect(true, currentBestLocation!!)
            }
        }
        mLocationRequest = LocationRequest()
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    interface LocationUpdate {
        fun onLocationDetect(isDetect: Boolean, mLocation: Location)
    }

    const val REQUEST_CHECK_SETTINGS = 12462

    fun startLocationUpdates(activity: Activity?) {
        stopLocationUpdates()
        if (activity != null) {
            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest!!)
                .addOnSuccessListener(activity) {
                    Log.i("Location", "All location settings are satisfied.")
                    if (ActivityCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest!!,
                        mLocationCallback!!, Looper.myLooper()
                    )
                    canGetLocation = true
                }.addOnFailureListener(
                    activity
                ) { e ->
                    canGetLocation = false
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(
                                "Location",
                                "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings "
                            )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                            } catch (sie: java.lang.Exception) {
                                Log.i("Location", "PendingIntent unable to execute request.")
                            }
                        }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                            Log.e("Location", errorMessage)
                            Toast.makeText(activity, "Please turn on GPS!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    fun stopLocationUpdates() {
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
        }
    }

    fun decodeString(data: String): String {
        var mRes = ""
        if (data == null) {
            return mRes
        } else {
            try {
                mRes = URLDecoder.decode(data, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return mRes
        }
    }


    fun dialogPrint(mContext: Context?, onSearchableDialog: OnSearchableDialog?) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_print_donot_print)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var img_print: android.widget.ImageView? =
            alertDialog.findViewById<ImageView>(R.id.img_pring)

        img_print!!.setColorFilter(
            ContextCompat.getColor(mContext, R.color.theme_color),
            PorterDuff.Mode.MULTIPLY
        )


        alertDialog.findViewById<View>(R.id.rl_print).setOnClickListener {
            onSearchableDialog?.onItemSelected("yes")
            alertDialog.dismiss()
        }

        alertDialog.findViewById<View>(R.id.rl_donot_print)
            .setOnClickListener { //your business logic
                onSearchableDialog?.onItemSelected("no")
                alertDialog.dismiss()
            }

        alertDialog.show()
    }

    fun dialogCustomerPrint(
        mContext: Context?,
        mType: String,
        onSearchableDialog: OnSearchableDialog?
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_cus_print_donot_print)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var img_print: android.widget.ImageView? =
            alertDialog.findViewById<ImageView>(R.id.img_pring)
        var txt_print: android.widget.TextView? =
            alertDialog.findViewById<TextView>(R.id.txtCust)

        if (mType == "Customer") {
            txt_print!!.text = "Customer"
        } else {
            txt_print!!.text = "Accounts"
        }

        var img_acprint: android.widget.ImageView? =
            alertDialog.findViewById<ImageView>(R.id.img_acctPrint)

        img_print!!.setColorFilter(
            ContextCompat.getColor(mContext, R.color.theme_color),
            PorterDuff.Mode.MULTIPLY
        )

        img_acprint!!.setColorFilter(
            ContextCompat.getColor(mContext, R.color.theme_color),
            PorterDuff.Mode.MULTIPLY
        )


        alertDialog.findViewById<View>(R.id.rl_print).setOnClickListener {
            onSearchableDialog?.onItemSelected(mType)
            alertDialog.dismiss()
        }

        alertDialog.findViewById<View>(R.id.rl_acct_print).setOnClickListener {
            onSearchableDialog?.onItemSelected("yes")
            alertDialog.dismiss()
        }

        alertDialog.findViewById<View>(R.id.rl_donot_print)
            .setOnClickListener { //your business logic
                onSearchableDialog?.onItemSelected("no")
                alertDialog.dismiss()
            }

        alertDialog.show()
    }

    @NonNull
    fun prepareFilePart(partName: String): MultipartBody.Part {
        val storageDir = File(
            Environment.getExternalStorageDirectory(),
            "Download" + File.separator + "SFA"
        )
        // use the FileUtils to get the actual file by uri
        val file = File(
            storageDir.getPath() + File.separator + SimpleDateFormat("yyyy-MM-dd").format(
                Date()
            ) + ".txt"
        )
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
            "txt".toMediaTypeOrNull(),
            file
        )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    @NonNull
    fun prepareFileInvoicePart(partName: String,fileName: String): MultipartBody.Part {
        val storageDir = File(
            Environment.getExternalStorageDirectory(),
            "Download" + File.separator + "Invoice"
        )
        // use the FileUtils to get the actual file by uri
        val file = File(
            storageDir.getPath() + File.separator + fileName
        )
        Log.e("Path Imaghe",file.absolutePath)

        val requestFile = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            file
        )
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)!!
        }
        return type
    }

    fun getIsDamaged(mReason: String): String {
        var isDamaged = "0"

        if (mReason == "Damage") {
            isDamaged = "1"
        } else if (mReason == "Product Complaint") {
            isDamaged = "1"
        } else if (mReason == "Problem Stock") {
            isDamaged = "1"
        } else if (mReason == "Market Complaint") {
            isDamaged = "1"
        } else if (mReason == "SM Issue") {
            isDamaged = "1"
        }
        return isDamaged
    }

    fun round(d: Float, decimalPlace: Int): BigDecimal {
        var bd = BigDecimal(d.toString())
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
        return bd
    }

//    fun twoDigit(f: Float): Float {
//        var bd = BigDecimal(f.toString())
//        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
//        var mFine = String.format("%.2f", bd.toFloat())
//        val decimalFormat = DecimalFormat("#.##")
//        var twoDigitsF: Float = java.lang.Float.valueOf(decimalFormat.format(mFine.toFloat()))
//
//        return twoDigitsF
//    }

    fun twoDigit(f: Float): Float {
        return BigDecimal.valueOf(f.toDouble()) // safer than f.toString()
            .setScale(2, RoundingMode.HALF_UP) // standard rounding
            .toFloat()
    }

//    fun twoDigit(f: Float): Float {
//        return (f * 100).toInt() / 100f
//    }

    fun twoDigitVat(f: Float): Float {
        var mFine = String.format("%.2f", f)
        val decimalFormat = DecimalFormat("#.##")
        var twoDigitsF: Float = java.lang.Float.valueOf(decimalFormat.format(mFine.toFloat()))

        return twoDigitsF
    }


    fun dismissDialog(mDialog: Dialog) {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun rejectReasonDialog(mContext: Context) {
        val alertDialogBuilder = AlertDialog.Builder(mContext)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Your request has been rejected by supervisor!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which -> dialog.dismiss() }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()
    }

}