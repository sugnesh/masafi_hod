package com.engear.vansales.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.engear.vansales.R
import com.engear.vansales.activities.picker.PickingActivity
import com.engear.vansales.activities.supervisor.SupervisorDashboardActivity
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityLoginBinding
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.model.SupervisorUserData
import com.engear.vansales.model.UnloadFlag
import com.engear.vansales.model.UserData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.MSGraphRequestWrapper
import com.engear.vansales.utils.Utils
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    var inDevelopment = true
    var isClick = false
    private var arrUnloadFlag: ArrayList<UnloadFlag> = ArrayList()
    var versionName: String = ""
    var deviceIMI: String = ""
    var deviceToken: String = ""
    var deviceName: String = ""
    var userType: Int = 1

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

//       var dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 30, "2")!!
//        Log.e("Due date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 60, "2")!!
//        Log.e("Due 60date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 90, "2")!!
//        Log.e("Due 90date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 120, "2")!!
//        Log.e("Due 120date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 45, "2")!!
//        Log.e("Due 45date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 15, "2")!!
//        Log.e("Due 15date",dueDate)
//        dueDate =
//            Utils.addDays("yyyy-MM-dd", "2023-12-01", 75, "2")!!
//        Log.e("Due 75date",dueDate)
        if (inDevelopment) {
//            binding.etUsername.setText("khorshedalam@masafi.com")
//            binding.etPassword.setText("123456")
        }
//        binding.rgLoginType.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                R.id.rbsalsman -> {
//                    userType = 1
//                }
//
//                R.id.rbPicker -> {
//                    userType = 3
//                }
//
//                R.id.rbChecker -> {
//                    userType = 4
//                }
//
//                R.id.rbLoader -> {
//                    userType = 5
//                }
//
//            }
//        }

        requestBluetoothPermissions()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_login -> {
                Utils.logData(
                    me,
                    "User Click on Login Button"
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        // Permission NOT granted
                        showPermissionRequiredDialog()
                    } else {
                        Utils.logData(
                            me,
                            "User Click on Login Button"
                        )


                        if (isValid()) {

                            if (userType == 1) {
                                callLoginAPI("")
                            } else {
                                callSupervisorLoginAPI()
                            }
                        }
                    }
                }

            }
        }
    }

    private fun showPermissionRequiredDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.setData(Uri.parse("package:$packageName"))
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
            }
        }
    }

    private fun displayError(@NonNull exception: java.lang.Exception) {
        //logTextView.setText(exception.toString())
        var message = exception.message.toString()
        if (message == "An account is already signed in.") {
            mSingleAccountApp!!.signOut(object :
                ISingleAccountPublicClientApplication.SignOutCallback {
                override fun onSignOut() {
                    Log.e("Signout", "Success")
                    getAuthInteractiveCallback()?.let {
                        mSingleAccountApp!!.signIn(
                            this@LoginActivity, null, SCOPES,
                            it
                        )
                    }
                }

                override fun onError(@NonNull exception: MsalException) {
                    Log.e("Signout", "Error")
                }
            })
        } else {
            Utils.displayAlert(this, exception.message.toString())
        }
    }

    private fun getAuthInteractiveCallback(): AuthenticationCallback? {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAGBA, "Successfully authenticated")

