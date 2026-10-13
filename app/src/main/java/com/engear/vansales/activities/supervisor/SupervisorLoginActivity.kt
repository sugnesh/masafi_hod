package com.engear.vansales.activities.supervisor

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityLoginBinding
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.model.SupervisorUserData
import com.engear.vansales.model.UnloadFlag
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupervisorLoginActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    var inDevelopment = true
    private var arrUnloadFlag: ArrayList<UnloadFlag> = ArrayList()
    var versionName: String = ""
    var deviceIMI: String = ""
    var deviceToken: String = ""
    var deviceName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            me,
            "On Login Screen"
        )
        makeStatusBarTransparent()
        setFullScreen()
        binding.cvLogin.setOnClickListener(this)

        setAppInfo()
        deviceIMI = getDeviceIMEI()!!
        deviceToken = mPreferenceManager!!.getString(AppConstants.FIREBASE_TOKEN).toString()

        if (inDevelopment) {
//            binding.etUsername.setText("abdul@grr.la")//abdul@grr.la//123456//zuhair.iqbal@nellara.net
//            binding.etPassword.setText("123456")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_login -> {
                Utils.logData(
                    me,
                    "User Click on Login Button"
                )
                if (isValid())
                    callLoginAPI()
            }
        }
    }

    fun getDeviceIMEI(): String? {
        var deviceUniqueIdentifier: String? = null
        deviceUniqueIdentifier =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        return deviceUniqueIdentifier
    }

    fun setAppInfo() {
        var pInfo: PackageInfo? = null
        try {
            pInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val sb = StringBuilder()
        sb.append("App Ver: ")
        sb.append(pInfo!!.versionName)
        sb.append("\t \t")
        sb.append("Build: ")
        sb.append(MyApplication.ENVIRONMENT)
        binding.tvAppInfo.setText(sb.toString())

        versionName = pInfo!!.versionName
        deviceName = android.os.Build.MANUFACTURER + android.os.Build.MODEL
    }


    private fun callLoginAPI() {

        showOnlyProgressDialog()

        Utils.logData(
            me,
            "Login API Call"
        )

        val requestBody: MutableMap<String, String> = HashMap()
//        requestBody["email"] = binding.etUsername.text.trim().toString()
//        requestBody["password"] = binding.etPassword.text.trim().toString()
        requestBody["device_token"] = deviceToken
        requestBody["vesion"] = versionName
        requestBody["device_name"] = deviceName
        requestBody["imei_number"] = deviceIMI

        ApiClient.getClient(this).supervisorLogin(requestBody)
            .enqueue(object : Callback<SupervisorUserData> {
                override fun onResponse(
                    call: Call<SupervisorUserData>,
                    response: Response<SupervisorUserData>
                ) {
                    Utils.logData(
                        this@SupervisorLoginActivity,
                        "Login Response$response"
                    )
                    dismissDialog()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@SupervisorLoginActivity,
                            "Login Response Body${response.body().toString()}"
                        )
                        if (response.body()!!.status) {
                            supervisorUserInfo = response.body()

                            mPreferenceManager!!.setString(
                                AppConstants.ROLE,
                                supervisorUserInfo!!.data.userInfo.role.id.toString()
                            )
                            mPreferenceManager!!.setFTokenId(
                                supervisorUserInfo!!.data.userInfo.role.id.toString()
                            )

                            if (supervisorUserInfo != null) {
                                mPreferenceManager!!.setSupervisorUserInfo(supervisorUserInfo)
                            }

                            mPreferenceManager!!.setString(
                                AppConstants.TOKEN,
                                supervisorUserInfo!!.data.accessToken
                            )

                            mPreferenceManager!!.setAPIToken(supervisorUserInfo!!.data.userInfo.apiToken)
                            mPreferenceManager!!.setUserId(supervisorUserInfo!!.data.userInfo.id!!.toString())
                            mPreferenceManager!!.setIsLogin("true")
                            mPreferenceManager!!.setString(AppConstants.LAST_VISITED_SEQ, "0")
                            mPreferenceManager!!.setString(AppConstants.IS_DATA_SYNCING, "false")
                            mPreferenceManager!!.setString(AppConstants.IS_ENDDAY, "0")
                            mPreferenceManager!!.setString(AppConstants.IS_STARTDAY, "0")

                            mPreferenceManager!!.setString(
                                AppConstants.INVOICE_LAST,
                                ""
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.ORDER_LAST,
                                ""
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.COLLECTION_LAST,
                                ""
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.RETURN_LAST,
                                ""
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.UNLOAD_LAST,
                                ""
                            )


                            startActivity(
                                Intent(
                                    this@SupervisorLoginActivity,
                                    SupervisorDashboardActivity::class.java
                                )
                            )
                        }
                    } else {
                        try {
                            val error = response.errorBody()!!.string()
                            val jsonObject = JSONObject(error)
                            if (jsonObject.has("message")) {
                                dismissDialog()
                                showSimpleDialog(
                                    jsonObject.getString("message"),
                                    "Alert",
                                    object : OnDialog {
                                        override fun onOk() {

                                        }
                                    })
                            }
                        } catch (e: Exception) {
                            dismissDialog()
                            Log.e("Msg", e.message!!)
                            showSimpleDialog(e.message!!, "Alert", object : OnDialog {
                                override fun onOk() {

                                }
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<SupervisorUserData>, error: Throwable) {
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@SupervisorLoginActivity,
                            "Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@SupervisorLoginActivity,
                            "Response API Fail"
                        )
                    }
                }
            })
    }

    private fun isValid(): Boolean {
//
//        if (binding.etUsername.text.toString().trim().isEmpty()) {
//            showSnackBar(this, "Please Enter Username!")
//            return false
//        } else if (binding.etPassword.text.toString().trim().isEmpty()) {
//            showSnackBar(this, "Please Enter Password!")
//            return false
//        }
        return true
    }

}