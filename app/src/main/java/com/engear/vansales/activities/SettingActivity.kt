package com.engear.vansales.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivitySettingBinding
import com.engear.vansales.databinding.ClearDataDialogBinding
import com.engear.vansales.model.RouteSettingApi
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivity : BaseActivity() {
    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@SettingActivity,
            "On setting Screen "
        )
        setToolbarTitle("Setting")
        setNavigationView(mPreferenceManager!!.getFTokenId()!!)

        binding.btnMaster!!.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On  master button click "
            )
            startActivity(Intent(this, MasterActivity::class.java))
        }

        binding.otpMaster!!.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On otpMaster click "
            )
            callOTPMaster()
        }

        binding.btnChangePWD.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On  change PWD click "
            )
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnUpload!!.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On upload click "
            )
            uploadLogFile()
        }

        binding.btnManual!!.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On manual click "
            )
            startActivity(Intent(me, OpenPDFActivity::class.java))
        }

        binding.btnClearData!!.setOnClickListener {
            Utils.logData(
                this@SettingActivity,
                "On clear data button click "
            )
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Clear Data?")
            builder.setMessage("Are you sure you want to clear data?")
            builder.setPositiveButton(
                "Yes"
            ) { dialog, _ ->
                dialog.cancel()
                passwordDialog()
            }
            builder.setNegativeButton(
                "No"
            ) { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        }

        setAppInfo()
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
        binding.tvAppInfo!!.setText(sb.toString())
    }

    private fun uploadLogFile() {

        MyApplication.isUpload = true

        showOnlyProgressDialog()


        var salesmanCode: String =
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code

        val description: RequestBody = RequestBody.create(
            MultipartBody.FORM, salesmanCode
        )

        var parts: MultipartBody.Part? = null
        parts = Utils.prepareFilePart("log_file")

        ApiClient.getClient(this)
            .uploadLog(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, description, parts
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body() != null) {

                            if (response.body()!!["status"].asBoolean) {
                                Toast.makeText(
                                    this@SettingActivity,
                                    "Log uploaded successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@SettingActivity,
                                    "Log uploaded fail!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@SettingActivity,
                            "Log uploaded fail!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    MyApplication.isUpload = false
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    MyApplication.isUpload = false
                    if (error.message != null) {
                    }
                    Toast.makeText(
                        this@SettingActivity,
                        "Log uploaded fail!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissDialog()
                }
            })
    }

    private fun callOTPMaster() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val route_id = mPreferenceManager!!.getString(AppConstants.ROUTEID)

        val requestBody: MutableMap<String, String> = HashMap()
        requestBody["route_id"] = route_id ?: ""

        ApiClient.getClient(this).getOtpMaster(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            requestBody
        ).enqueue(object : Callback<RouteSettingApi> {
            override fun onResponse(
                call: Call<RouteSettingApi>, response: Response<RouteSettingApi>
            ) {
                Log.d("callOTPMaster", "onResponse")
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@SettingActivity, "Success", Toast.LENGTH_LONG
                    ).show()

                    var userInfo = response.body()!!.data

                    mPreferenceManager!!.setString(
                        AppConstants.SALESENABLE,
                        userInfo!!.salesEnable.toString()
                    )

                    mPreferenceManager!!.setString(
                        AppConstants.RETURNENABLE,
                        userInfo!!.returnEnable.toString()
                    )

                    mPreferenceManager!!.setString(
                        AppConstants.COLLECTIONENABLE,
                        userInfo!!.collection.toString()
                    )

                    mPreferenceManager!!.setString(
                        AppConstants.PRINTENABLE,
                        userInfo!!.print.toString()
                    )

                    //Routting
                    mPreferenceManager!!.setString(
                        AppConstants.VANSTOCKPRINTENABLE,
                        userInfo!!.vanStockPrintEnable.toString()
                    )
                    //Unschedule
                    mPreferenceManager!!.setString(
                        AppConstants.UNSCHEDULEPRINTENABLE,
                        userInfo!!.unscheduledVisitsEnable.toString()
                    )

                    //skip Customer
                    mPreferenceManager!!.setString(
                        AppConstants.CUSTOMERSKIPENABLE,
                        userInfo!!.customerSkipEnable.toString()
                    )

                    //skip Original Customer
                    mPreferenceManager!!.setString(
                        AppConstants.CUSTOMERORIGINALSKIPENABLE,
                        userInfo!!.skippingCustomers.toString()
                    )

                    //skip Force check in Customer
                    mPreferenceManager!!.setString(
                        AppConstants.FORCECHECKINENABLE,
                        userInfo!!.forceCheckInEnable.toString()
                    )

                    //skip Force check out Customer
                    mPreferenceManager!!.setString(
                        AppConstants.FORCECHECKOUTENABLE,
                        userInfo!!.forceCheckOutEnable.toString()
                    )


                    //skip No Transection Customer
                    mPreferenceManager!!.setString(
                        AppConstants.NOTRANCETIONENABLE,
                        userInfo!!.noTransactionEnable.toString()
                    )

                    //skip JCP Customer
                    mPreferenceManager!!.setString(
                        AppConstants.JCPCUSTOMERENABLE,
                        userInfo!!.jcpCustomersEnable.toString()
                    )

                    //skip Invoice Cancel Customer
                    mPreferenceManager!!.setString(
                        AppConstants.INVOICECANCELENABLE,
                        userInfo!!.invoiceCancellationVoidEnable.toString()
                    )
                } else {
                    // Handle API error
                    Log.e("API Error", "API call failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RouteSettingApi>, error: Throwable) {
                Log.e("callOTPMaster", "onFailure", error)
                progressDialog.dismiss()
                // Handle network error
                Log.e("Network Error", "Failed to make API call: ${error.message}")
            }
        })
    }

    fun passwordDialog() {
        val factory = LayoutInflater.from(this)
        val binding: ClearDataDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.clear_data_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.btnConfirm.setOnClickListener {
            var password = binding.edtComment.text.toString().toLowerCase()

            if (password.isEmpty()) {
                Toast.makeText(applicationContext, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != "masafi@7824") {
                Toast.makeText(applicationContext, "Password does not match!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                deleteDialog.dismiss()
                mPreferenceManager!!.mySharedPreference.edit().clear()
                mPreferenceManager!!.setIsLogin("false")
                mPreferenceManager!!.setString(AppConstants.IS_STARTDAY, "0")
                mPreferenceManager!!.setString(AppConstants.IS_DATA_SYNCING, "false")
                //mCRDPreferenceManager!!.setLoginCredential("","")
                db!!.clearDatabase()
                db!!.delteMasterData()
                if (mSingleAccountApp != null) {
                    mSingleAccountApp!!.signOut(object :
                        ISingleAccountPublicClientApplication.SignOutCallback {
                        override fun onSignOut() {
                            Log.e("Signout", "Success")
                            startActivity(
                                Intent(
                                    this@SettingActivity,
                                    LoginActivity::class.java
                                ).apply {
                                    this.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                        }

                        override fun onError(@NonNull exception: MsalException) {
                            Log.e("Signout", "Error")
                            startActivity(Intent(this@SettingActivity, LoginActivity::class.java).apply {
                                this.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                        }
                    })
                } else {
                    startActivity(Intent(this@SettingActivity, LoginActivity::class.java).apply {
                        this.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }

        }

        binding.btnCancel.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }
}