//                /* Update UI */updateUI(authenticationResult.account)
//                /* call graph */callGraphAPI(authenticationResult)
                Log.e("Username", authenticationResult.account.username)
                //Utils.displayAlert(this@LoginActivity, authenticationResult.account.username)
                callGraphAPI(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Log.d(TAGBA, "Authentication failed: $exception")
                displayError(exception)
            }

            override fun onCancel() {
                /* User canceled the authentication */
                Log.d(TAGBA, "User cancelled login.")
            }
        }
    }

    private fun callGraphAPI(authenticationResult: IAuthenticationResult) {

        var mEndPoint = MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me"
        MSGraphRequestWrapper.callGraphAPIUsingVolley(
            this,
            mEndPoint,
            authenticationResult.accessToken,
            { response ->
                Log.d(TAGBA, "Response: $response")
                displayGraphResult(response)
            }
        ) { error ->
            Log.d(TAGBA, "Error: $error")
            displayError(error)
        }
    }

    private fun displayGraphResult(graphResponse: JSONObject) {
        Log.e("Graph Respone", graphResponse.toString())
        if (graphResponse != null) {
            if (graphResponse.has("userPrincipalName")) {
                var mEmail = graphResponse.getString("userPrincipalName")
                Log.e("mEmail", mEmail.toString())
                callLoginAPI(mEmail)
            }
        }
        //Toast.makeText(this, graphResponse.toString(), Toast.LENGTH_LONG).show()
        // Utils.displayAlert(this, graphResponse.toString())
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


    private fun callLoginAPI(mEmail: String) {

        isClick = false
        showOnlyProgressDialog()

        Utils.logData(
            me,
            "Login API Call"
        )

        val requestBody: MutableMap<String, String> = HashMap()
        requestBody["email"] = binding.etUsername.text.trim().toString()
        requestBody["password"] = binding.etPassword.text.trim().toString()
        requestBody["device_token"] = deviceToken
        requestBody["vesion"] = versionName
        requestBody["device_name"] = deviceName
        requestBody["imei_number"] = deviceIMI

        ApiClient.getClient(this).salesmanLogin(requestBody).enqueue(object : Callback<UserData> {
            override fun onResponse(
                call: Call<UserData>,
                response: Response<UserData>
            ) {
                Utils.logData(
                    this@LoginActivity,
                    "Login Response$response"
                )
                dismissDialog()
                if (response.isSuccessful) {
                    Utils.logData(
                        this@LoginActivity,
                        "Login Response Body${response.body().toString()}"
                    )
                    if (response.body()!!.status) {
                        userInfo = response.body()

                        mPreferenceManager!!.setString(
                            AppConstants.ROLE,
                            userInfo!!.data.userInfo.salesmanInfo.role.id.toString()
                        )
                        mPreferenceManager!!.setFTokenId(
                            userInfo!!.data.userInfo.salesmanInfo.role.id.toString()
                        )

                        if (userInfo != null) {
                            mPreferenceManager!!.setUserInfo(userInfo)
                            db!!.insertSalesman(userInfo!!)
                        }

                        mPreferenceManager!!.setString(
                            AppConstants.LOCUS_PASSWORD,
                            userInfo!!.data.userInfo.salesmanInfo.salesman_password
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.LOCUS_TRACKING,
                            "0"
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.DAY_START,
                            ""
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.TOKEN,
                            userInfo!!.data.accessToken
                        )
                        if (userInfo!!.data.userInfo.salesmanInfo.type.id.toString() == "2") {
                            mPreferenceManager!!.setString(
                                AppConstants.ROLE, "5"
                            )
                            mPreferenceManager!!.setFTokenId("5")
                        } else {
                            mPreferenceManager!!.setString(
                                AppConstants.ROLE,
                                userInfo!!.data.userInfo.salesmanInfo.role.id.toString()
                            )
                            mPreferenceManager!!.setFTokenId(
                                userInfo!!.data.userInfo.salesmanInfo.role.id.toString()
                            )
                        }

                        if (userInfo!!.data.activeMenu.isNotEmpty()) {
                            db!!.insertSalesmanMenu(userInfo!!.data.activeMenu)
                        }

                        mPreferenceManager!!.setString(
                            AppConstants.TYPE,
                            userInfo!!.data.userInfo.salesmanInfo.type.id.toString()
                        )
                        mPreferenceManager!!.setString(
                            AppConstants.DRIVERNAME,
                            userInfo!!.data.userInfo.firstname + " " + userInfo!!.data.userInfo.lastname
                        )

                        if (userInfo!!.data.userInfo.salesmanInfo.supervisor != null) {
                            mPreferenceManager!!.setString(
                                AppConstants.SUPERVISOR_ID,
                                userInfo!!.data.userInfo.salesmanInfo.supervisor.id.toString()
                            )
                        } else {
                            mPreferenceManager!!.setString(
                                AppConstants.SUPERVISOR_ID,
                                ""
                            )
                        }
                        //Salesmen rights enable or disable


                        mPreferenceManager!!.setString(
                            AppConstants.SALESENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.salesEnable
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.RETURNENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.returnEnable
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.COLLECTIONENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.collection
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.PRINTENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.print
                        )

                        //Routting
                        mPreferenceManager!!.setString(
                            AppConstants.VANSTOCKPRINTENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.van_stock_print_enable.toString()
                        )
                        //Unschedule
                        mPreferenceManager!!.setString(
                            AppConstants.UNSCHEDULEPRINTENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.unscheduled_visits_enable.toString()
                        )

                        //skip Customer
                        mPreferenceManager!!.setString(
                            AppConstants.CUSTOMERSKIPENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.customer_skip_enable.toString()
                        )

                        //skip Original Customer
                        mPreferenceManager!!.setString(
                            AppConstants.CUSTOMERORIGINALSKIPENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.skipping_customers.toString()
                        )

                        //skip Force check in Customer
                        mPreferenceManager!!.setString(
                            AppConstants.FORCECHECKINENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.force_check_in_enable.toString()
                        )

                        //skip Force check out Customer
                        mPreferenceManager!!.setString(
                            AppConstants.FORCECHECKOUTENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.force_check_out_enable.toString()
                        )

                        //skip Force check out Customer
                        mPreferenceManager!!.setString(
                            AppConstants.FORCECHECKOUTENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.force_check_out_enable.toString()
                        )

                        //skip No Transection Customer
                        mPreferenceManager!!.setString(
                            AppConstants.NOTRANCETIONENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.no_transaction_enable.toString()
                        )

                        //skip JCP Customer
                        mPreferenceManager!!.setString(
                            AppConstants.JCPCUSTOMERENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.jcp_customers_enable.toString()
                        )

                        //skip Invoice Cancel Customer
                        mPreferenceManager!!.setString(
                            AppConstants.INVOICECANCELENABLE,
                            userInfo!!.data.userInfo.salesmanInfo.route.routesetting.invoice_cancellation_void_enable.toString()
                        )

                        //Warehouse Data
                        if (userInfo!!.data.userInfo.salesmanInfo.route.warehouse != null) {
                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_LAT,
                                userInfo!!.data.userInfo.salesmanInfo.route.warehouse.lat
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_LONG,
                                userInfo!!.data.userInfo.salesmanInfo.route.warehouse.lang
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_RADIUS,
                                userInfo!!.data.userInfo.salesmanInfo.route.warehouse.radius
                            )
                        } else {
                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_LAT,
                                ""
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_LONG,
                                ""
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.WAREHOUSE_RADIUS,
                                ""
                            )
                        }

                        mPreferenceManager!!.setString(
                            AppConstants.ROUTEID,
                            userInfo!!.data.userInfo.salesmanInfo.routeId!!.toString()
                        )
                        mPreferenceManager!!.setString(
                            AppConstants.ROUTENAME,
                            userInfo!!.data.userInfo.salesmanInfo.route.routeName
                        )

                        try {
                            mPreferenceManager!!.setString(
                                AppConstants.VAN_CODE,
                                userInfo!!.data.userInfo.salesmanInfo.route.van.van_code
                            )
                        } catch (e: java.lang.Exception) {

                        }

                        try {
                            mPreferenceManager!!.setString(
                                AppConstants.VAN_ID,
                                userInfo!!.data.userInfo.salesmanInfo.route.van.id.toString()
                            )

                            mPreferenceManager!!.setString(
                                AppConstants.LAST_ODOMETER,
                                userInfo!!.data.userInfo.salesmanInfo.route.van.odometer_reading
                            )
                        } catch (e: java.lang.Exception) {

                        }




                        mPreferenceManager!!.setAPIToken(userInfo!!.data.userInfo.apiToken)
                        mPreferenceManager!!.setUserId(userInfo!!.data.userInfo.salesmanInfo.userId!!.toString())
                        mPreferenceManager!!.setIsLogin("true")
                        mPreferenceManager!!.setString(AppConstants.LAST_VISITED_SEQ, "0")
                        mPreferenceManager!!.setString(AppConstants.IS_DATA_SYNCING, "false")
                        mPreferenceManager!!.setString(Constants.IS_ENDDAY, "0")
                        mPreferenceManager!!.setString(Constants.IS_ENDTRIP, "0")
                        mPreferenceManager!!.setString(AppConstants.IS_ENDO_REQUEST, "")
                        mPreferenceManager!!.setString(AppConstants.IS_STARTDAY, "0")
                        mPreferenceManager!!.setString(AppConstants.IS_LOAD_VERIFY, "0")

                        if (userInfo!!.data.currency != null) {
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY,
                                userInfo!!.data.currency.symbol
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY_DECIMAL,
                                userInfo!!.data.currency.decimal_digits
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY_FORMATE,
                                userInfo!!.data.currency.format
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.ITEM_DECIMAL,
                                userInfo!!.data.itemDecimal
                            )
                        } else {
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY,
                                "AED"
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY_DECIMAL,
                                "2"
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.CURRENCY_FORMATE,
                                "1,234,567.89"
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.ITEM_DECIMAL,
                                "2"
                            )
                        }

                        mPreferenceManager!!.setString(
                            AppConstants.INVOICE_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.invoiceNext
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.ORDER_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.orderNext
                        )


                        mPreferenceManager!!.setString(
                            AppConstants.COLLECTION_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.collectionNext
                        )
                        mPreferenceManager!!.setString(
                            AppConstants.RETURN_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.returnNext
                        )
                        mPreferenceManager!!.setString(
                            AppConstants.UNLOAD_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.unloadNext
                        )

                        mPreferenceManager!!.setString(
                            AppConstants.LOAD_LAST,
                            userInfo!!.data.userInfo.salesmanInfo.route.loadNext
                        )

                        var exchan = userInfo!!.data.userInfo.salesmanInfo.route.returnNext
                        mPreferenceManager!!.setString(
                            AppConstants.EXCHANGE_LAST,
                            exchan
                        )
                        /*if (userInfo!!.data.salesmanNumberRange != null) {
                            //Set Number Range
                            mPreferenceManager!!.setString(
                                AppConstants.INVOICE_LAST,
                                userInfo!!.data.salesmanNumberRange.invoiceFrom
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.ORDER_LAST,
                                userInfo!!.data.salesmanNumberRange.orderFrom
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.COLLECTION_LAST,
                                userInfo!!.data.salesmanNumberRange.collectionFrom
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.RETURN_LAST,
                                userInfo!!.data.salesmanNumberRange.returnFrom
                            )
                            mPreferenceManager!!.setString(
                                AppConstants.UNLOAD_LAST,
                                userInfo!!.data.salesmanNumberRange.unloadFrom
                            )

                            var exchan = userInfo!!.data.salesmanNumberRange.returnFrom
                            mPreferenceManager!!.setString(
                                AppConstants.EXCHANGE_LAST,
                                exchan
                            )
                        } else {
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
                            mPreferenceManager!!.setString(
                                AppConstants.EXCHANGE_LAST,
                                ""
                            )
                        }*/

                        //Insert UnloadFlag
                        var mUnFlag = UnloadFlag()
                        /* mUnFlag = UnloadFlag()
                         mUnFlag.unloadType = "Van Expiry"
                         mUnFlag.unloadTypeCode = "3"
                         mUnFlag.unloadFlag = "1"
                         arrUnloadFlag.add(mUnFlag)*/

                        mUnFlag = UnloadFlag()
                        mUnFlag.unloadType = "Variance"
                        mUnFlag.unloadFlag = "1"
                        mUnFlag.unloadTypeCode = "5"
                        arrUnloadFlag.add(mUnFlag)

                        /*mUnFlag = UnloadFlag()
                        mUnFlag.unloadType = "Van Return"
                        mUnFlag.unloadFlag = "1"
                        mUnFlag.unloadTypeCode = "5"
                        arrUnloadFlag.add(mUnFlag)*/

                        db!!.insertUnLoadFlag(arrUnloadFlag)

                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DownloadingDataActivity::class.java
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

            override fun onFailure(call: Call<UserData>, error: Throwable) {
                dismissDialog()
                Log.e("loginfailed", error.toString())
                if (error.message != null) {
                    Utils.logData(
                        this@LoginActivity,
                        "Response Fail${error.message.toString()}"
                    )
                } else {
                    Utils.logData(
                        this@LoginActivity,
                        "Response API Fail"
                    )
                }
            }
        })
    }

    private fun callSupervisorLoginAPI() {

        showOnlyProgressDialog()

        Utils.logData(
            me,
            "Login API Call"
        )

        val requestBody: MutableMap<String, String> = HashMap()
        requestBody["email"] = binding.etUsername.text.trim().toString()
        requestBody["password"] = binding.etPassword.text.trim().toString()
        requestBody["device_token"] = deviceToken
        requestBody["vesion"] = versionName
        requestBody["device_name"] = deviceName
        requestBody["imei_number"] = deviceIMI

        ApiClient.getClient(this).checkerLogin(requestBody)
            .enqueue(object : Callback<SupervisorUserData> {
                override fun onResponse(
                    call: Call<SupervisorUserData>,
                    response: Response<SupervisorUserData>
                ) {
                    Utils.logData(
                        this@LoginActivity,
                        "Login Response$response"
                    )
                    dismissDialog()
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@LoginActivity,
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
                            mPreferenceManager!!.setUserTypeLogin(userType.toString())
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

                            if (userType == 3) {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        PickingActivity::class.java
                                    )
                                )
                            } else if (userType == 4) {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        PickingActivity::class.java
                                    )
                                )
                            } else if (userType == 5) {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        PickingActivity::class.java
                                    )
                                )
                            } else {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        SupervisorDashboardActivity::class.java
                                    )
                                )
                            }

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
                            this@LoginActivity,
                            "Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@LoginActivity,
                            "Response API Fail"
                        )
                    }
                }
            })
    }

    private fun isValid(): Boolean {

        if (binding.etUsername.text.toString().trim().isEmpty()) {
            showSnackBar(this, "Please Enter Username!")
            return false
        } else if (binding.etPassword.text.toString().trim().isEmpty()) {
            showSnackBar(this, "Please Enter Password!")
            return false
        }
        return true
    }

    private fun requestBluetoothPermissions() {

        val initialPermissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            initialPermissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            initialPermissions.add(Manifest.permission.READ_PHONE_STATE)
        }

        val missingInitial = initialPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingInitial.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingInitial.toTypedArray(),
                100
            )
        } else {
            requestBackgroundLocationIfNeeded()
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val missing = BLUETOOTH_PERMISSIONS.filter {
//                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
//            }
//            if (missing.isNotEmpty()) {
//                ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    100
//                )
//            }
//        }
    }

    private fun requestBackgroundLocationIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestBackgroundLocationIfNeeded()
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }

}