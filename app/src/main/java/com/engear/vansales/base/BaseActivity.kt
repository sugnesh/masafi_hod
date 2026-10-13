package com.engear.vansales.base

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.digitsecure.dunncpplugin.handlers.DunnPay
import com.engear.vansales.R
import com.engear.vansales.activities.*
import com.engear.vansales.activities.picker.PickingActivity
import com.engear.vansales.activities.supervisor.NewCustomerActivity
import com.engear.vansales.activities.supervisor.RouteActivity
import com.engear.vansales.activities.supervisor.SupervisorDashboardActivity
import com.engear.vansales.activities.supervisor.TaskListActivity
import com.engear.vansales.adapter.DrawarAdapter
import com.engear.vansales.common.*
import com.engear.vansales.databinding.ActivityBaseBinding
import com.engear.vansales.databinding.OdometerDialogBinding
import com.engear.vansales.interfaces.DrawerItemClickListner
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.merchandiser.PromotionalActivity
import com.engear.vansales.model.DrawerItemModel
import com.engear.vansales.model.SupervisorUserData
import com.engear.vansales.model.UserData
import com.engear.vansales.printer.BluetoothPrintService
import com.engear.vansales.printer.WoosimCustomService
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.locus.lotr.sdk.LocusLotrSdk
import sh.locus.lotr.sdk.LogoutStatusListener
import sh.locus.lotr.sdk.LotrSdkReadyCallback
import sh.locus.lotr.sdk.auth.UserAuthParams
import sh.locus.lotr.sdk.exception.LotrSdkError


open class BaseActivity : AbstractBaseActivity() {


    lateinit var baseBinding: ActivityBaseBinding
    lateinit var me: BaseActivity
    var db: DatabaseHandler? = null

    open var dunnPay: DunnPay? = null

    //    var mDb: AppDatabase? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    internal var mDrawerAdapter: DrawarAdapter? = null
    var mUserType = "Salesman"

    val SCOPES = arrayOf("User.Read")
    var TAGBA: String = "Login"

