package com.engear.vansales.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.engear.vansales.model.SupervisorUserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.engear.vansales.model.UserData

class SharedPreferenceManager(val context: Context) {
    val mode: Int = Activity.MODE_PRIVATE
    val myPreference = "MyPreference"
    lateinit var mySharedPreference: SharedPreferences
    var mPreferenceManager: SharedPreferenceManager? = null
    lateinit var myEditor: SharedPreferences.Editor

    fun setString(key: String?, value: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(key, value).apply()
    }

    fun getString(key: String?): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(key, "")
    }


    fun setFTokenId(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.fId, userId).apply()
    }

    fun getFTokenId(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.fId, "")
    }

    fun setIsLogin(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.isLogin, userId).apply()
    }

    fun setUserTypeLogin(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.isUSerType, userId).apply()
    }

    fun getUserTypeLogin(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.isUSerType, "")
    }

    fun getIsLogin(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.isLogin, "false")
    }

    fun setUserId(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.userId, userId).apply()
    }

    fun getUserId(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.userId, "")
    }

    fun setTripId(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.tripId, userId).apply()
    }

    fun getTripId(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.tripId, "")
    }

    fun setTripDate(userId: String?) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.tripDate, userId).apply()
    }

    fun getTripDate(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.tripDate, "")
    }


    fun getAPIToken(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.apiToken, "")
    }

    fun setAPIToken(GSMToken: String) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.apiToken, GSMToken).apply()
    }

    fun getFCMToken(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.fcmToken, "")
    }

    fun setFCMToken(GSMToken: String) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.fcmToken, GSMToken).apply()
    }

    fun getIsFirstTime(): String? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        return mySharedPreference.getString(Constants.isFirstTime, "")
    }

    fun setIsFirstTime(GSMToken: String) {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.isFirstTime, GSMToken).apply()
    }

    fun setUserInfo(userInfo: UserData?) {
        val gson = Gson()
        val type = object : TypeToken<UserData>() {
        }.type
        val strObject = gson.toJson(userInfo, type)
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.userData, strObject).apply()
    }

    fun getUserInfo(): UserData? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        val gson = Gson()
        val type = object : TypeToken<UserData>() {

        }.type
        return gson.fromJson(mySharedPreference.getString(Constants.userData, ""), type)
    }
  fun setSupervisorUserInfo(userInfo: SupervisorUserData?) {
        val gson = Gson()
        val type = object : TypeToken<SupervisorUserData>() {
        }.type
        val strObject = gson.toJson(userInfo, type)
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        mySharedPreference.edit().putString(Constants.userData, strObject).apply()
    }

    fun getSupervisorUserInfo(): SupervisorUserData? {
        mySharedPreference = MyApplication.getInstance()!!.getSharedPreferences(myPreference, mode)
        val gson = Gson()
        val type = object : TypeToken<SupervisorUserData>() {

        }.type
        return gson.fromJson(mySharedPreference.getString(Constants.userData, ""), type)
    }
}