package com.engear.vansales.common

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.engear.vansales.activities.DashboardActivity
import com.engear.vansales.activities.OnboardingActivity
import com.engear.vansales.activities.picker.PickingActivity
import com.engear.vansales.activities.supervisor.SupervisorDashboardActivity
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivitySplashBinding
import com.engear.vansales.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        setFullScreen()

        getToken()
        //throw RuntimeException("Test Crash"); // Force a crash
        Handler().postDelayed({

            if (mPreferenceManager!!.getIsLogin() != null) {
                mPreferenceManager!!.setString(AppConstants.IS_DATA_SYNCING, "false")
                if (mPreferenceManager!!.getIsLogin().toString() == "true") {
                    if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo != null) {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    } else {
                        if (mPreferenceManager!!.getUserTypeLogin().toString() == "3") {
                            startActivity(Intent(this, PickingActivity::class.java))
                        } else if (mPreferenceManager!!.getUserTypeLogin().toString() == "4") {
                            startActivity(Intent(this, PickingActivity::class.java))
                        } else {
                            startActivity(Intent(this, PickingActivity::class.java))
                        }

                    }
                } else {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
            } else {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 4000)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            mPreferenceManager!!.setString(AppConstants.FIREBASE_TOKEN, token)
            Log.d("Firebase", token)
        })
    }
}