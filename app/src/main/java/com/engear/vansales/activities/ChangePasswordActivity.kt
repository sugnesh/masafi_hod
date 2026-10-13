package com.engear.vansales.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityChangePasswordBinding
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseActivity() {
    lateinit var binding: ActivityChangePasswordBinding
    private var oldPassword: String = ""
    private var newPassword: String = ""
    private var confirmPassword: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        Utils.logData(
            this@ChangePasswordActivity,
            "On Change Password Screen "
        )
    }

    private fun init() {
        setToolbarTitle("Change Password")

        binding.btnSubmit.setOnClickListener {
            Utils.logData(
                this@ChangePasswordActivity,
                "On Submit button Click "
            )
            if (isValid()) {
                callAPI()
            }
        }

    }

    private fun isValid(): Boolean {

        oldPassword = binding.etOldPwd.text.toString()
        newPassword = binding.etNewPwd.text.toString()
        confirmPassword = binding.etConfirmPwd.text.toString()

        if (oldPassword.trim().isEmpty()) {
            showSnackBar(this, "Please Enter Old Password!")
            return false
        } else if (newPassword.trim().isEmpty()) {
            showSnackBar(this, "Please Enter New Password!")
            return false
        } else if (confirmPassword.trim().isEmpty()) {
            showSnackBar(this, "Please Enter Confirm Password!")
            return false
        } else if (newPassword.trim() != confirmPassword.trim()) {
            showSnackBar(this, "New Password & confirm password does not match!!")
            return false
        }
        return true
    }

    private fun callAPI() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        try {
            jObj.addProperty("email", mPreferenceManager!!.getUserInfo()!!.data.userInfo.email)
            jObj.addProperty("old_password", oldPassword)
            jObj.addProperty("password", newPassword)
        } catch (e: JsonIOException) {
            e.printStackTrace()
        }

        ApiClient.getClient(this)
            .changePassword(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN,
                jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    Utils.logData(
                        this@ChangePasswordActivity,
                        "ChangePWD Response$response"
                    )

                    dismissDialog()
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@ChangePasswordActivity,
                                "Invoice Response${response.body().toString()}"
                            )
                            if (response.body()!!["status"].asBoolean) {

                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    "Password change successfully!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this@ChangePasswordActivity,
                                    response.body()!!["message"].asString,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "Error In API",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    if (error.message != null) {
                        Utils.logData(
                            this@ChangePasswordActivity,
                            "Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@ChangePasswordActivity,
                            "Response API Fail"
                        )
                    }
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "API Fail",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissDialog()
                }
            })
    }
}