    companion object {
        var menuSelection = -1
        var userInfo: UserData? = null
        var supervisorUserInfo: SupervisorUserData? = null
        var mSingleAccountApp: ISingleAccountPublicClientApplication? = null

        var mPrintService: BluetoothPrintService? = null
        var mWoosim: WoosimCustomService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_base
        )
        init()
        initDunnPay()
    }

    private fun initDunnPay() {
        val myApplication = application as MyApplication
        dunnPay = myApplication.dunnPay
        dunnPay?.setContext(this)
        dunnPay?.setDoInitAsync(false)
    }


    private fun init() {
        me = this
        mPreferenceManager = SharedPreferenceManager(this)
        /**Setup window setting*/
        /**Setup window setting */
        setToolBar(baseBinding.mToolbar)
        hideDrawerToggle()
        db = DatabaseHandler(this)

        PublicClientApplication.createSingleAccountPublicClientApplication(
            applicationContext,
            R.raw.auth_config_single_account, object :
                IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                }

                override fun onError(exception: MsalException) {
                    Log.e("Exception:", exception.toString())
                    //displayError(exception)
                }
            })

    }

    private fun displayError(@NonNull exception: java.lang.Exception) {
        //logTextView.setText(exception.toString())
        Utils.displayAlert(this, exception.toString())
    }

    fun setFullScreen() {
        try {
            supportActionBar!!.hide()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun showRightMenu() {
        baseBinding.lytRightTitle!!.visibility = View.VISIBLE

    }

    fun showNotification() {

        baseBinding.lytNotification.visibility = View.VISIBLE

        baseBinding.lytNotification.setOnClickListener {

            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    /*set supervisor navigation*/
    protected fun setNavigationView() {

        mUserType = "Supervisor"
        if (mPreferenceManager!!.getSupervisorUserInfo() != null) {
            //set Navigation header data
            baseBinding.name.text =
                "${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.firstname} ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.lastname}"

            baseBinding.txtTitle.text = "Supervisor Detail"

            if (mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo != null) {
                baseBinding.txtSalemaneNo.text =
                    "SuperVisor No: ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id}"
            } else {
                baseBinding.txtSalemaneNo.text = "Supervisor No:"
            }

            baseBinding.txtType.visibility = View.GONE
            baseBinding.txtRoute.visibility = View.GONE
            baseBinding.txtVehicleNo.visibility = View.GONE

//            if (mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo != null) {
//                Glide.with(this)
//                    .load(mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.salesmanInfo.profile_image)
//                    .into(baseBinding.imgProfile)
//            }

        }

        baseBinding.btnView.setOnClickListener {
            startActivity(Intent(me, SalesmanProfileActivity::class.java))
        }

        val drawer = baseBinding.drawerLayout

        val toggle = object : ActionBarDrawerToggle(this, drawer, baseBinding.mToolbar, 0, 0) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utils.hideSoftKeyboard(me)
            }
        }
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_white)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        baseBinding.mToolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        baseBinding.toolbarTitle.setTextColor(resources.getColor(R.color.colorWhite))

        baseBinding.drawerLayout.setScrimColor(
            ContextCompat.getColor(
                me,
                android.R.color.transparent
            )
        )
        baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val listViewItems = ArrayList<DrawerItemModel>()

        listViewItems.add(DrawerItemModel("Dashboard", R.drawable.ic_home))
        listViewItems.add(DrawerItemModel("Route Monitoring", R.drawable.ic_journey))
        listViewItems.add(DrawerItemModel("New Customer", R.drawable.ic_icon_print))
        listViewItems.add(DrawerItemModel("Daily Field Activity", R.drawable.ic_icon_settings))
        listViewItems.add(DrawerItemModel("Task List", R.drawable.ic_icon_settings))
        listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

        baseBinding.txtType.text = "Type: Supervisor"

        mDrawerAdapter = DrawarAdapter(this, listViewItems, object : DrawerItemClickListner {
            override fun OnDrawerItemClicked(position: Int, subposition: Int) {
                //baseBinding.drawerLayout.closeDrawer(baseBinding.drawerListLayout)

                when (position) {
                    0 -> {
                        menuSelection = 0
                        startActivity(Intent(me, SupervisorDashboardActivity::class.java))
                        finish()
                    }

                    1 -> {
                        menuSelection = 1
                        startActivity(Intent(me, RouteActivity::class.java))
                        finish()
                    }

                    2 -> {
                        menuSelection = 2
                        startActivity(Intent(me, NewCustomerActivity::class.java))
                        finish()
                    }

                    3 -> {
                        menuSelection = 3
                        startActivity(Intent(me, RouteActivity::class.java))
                        finish()
                    }

                    4 -> {
                        menuSelection = 4
                        startActivity(Intent(me, TaskListActivity::class.java))
                        finish()
                    }

                    5 -> {
                        logoutPopup()
                    }
                }
            }
        })
        baseBinding.lvLeftDrawer.setAdapter(mDrawerAdapter)

    }

    /* set Picker Navigation*/
    protected fun setPickerNavigationView() {

        if (mPreferenceManager!!.getUserTypeLogin() == "3") {
            mUserType = "Picker"
        } else if (mPreferenceManager!!.getUserTypeLogin() == "4") {
            mUserType = "Checker"
        } else {
            mUserType = "Loader"
        }

        if (mPreferenceManager!!.getSupervisorUserInfo() != null) {
            //set Navigation header data
            baseBinding.name.text =
                "${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.firstname} ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.lastname}"

            if (mUserType == "Picker") {
                baseBinding.txtTitle.text = "Picker Detail"
                if (mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo != null) {
                    baseBinding.txtSalemaneNo.text =
                        "Picker No: ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id}"
                } else {
                    baseBinding.txtSalemaneNo.text = "Supervisor No:"
                }
                baseBinding.txtType.text = "Type: Picker"
            } else if (mUserType == "Checker") {
                baseBinding.txtTitle.text = "Checker Detail"
                if (mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo != null) {
                    baseBinding.txtSalemaneNo.text =
                        "Checker No: ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id}"
                } else {
                    baseBinding.txtSalemaneNo.text = "Supervisor No:"
                }
                baseBinding.txtType.text = "Type: Checker"
            } else {
                baseBinding.txtTitle.text = "Loader Detail"
                if (mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo != null) {
                    baseBinding.txtSalemaneNo.text =
                        "Loader No: ${mPreferenceManager!!.getSupervisorUserInfo()!!.data.userInfo.id}"
                } else {
                    baseBinding.txtSalemaneNo.text = "Supervisor No:"
                }
                baseBinding.txtType.text = "Type: Loader"
            }

            baseBinding.txtSalemaneNo.visibility = View.GONE
            baseBinding.txtTitle.visibility = View.GONE
            baseBinding.txtType.visibility = View.GONE
            baseBinding.txtRoute.visibility = View.GONE
            baseBinding.txtVehicleNo.visibility = View.GONE

        }

        baseBinding.btnView.setOnClickListener {
            startActivity(Intent(me, SalesmanProfileActivity::class.java))
        }

        val drawer = baseBinding.drawerLayout

        val toggle = object : ActionBarDrawerToggle(this, drawer, baseBinding.mToolbar, 0, 0) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utils.hideSoftKeyboard(me)
            }
        }
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_white)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        baseBinding.mToolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        baseBinding.toolbarTitle.setTextColor(resources.getColor(R.color.colorWhite))

        baseBinding.drawerLayout.setScrimColor(
            ContextCompat.getColor(
                me,
                android.R.color.transparent
            )
        )
        baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val listViewItems = ArrayList<DrawerItemModel>()

        listViewItems.add(DrawerItemModel("Load Request", R.drawable.ic_van_transfer))
        listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))



        mDrawerAdapter = DrawarAdapter(this, listViewItems, object : DrawerItemClickListner {
            override fun OnDrawerItemClicked(position: Int, subposition: Int) {
                //baseBinding.drawerLayout.closeDrawer(baseBinding.drawerListLayout)

                when (position) {
                    0 -> {
                        menuSelection = 0
                        startActivity(Intent(me, PickingActivity::class.java))
                        finish()
                    }

                    1 -> {
                        menuSelection = 1
                        logoutPopup()
                    }

                }
            }
        })
        baseBinding.lvLeftDrawer.setAdapter(mDrawerAdapter)

    }


    /*Setup toolbar and navigation drawer*/
    protected fun setNavigationView(type: String) {

        mUserType = "Salesman"
        if (mPreferenceManager!!.getUserInfo() != null) {
            //set Navigation header data
            baseBinding.name.text =
                "${mPreferenceManager!!.getUserInfo()!!.data.userInfo.firstname} ${mPreferenceManager!!.getUserInfo()!!.data.userInfo.lastname}"

            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route != null) {
                baseBinding.txtRoute.text =
                    "Route: ${mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route!!.routeCode}"
            } else {
                baseBinding.txtRoute.visibility = View.GONE
            }

            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code != null) {
                baseBinding.txtSalemaneNo.text =
                    "Salesman No: ${mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code}"
            } else {
                baseBinding.txtSalemaneNo.text = "Salesman No:"
            }

            if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.profile_image != null) {
                Glide.with(this)
                    .load(mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.profile_image)
                    .into(baseBinding.imgProfile)
            }

        }

        baseBinding.btnView.setOnClickListener {
            startActivity(Intent(me, SalesmanProfileActivity::class.java))
        }

        val drawer = baseBinding.drawerLayout

        val toggle = object : ActionBarDrawerToggle(this, drawer, baseBinding.mToolbar, 0, 0) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Utils.hideSoftKeyboard(me)
            }
        }
        toggle.drawerArrowDrawable.color = Color.WHITE
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_white)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white)
        baseBinding.mToolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        baseBinding.toolbarTitle.setTextColor(resources.getColor(R.color.colorWhite))

        baseBinding.drawerLayout.setScrimColor(
            ContextCompat.getColor(
                me,
                android.R.color.transparent
            )
        )
        baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val listViewItems = ArrayList<DrawerItemModel>()

        when (type) {
            "1" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(
                    DrawerItemModel(
                        "Overdue Customer",
                        R.drawable.ic_van_transfer
                    )
                )
                listViewItems.add(DrawerItemModel("Setting", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Approvals", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("End Trip", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

                baseBinding.txtType.text = "Type: Pre Seller"
            }

            "2" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Manage Inventory", R.drawable.ic_inventory))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Load Request", R.drawable.ic_load))
                listViewItems.add(
                    DrawerItemModel(
                        "Overdue Customer",
                        R.drawable.ic_van_transfer
                    )
                )
                listViewItems.add(DrawerItemModel("Payment", R.drawable.ic_collection))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("Setting", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Approvals", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("End Trip", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))
                //listViewItems.add(DrawerItemModel("Competitor Information", R.drawable.ic_compe))
                baseBinding.txtType.text = "Type: Van Sales"

            }

            "4" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Manage Inventory", R.drawable.icon_merchant))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Payment", R.drawable.ic_collection))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("Setting", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("End Trip", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

                baseBinding.txtType.text = "Type: Delivery"

            }

            "3" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Manage Inventory", R.drawable.ic_inventory))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Load Request", R.drawable.ic_load))
                listViewItems.add(
                    DrawerItemModel(
                        "Overdue Customer",
                        R.drawable.ic_van_transfer
                    )
                )
                listViewItems.add(DrawerItemModel("Payment", R.drawable.ic_collection))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("Setting", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("End Trip", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

                baseBinding.txtType.text = "Type: Hybrid"
            }

            "5" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Competitor Information", R.drawable.ic_compe))
                listViewItems.add(DrawerItemModel("Sensory Survey", R.drawable.ic_survey))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("Product Catalog", R.drawable.ic_product))
                listViewItems.add(
                    DrawerItemModel(
                        "Promotional Accountability",
                        R.drawable.ic_product
                    )
                )
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

                baseBinding.txtType.text = "Type: Merchandiser"
            }

            "6" -> {
                listViewItems.add(DrawerItemModel("Dashbaord", R.drawable.ic_home))
                listViewItems.add(DrawerItemModel("Journey Plan", R.drawable.ic_journey))
                listViewItems.add(DrawerItemModel("Data Posting", R.drawable.ic_icon_print))
                listViewItems.add(DrawerItemModel("Setting", R.drawable.ic_icon_settings))
                listViewItems.add(DrawerItemModel("Logout", R.drawable.ic_logout))

                baseBinding.txtType.text = "Type: BD"
            }
        }

        mDrawerAdapter = DrawarAdapter(this, listViewItems, object : DrawerItemClickListner {
            override fun OnDrawerItemClicked(position: Int, subposition: Int) {
                //baseBinding.drawerLayout.closeDrawer(baseBinding.drawerListLayout)

                when (type) {
                    "1" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, OverDueCustomerActivity::class.java))
                                finish()
                            }

                            4 -> {
                                startActivity(Intent(me, SettingActivity::class.java))
                                finish()
                            }

                            5 -> {
                                startActivity(Intent(me, ApprovalsActivity::class.java))
                                finish()
                            }

                            6 -> {
                                drawer.closeDrawers()
                                if (db!!.isSyncCompleteTransaction()) {
                                    Utils.displayAlert(
                                        me,
                                        "Please sync fail transaction first!"
                                    );
                                } else {
                                    endTripDialog()
                                }
                            }

                            7 -> {
                                if (mPreferenceManager!!.getString(Constants.IS_ENDTRIP) == "1") {
                                    logoutPopup()
                                } else {
                                    Utils.displayAlert(me, "Please first do end Trip!")
                                }

                            }
                        }
                    }

                    "2" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, ManageInventoryActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, LoadRequestActivity::class.java))
                                finish()
                            }

                            4 -> {

                                startActivity(Intent(me, OverDueCustomerActivity::class.java))
                                finish()

                            }
                            /*4 -> {
                                startActivity(Intent(me, OverDueInvoiceActivity::class.java))
                                finish()
                            }*/
                            5 -> {
                                startActivity(Intent(me, PaymentActivity::class.java))
                                finish()
                            }

                            6 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            7 -> {
                                startActivity(Intent(me, SettingActivity::class.java))
                                finish()
                            }

                            8 -> {
                                startActivity(Intent(me, ApprovalsActivity::class.java))
                                finish()
                            }

                            9 -> {
                                drawer.closeDrawers()
                                if (mPreferenceManager!!.getString(AppConstants.IS_LOAD_VERIFY) == "1") {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                                        if (db!!.isSyncCompleteTransaction()) {
                                            Utils.displayAlert(
                                                me,
                                                "Please sync unload transaction first!"
                                            );
                                        } else {
                                            endTripDialog()
                                        }
                                    } else {
                                        Utils.displayAlert(
                                            me,
                                            "Please do Reconsilation before endTrip"
                                        )
                                    }
                                } else {
                                    endTripDialog()
                                }
                            }

                            10 -> {
                                if (mPreferenceManager!!.getString(AppConstants.IS_STARTDAY)
                                        .equals("1", ignoreCase = true)
                                ) {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDTRIP) == "1") {
                                        logoutPopup()
                                    } else {
                                        Utils.displayAlert(me, "Please first do end Trip!")
                                    }
                                } else {
                                    logoutPopup()
                                }

                            }

                            10 -> {
                                startActivity(Intent(me, CompetitorInfoActivity::class.java))
                                finish()
                            }
                        }
                    }

                    "4" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, ManageInventoryActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, PaymentActivity::class.java))
                                finish()
                            }

                            4 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            5 -> {
                                startActivity(Intent(me, SettingActivity::class.java))
                                finish()
                            }

                            6 -> {
                                drawer.closeDrawers()
                                if (mPreferenceManager!!.getString(AppConstants.IS_LOAD_VERIFY) == "1") {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                                        if (db!!.isSyncCompleteTransaction()) {
                                            Utils.displayAlert(
                                                me,
                                                "Please sync unload transaction first!"
                                            );
                                        } else {
                                            endTripDialog()
                                        }
                                    } else {
                                        Utils.displayAlert(
                                            me,
                                            "Please do Reconsilation before endTrip"
                                        )
                                    }
                                } else {
                                    endTripDialog()
                                }
                                //endTripDialog()
                            }

                            7 -> {
                                if (mPreferenceManager!!.getString(AppConstants.IS_STARTDAY)
                                        .equals("1", ignoreCase = true)
                                ) {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDTRIP) == "1") {
                                        logoutPopup()
                                    } else {
                                        Utils.displayAlert(me, "Please first do end Trip!")
                                    }
                                } else {
                                    logoutPopup()
                                }
                            }
                        }
                    }

                    "3" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, ManageInventoryActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, LoadRequestActivity::class.java))
                                finish()
                            }

                            4 -> {
                                startActivity(Intent(me, OverDueInvoiceActivity::class.java))
                                finish()
                            }

                            5 -> {
                                startActivity(Intent(me, PaymentActivity::class.java))
                                finish()
                            }

                            6 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            7 -> {
                                startActivity(Intent(me, SettingActivity::class.java))
                                finish()
                            }
                            8 -> {
                                if (mPreferenceManager!!.getString(AppConstants.IS_LOAD_VERIFY) == "1") {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
                                        if (db!!.isSyncCompleteTransaction()) {
                                            Utils.displayAlert(
                                                me,
                                                "Please sync unload transaction first!"
                                            );
                                        } else {
                                            endTripDialog()
                                        }
                                    } else {
                                        Utils.displayAlert(
                                            me,
                                            "Please do Reconsilation before endTrip"
                                        )
                                    }
                                } else {
                                    endTripDialog()
                                }
                            }
                            9 -> {
                                if (mPreferenceManager!!.getString(AppConstants.IS_STARTDAY)
                                        .equals("1", ignoreCase = true)
                                ) {
                                    if (mPreferenceManager!!.getString(Constants.IS_ENDTRIP) == "1") {
                                        logoutPopup()
                                    } else {
                                        Utils.displayAlert(me, "Please first do end Trip!")
                                    }
                                } else {
                                    logoutPopup()
                                }
                            }
                        }
                    }

                    "5" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, CompetitorInfoActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, SensorySurveyActivity::class.java))
                                finish()
                            }

                            4 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            5 -> {
                                startActivity(Intent(me, ProductCatalogActivity::class.java))
                                finish()
                            }

                            6 -> {
                                startActivity(Intent(me, PromotionalActivity::class.java))
                                finish()
                            }

                            7 -> {
                                logoutPopup()
                            }
                        }
                    }

                    "6" -> {
                        when (position) {
                            0 -> {
                                startActivity(Intent(me, DashboardActivity::class.java))
                                finish()
                            }

                            1 -> {
                                startActivity(Intent(me, JourneyPlanActivity::class.java))
                                finish()
                            }

                            2 -> {
                                startActivity(Intent(me, DataPostingActivity::class.java))
                                finish()
                            }

                            3 -> {
                                startActivity(Intent(me, SettingActivity::class.java))
                                finish()
                            }

                            4 -> {
                                logoutPopup()
                            }

                        }
                    }
                }
            }
        })
        baseBinding.lvLeftDrawer.setAdapter(mDrawerAdapter)

    }

    fun setToolBar(mToolBar: Toolbar?) {
        try {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setWindowAnimations(0)
            setSupportActionBar(mToolBar)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setToolbarTitle(title: String) {
        baseBinding.toolbarTitle.text = title
        baseBinding.mToolbar.navigationIcon!!.setColorFilter(
            resources.getColor(R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun checkSessionList() {

        showOnlyProgressDialog()

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

                    dismissDialog()

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

                                            endTripDialog()
                                        } else {
                                            Utils.displayAlert(
                                                me,
                                                "Please wait until supervisor approve your request!!"
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

                    dismissDialog()
                }
            })
    }

    private fun endTripDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("EndTrip?")
        builder.setMessage("Are you sure you want to endTrip?")
        builder.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            dialog.cancel()
            //Trip End
            //endTrip(startmeter)
            odometerDialog()
        }
        builder.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun odometerDialog() {

        val factory = LayoutInflater.from(this)
        val binding: OdometerDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.odometer_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        var lastReading = mPreferenceManager!!.getString(AppConstants.START_ODOMETER)
        binding.edtVehicle.text = mPreferenceManager!!.getString(AppConstants.START_ODOMETER)

        binding.btnConfirm.setOnClickListener {

            var startmeter = binding.edtComment.text.toString()

            if (startmeter.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter odometer reading",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (startmeter.toDouble() < lastReading!!.toDouble()) {
                Toast.makeText(
                    applicationContext,
                    "End odometer is more than start odometer",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                mPreferenceManager!!.setString(AppConstants.START_ODOMETER, startmeter)
                deleteDialog.dismiss()
                endTrip(startmeter)
            }
        }

        deleteDialog.show()
    }

    private fun locusLogout() {
        try {
            LocusLotrSdk.logout(true, object : LogoutStatusListener {
                override fun onFailure() {
                    // Don't care
                }

                override fun onSuccess() {
                    // Don't care
                }
            })
        } catch (e: IllegalStateException) {
            // When it wasn't even initialized
            e.printStackTrace()
        }
    }

    private fun stopLocusTracking() {
        login(
            MyApplication.LOCUS_CLIENT,
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.salesman_code,
            mPreferenceManager!!.getString(AppConstants.LOCUS_PASSWORD).toString()
        )
    }

    private fun login(clientId: String, userId: String, password: String) {

        val sdkReadyCallback = object : LotrSdkReadyCallback {
            override fun onAuthenticated() {
                //updateTourSTatus()
                startLocationUpdate()
            }

            override fun onError(error: LotrSdkError) {
                Toast.makeText(this@BaseActivity, error.message, Toast.LENGTH_LONG).show()
                error.throwable?.printStackTrace()
            }
        }

        // App can use either user's or client's login based on BuildConfig
        LocusLotrSdk.init(
            this@BaseActivity,
            UserAuthParams(clientId, userId, password),
            true,
            sdkReadyCallback
        )

    }

    private fun startLocationUpdate() {

        if (mPreferenceManager!!.getString(AppConstants.LOCUS_TRACKING) == "1") {
            mPreferenceManager!!.setString(AppConstants.LOCUS_TRACKING, "0")
            LocusLotrSdk.stopTracking()
        }

    }

    private fun endTrip(startmeter: String) {

        showOnlyProgressDialog()
        val jObj = JsonObject()
        if (mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId != null) {
            jObj.addProperty(
                "route_id",
                mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId
            )
        } else {
            jObj.addProperty("route_id", "")
        }
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty("trip_end", Utils.getCurrentDate() + " " + Utils.get24CurrentTime())
        jObj.addProperty("trip_status", "2")
        jObj.addProperty("trip_from", "1")
        jObj.addProperty("end_odometer", startmeter)
        jObj.addProperty("trip_id", mPreferenceManager!!.getTripId())
        //  jObj.addProperty("trip_start", Utils.getCurrentDate() + " " + Utils.getCurrentTime())
        //  jObj.addProperty("trip_start_date", Utils.getCurrentDate())
        //jObj.addProperty("trip_start_time", Utils.getCurrentTime())
        // jObj.addProperty("trip_end_date", Utils.getCurrentDate())
        //jObj.addProperty("trip_end_time", Utils.getCurrentTime())

        Utils.logData(
            this@BaseActivity,
            "End Trip Request ${jObj.toString()}"
        )
        Log.e("End Day", jObj.toString())
        ApiClient.getClient(this)
            .postEndDay(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        Utils.logData(
                            this@BaseActivity,
                            "End Trip Response  ${response.body()!!.toString()}"
                        )
                        if (response.body()!!["status"].asBoolean) {
                            mPreferenceManager!!.setString(Constants.IS_ENDTRIP, "1")
                            Toast.makeText(
                                applicationContext,
                                "Trip End Successfully!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            setNavigationView(mPreferenceManager!!.getFTokenId()!!)
                            //stopLocusTracking()
                        }
                    } else {
                        Utils.logData(
                            this@BaseActivity,
                            "End Trip Fail Response"
                        )
                        mPreferenceManager!!.setString(Constants.IS_ENDTRIP, "1")
                        //stopLocusTracking()
                    }

                    dismissDialog()
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    dismissDialog()
                    Toast.makeText(applicationContext, "Error while end trip", Toast.LENGTH_SHORT)
                        .show()
                   // stopLocusTracking()

                }
            })
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    fun hideDrawerToggle() {
        baseBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onBackPressed() {
        hideDrawerToggle()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun makeStatusBarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }
    }

    private fun logoutPopup() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Logout?")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            dialog.cancel()
            mPreferenceManager!!.mySharedPreference.edit().clear()
            mPreferenceManager!!.setIsLogin("false")
            mPreferenceManager!!.setString(AppConstants.IS_DATA_SYNCING, "false")
            mPreferenceManager!!.setString(
                AppConstants.DAY_START,
                "0"
            )
            db!!.clearDatabase()
            //locusLogout()
            if (mSingleAccountApp != null) {
                mSingleAccountApp!!.signOut(object : SignOutCallback {
                    override fun onSignOut() {
                        Log.e("Signout", "Success")
                        startActivity(Intent(this@BaseActivity, LoginActivity::class.java).apply {
                            this.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }

                    override fun onError(@NonNull exception: MsalException) {
                        Log.e("Signout", "Error")
                        startActivity(Intent(this@BaseActivity, LoginActivity::class.java).apply {
                            this.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                })
            } else {
                startActivity(Intent(this@BaseActivity, LoginActivity::class.java).apply {
                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }

        }
        builder.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    inline fun <reified T : Any> getList(list: MutableList<T>): T? {
        val cls = T::class.java
        return cls as T
    }
}
