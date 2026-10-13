package com.engear.vansales.firebase

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.engear.vansales.R
import com.engear.vansales.activities.NotificationActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.common.SplashActivity
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.restAPI.ApiClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"

    var intentBr: Intent? = null
    var intentDBr: Intent? = null
    var intentVan: Intent? = null
    var intentGr: Intent? = null
    var intentLimit: Intent? = null
    var intentOver: Intent? = null
    var obj: JSONArray? = null
    var mPreferenceManager: SharedPreferenceManager? = null

    private var db: DatabaseHandler? = null

    companion object {
        const val BROADCAST_ACTION = "com.engear.vansales.Route"
        const val BROADCAST_ACTIONVAN = "com.engear.vansales.Van"
        const val BROADCAST_ACTIONGR = "com.engear.vansales.Credit"
        const val BROADCAST_ACTIONLIMIT = "com.engear.vansales.Limit"
        const val BROADCAST_ACTIONOVERDUE = "com.engear.vansales.overdue"
        const val BROADCAST_ACTION_DELIVERY = "com.engear.vansales.RouteDelivery"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        mPreferenceManager = SharedPreferenceManager(this)
        db = DatabaseHandler(this)
        intentVan = Intent(BROADCAST_ACTIONVAN)
        intentBr = Intent(BROADCAST_ACTION)
        intentDBr = Intent(BROADCAST_ACTION_DELIVERY)
        intentGr = Intent(BROADCAST_ACTIONGR)
        intentLimit = Intent(BROADCAST_ACTIONLIMIT)
        intentOver = Intent(BROADCAST_ACTIONOVERDUE)
        try {
            if (remoteMessage.data != null) {
                sendNotification(remoteMessage)
            }
            Log.e(TAG, "Push notification: " + remoteMessage.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var message = ""
        var title = ""
        var notitype = ""
        var LoadRequestId = ""
        var type = ""
        if (remoteMessage.data.containsKey("message")) {
            message = remoteMessage.data.get("message").toString()
            Log.v(TAG, "Message: " + message)
        }

        if (remoteMessage.data.containsKey("title")) {
            title = remoteMessage.data.get("title").toString()
            Log.v(TAG, "Title: " + title)
        }

        if (remoteMessage.data.containsKey("noti_type")) {
            notitype = remoteMessage.data.get("noti_type").toString()
            Log.v(TAG, "NotiType: " + notitype)
        }



        if (notitype == "load_request") {
            LoadRequestId = remoteMessage.data.get("uuid").toString()
            Log.v(TAG, "LoadId: " + LoadRequestId)
        } else if (notitype == "Delivery") {
            if (remoteMessage.data.containsKey("type")) {
                type = remoteMessage.data.get("type").toString()
                Log.e(TAG, "DelvType: " + type)
            }

            var mDeliveryNo = ""

            if (remoteMessage.data.containsKey("delivery_number")) {
                mDeliveryNo = remoteMessage.data.get("delivery_number").toString()
                Log.e(TAG, "DELVNo: " + type)
            }

            if (type == "2") {
                db!!.deleteDelivery(mDeliveryNo)
            }

        } else if (notitype == "Delivery-Remove") {
            if (remoteMessage.data.containsKey("type")) {
                type = remoteMessage.data.get("type").toString()
                Log.e(TAG, "DelvType: " + type)
            }

            var mDeliveryNo = ""

            if (remoteMessage.data.containsKey("delivery_number")) {
                mDeliveryNo = remoteMessage.data.get("delivery_number").toString()
                Log.e(TAG, "DELVNo: " + type)
            }
            db!!.deleteDelivery(mDeliveryNo)

        } else if (notitype == "Session Endorsement") {
            checkSessionList()
        } else if (notitype == "Route Diversion") {

            intentBr!!.putExtra("supervisor_id", "")
            intentBr!!.putExtra("salesman_id", "")
            intentBr!!.putExtra("push_type", "Route Diviation")
            intentBr!!.putExtra("customer_id", "")
            intentBr!!.putExtra("status", "Approve")
            sendBroadcast(intentBr)


        } else if (notitype == "Geo Approval") {

            var status = remoteMessage.data.get("status").toString()
            var reason = remoteMessage.data.get("reason").toString()

            intentBr!!.putExtra("supervisor_id", "")
            intentBr!!.putExtra("salesman_id", "")
            intentBr!!.putExtra("push_type", "Geo Approval")
            intentBr!!.putExtra("customer_id", "")
            intentBr!!.putExtra("status", status)
            intentBr!!.putExtra("reason", reason)
            sendBroadcast(intentBr)

//            intentVan!!.putExtra("supervisor_id", "")
//            intentVan!!.putExtra("salesman_id", "")
//            intentVan!!.putExtra("push_type", "Van Stock")
//            intentVan!!.putExtra("customer_id", "")
//            intentVan!!.putExtra("status", status)
//            intentVan!!.putExtra("reason", reason)
//            sendBroadcast(intentVan)
        } else if (notitype == "Invoice Cancel") {

            var status = remoteMessage.data.get("status").toString()
            Log.e("Status", status)
            var reason = remoteMessage.data.get("reason").toString()

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "4") {
                intentDBr!!.putExtra("supervisor_id", "")
                intentDBr!!.putExtra("salesman_id", "")
                intentDBr!!.putExtra("push_type", "Geo Approval")
                intentDBr!!.putExtra("customer_id", "")
                intentDBr!!.putExtra("status", status)
                intentDBr!!.putExtra("reason", reason)
                sendBroadcast(intentDBr)
            } else {
                intentBr!!.putExtra("supervisor_id", "")
                intentBr!!.putExtra("salesman_id", "")
                intentBr!!.putExtra("push_type", "Geo Approval")
                intentBr!!.putExtra("customer_id", "")
                intentBr!!.putExtra("status", status)
                intentBr!!.putExtra("reason", reason)
                sendBroadcast(intentBr)
            }


        } else if (notitype == "Over Due") {

            var status = remoteMessage.data.get("status").toString()
            var reason = remoteMessage.data.get("reason").toString()

            intentGr!!.putExtra("push_type", "Over Due")
            intentGr!!.putExtra("status", status)
            intentGr!!.putExtra("reason", reason)
            sendBroadcast(intentGr)
        } else if (notitype == "Credit Limit") {

            var status = remoteMessage.data.get("status").toString()
            var reason = remoteMessage.data.get("reason").toString()

            var approveamount = remoteMessage.data.get("approved_amount").toString()
            var nodays = remoteMessage.data.get("no_of_days_approved").toString()
            var customerId = remoteMessage.data.get("customer_id").toString()
            var comment = remoteMessage.data.get("salesman_comment").toString()

            intentLimit!!.putExtra("push_type", "Over Due")
            intentLimit!!.putExtra("status", status)
            intentLimit!!.putExtra("reason", reason)
            intentOver!!.putExtra("push_type", "Over Due")
            intentOver!!.putExtra("status", status)
            intentOver!!.putExtra("approve", approveamount)
            intentOver!!.putExtra("nodays", nodays)
            intentOver!!.putExtra("customerId", customerId)
            intentOver!!.putExtra("comment", comment)
            sendBroadcast(intentLimit)
        } else if (notitype == "Account Release Approval") {

            var status = remoteMessage.data.get("status").toString()
            var approveamount = remoteMessage.data.get("approved_amount").toString()
            var nodays = remoteMessage.data.get("no_of_days_approved").toString()
            var customerId = remoteMessage.data.get("customer_id").toString()
            var comment = remoteMessage.data.get("salesman_comment").toString()

            intentOver!!.putExtra("push_type", "Over Due")
            intentOver!!.putExtra("status", status)
            intentOver!!.putExtra("approve", approveamount)
            intentOver!!.putExtra("nodays", nodays)
            intentOver!!.putExtra("customerId", customerId)
            intentOver!!.putExtra("comment", comment)
            sendBroadcast(intentOver)
        }

        var intent = Intent()
        intent = if (isAppIsInBackground(this)) {
            Intent(this, SplashActivity::class.java)
        } else {
            Intent(this, NotificationActivity::class.java)
        }

        val nId = Random().nextInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            nId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
        )

        val channelName = "VanSales Notification"
        val channelDescription = "vansales"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        var channelId: String = "Viebrae"
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            channelId
        )
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.ic_launcher_vansales)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            channel.description = channelDescription
            channel.setShowBadge(true)
            channel.enableLights(true)
            channel.lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            notificationBuilder.setVibrate(longArrayOf(0L))
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0L)

            notificationManager?.createNotificationChannel(channel)
        }

        notificationManager?.notify(nId, notificationBuilder.build())

        // notificationManager.notify(AppConstants.countNoti, notificationBuilder.build())
        AppConstants.countNoti++

    }

    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }

    private fun checkSessionList() {

        ApiClient.getClient(this)
            .checkSessionList(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()!!["status"].asBoolean) {

                            var mTripdId = mPreferenceManager!!.getTripId()
                            var jsonArray = response.body()!!["data"].asJsonArray

                            for (mObj in jsonArray) {
                                var jObj = mObj.asJsonObject
                                if (jObj.has("trip_id")) {
                                    if (jObj.get("trip_id").asString == mTripdId) {
                                        var status = jObj.get("status").asString
                                        if (status == "Approved") {
                                            mPreferenceManager!!.setString(
                                                AppConstants.IS_ENDO_REQUEST_APPROVE,
                                                "1"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                }
            })
    }
}