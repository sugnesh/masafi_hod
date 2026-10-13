package com.engear.vansales.fragments

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engear.vansales.R
import com.engear.vansales.activities.InputOrderQtyDialogActivity
import com.engear.vansales.activities.LoadConfirmActivity
import com.engear.vansales.activities.ManageInventoryActivity
import com.engear.vansales.activities.VanstockBatchDetailsActivity
import com.engear.vansales.adapter.CommonAdapter
import com.engear.vansales.adapter.VanBatchDetailsAdapter
import com.engear.vansales.adapter.VanStockAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.ActivityVanBatchDetailsDialogBinding
import com.engear.vansales.databinding.FragmentVanStockBinding
import com.engear.vansales.databinding.OdometerDialogBinding
import com.engear.vansales.databinding.RowItemCategoryBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.BatchdetailsList
import com.engear.vansales.model.BlockData
import com.engear.vansales.model.CategoryData
import com.engear.vansales.model.GeoApprovalData
import com.engear.vansales.model.Item
import com.engear.vansales.model.UomData
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 * Use the [VanStockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VanStockFragment : Fragment() {

    var binding: FragmentVanStockBinding? = null
    private val intentCode = 100
    var allItemList: ArrayList<Item> = ArrayList()
    var allLoadBatchItem: ArrayList<BatchdetailsList> = ArrayList()
    var catItemList: ArrayList<Item>? = ArrayList()
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var db: DatabaseHandler? = null
    var catPosition = 0
    var selectCatId = ""
    var mAdapter: VanStockAdapter? = null

    //var mAdapter_Batch: VanBatchDetailsAdapter? = null
    private var mCategoryAdapter: CommonAdapter<CategoryData>? = null
    var mPreferenceManager: SharedPreferenceManager? = null
    var itemClickListener: OnItemClickListener? = null
    var vanstockPrinteble: String = ""
    private var mDialog: Dialog? = null
    var mProgressDialog: ProgressDialog? = null
    var str_broadcast: String = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_van_stock, container, false
        )
        val view = binding!!.root



        db = DatabaseHandler(activity!!)
        mPreferenceManager = SharedPreferenceManager(activity!!)

        refreshLoad()

        vanstockPrinteble =
            mPreferenceManager!!.getString(AppConstants.VANSTOCKPRINTENABLE).toString()

        binding!!.btnPrint.setOnClickListener {
            Utils.dialogPrint(activity!!, object : OnSearchableDialog {

                override fun onItemSelected(selection: String?) {

                    if (selection.equals("yes", ignoreCase = true)) {

//                        val jsonArray = createPrintData("")
//                        val `object` = PrintLog(
//                            activity!!,
//                            activity!!
//                        )
//                        `object`.execute("", jsonArray)
//                        Log.e("Van Print",jsonArray.toString())
                        (activity!! as ManageInventoryActivity).callPrint()

                    }
                }
            })

        }

        return view
    }

    fun callPrint() {
        val jsonArray = createPrintData("")
        val `object` = PrintLog(
            activity!!,
            activity!!
        )
        `object`.execute("", jsonArray)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_load, menu)

        val color: Int
        color = resources.getColor(R.color.colorWhite)

        var drawable =
            menu.findItem(R.id.action_search).icon

        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, color)
        menu.findItem(R.id.action_search).icon = drawable

        val searchView: SearchView =
            MenuItemCompat.getActionView(menu.findItem(R.id.action_search)) as SearchView
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.setIconifiedByDefault(true)
        val searchEditText =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.hint = "Search"

        searchEditText.setTextColor(color)
        searchEditText.setHintTextColor(color)

        val searchIcon =
            searchView.findViewById(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon
            .setImageResource(R.drawable.ic_search)

        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.imageTintList =
            ColorStateList.valueOf(color)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                mAdapter!!.filter!!.filter(s)
                return false
            }
        })
    }

    companion object {
        fun newInstance(): VanStockFragment {
            return VanStockFragment()
        }

        fun callbackFunction() {
            VanStockFragment().refreshLoad()
        }
    }

    fun refreshLoad() {

        if (binding != null) {
            allItemList = ArrayList()
            allItemList = db!!.getAllVanStockItemList()

            itemClickListener = object : OnItemClickListener {
                override fun onItemsClick(position: Int, tag: String) {
                    if (tag == "batchdetails") {
                        allLoadBatchItem!!.clear()
                        allLoadBatchItem =
                            db!!.getAllVanBatchItemByID(mItemID = position.toString())
                        // allLoadBatchItem = db!!.getAllLoadBatchItem(mLoadNo = loadNo)
                        //batchDetailsDialog()
                    }
                }

            }

            setUpRecycler()

        }
    }


    private fun getCategoryItem(catId: String): ArrayList<Item>? {
        val arrMItem = ArrayList<Item>()
        for (i in allItemList!!) {
            if (i.itemCatId.toString() == catId) {
                arrMItem.add(i)
            }
        }
        return arrMItem
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            str_broadcast = "0"
            activity!!.registerReceiver(
                broadcastReceiver_Van,
                IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
            )
        } else {
            str_broadcast = "0"
            try {
                activity!!.unregisterReceiver(broadcastReceiver_Van)
            } catch (e: java.lang.Exception) {

            }
        }
    }

    private fun setUpCategoryRecycler() {
        binding!!.rvCategory.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        mCatAdapter = CategoryAdapter(context!!, categoryList!!)
//        binding!!.rvCategory.adapter = mCatAdapter

        mCategoryAdapter = object : CommonAdapter<CategoryData>(categoryList!!) {
            override fun onBind(holder: CommonViewHolder, position: Int) {
                if (holder.binding is RowItemCategoryBinding) {
                    val mData = categoryList!![position]
                    (holder.binding as RowItemCategoryBinding).tvCategory.setText(
                        mData.name
                    )
                    if (mData.isClick.equals("1")) {
                        (holder.binding as RowItemCategoryBinding).tvCategory.setBackgroundResource(
                            R.drawable.rounded_rect_blue_capsule
                        )
                        (holder.binding as RowItemCategoryBinding).tvCategory.setTextColor(
                            Color.parseColor(
                                "#FFFFFF"
                            )
                        )
                    } else {
                        (holder.binding as RowItemCategoryBinding).tvCategory.setBackgroundResource(
                            R.drawable.rounded_rect_white_capsule
                        )
                        (holder.binding as RowItemCategoryBinding).tvCategory.setTextColor(
                            Color.parseColor(
                                "#20B389"
                            )
                        )
                    }
                    (holder.binding as RowItemCategoryBinding).tvCategory.setOnClickListener(View.OnClickListener {
                        categoryList!![position].isClick = "1"
                        categoryList!![catPosition].isClick = "0"
                        catPosition = position
                        notifyDataSetChanged()
                        selectCatId = categoryList!![catPosition].catId.toString()

                        catItemList = ArrayList()
                        catItemList = getCategoryItem(selectCatId)
                        setUpRecycler()
                    })
                    holder.binding.executePendingBindings()
                }
            }

            override fun getItemViewType(position: Int): Int {
                return R.layout.row_item_category
            }
        }

        binding!!.rvCategory.adapter = mCategoryAdapter
    }


    private fun setUpRecycler() {

        binding!!.rvList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = VanStockAdapter(activity!!.applicationContext, allItemList!!, itemClickListener)
        binding!!.rvList.adapter = mAdapter


    }

    override fun onResume() {
        super.onResume()
        str_broadcast = "0"

    }

    /*****************************************************************************************
    @ Creating data for print and saving it in DB for future printing
     ****************************************************************************************/
    fun createPrintData(orderNo: String): JSONArray {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            var mQty = 0
            jDict.put(MyApplication.REQUEST, MyApplication.VAN_STOCK)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", orderNo) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("TripID", mPreferenceManager!!.getTripId())
            mainArr.put("Load Number", "0")
            val HEADERS = JSONArray()
            val TOTAL = JSONArray()
            HEADERS.put("S.NO")
            HEADERS.put("ITEM#")
            HEADERS.put("DESCRIPTION")
            HEADERS.put("UOM")
            HEADERS.put("QTY")

            //HEADERS.put(obj1);
            // HEADERS.put(obj2);
            mainArr.put("HEADERS", HEADERS)
            val jData = JSONArray()

            for (mItem in allItemList) {
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItem.itemCode)
                data.put(mItem.itemName)
                var qty = Utils.getUOMQty(mItem.itemDisplayUPC!!, mItem.itemQty!!).toDouble()

                data.put(mItem.itemDisplayUnitName)
                data.put(qty)
                var mLQty = 0F
                mLQty = qty.toFloat()

//                var displayQty = qty.toInt()
//                var totalBaseQty = displayQty * mItem.itemDisplayUPC!!.toDouble()
//                var mLQty = 0F
//                if (totalBaseQty == mItem.itemQty!!.toDouble()) {
//                    data.put(mItem.itemDisplayUnitName)
//                    data.put(displayQty)
//                    mLQty = displayQty.toFloat()
//                } else {
//                    data.put(mItem.itemUOMName)
//                    data.put(mItem.itemQty)
//                    mLQty = mItem.itemQty!!.toFloat()
//                }
                jData.put(data)
                mQty += mLQty.roundToInt()
            }
            val totalObj = JSONObject()
            //            totalObj.put("TOTAL UNITS","+" + String.format("%.2f",totalPcs));
//            totalObj.put("UNIT PRICE","");
            totalObj.put("Total Qty", "" + mQty)
            TOTAL.put(totalObj)
            mainArr.put("TOTAL", TOTAL)
            mainArr.put("data", jData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)

            // jArr.put(HEADERS);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    private lateinit var dialogBox: Dialog
    private fun openDialogActivity(id: String) {

        this.dialogBox = Dialog(activity!!)
        this.dialogBox.setContentView(R.layout.activity_van_batch_details_dialog)
        var rvVanbatchList: RecyclerView =
            this.dialogBox.findViewById(R.id.rvBatchList)//recycler view is defined in dialog box View
        rvVanbatchList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        /* mAdapter_Batch = VanBatchDetailsAdapter(activity!!.applicationContext, allLoadBatchItem!!)
         rvVanbatchList.adapter = mAdapter_Batch*/
        // mAdapter_Batch!!.notifyDataSetChanged()
        dialogBox.show()
    }

    private fun batchDetailsDialog() {

        val factory = LayoutInflater.from(activity!!)

        val binding: ActivityVanBatchDetailsDialogBinding =
            DataBindingUtil.inflate(
                factory,
                R.layout.activity_van_batch_details_dialog,
                null,
                false
            )

        val deleteDialogView: View = binding.root

        val deleteDialog: AlertDialog = AlertDialog.Builder(activity!!).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)


        /*  binding.rvBatchList.layoutManager =
              LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
          mAdapter_Batch = VanBatchDetailsAdapter(activity!!.applicationContext, allLoadBatchItem!!)
          binding.rvBatchList.adapter = mAdapter_Batch*/

        binding.btnClose.setOnClickListener {

            deleteDialog.cancel()
        }

        deleteDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {

                mAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun dialogPrint(
        mContext: Context?,
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_load_request_permission)

        var txtMsg: TextView? = alertDialog.findViewById<Button>(R.id.txt_msg)
        txtMsg!!.text =
            "You do not have permission to print van stock.Click on Yes you send request to the supervisor for approval."

        var btnConfirm: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnConfirm)

        var btnCancel: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm!!.setOnClickListener {
            alertDialog.dismiss()
            str_broadcast = "1"
            sendApprovalRequest()
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(activity!!)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showWaitDialog(mContext: Context) {
        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }

    private fun sendApprovalRequest() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
        )
        jObj.addProperty("type", "Van Stock print")

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(activity!!).sendSupervisorGeoApprovalRequest(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<GeoApprovalData> {
            override fun onResponse(
                call: Call<GeoApprovalData>,
                response: Response<GeoApprovalData>
            ) {
                Log.e("Van GeoApproval Success", "Success")
                Log.e("Van GeoApproval request", response.body().toString())
                Utils.dismissDialog(mDialog!!)
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        showWaitDialog(activity!!)
                    } else {
                        Utils.displayAlert(activity!!, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(activity!!, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                Utils.dismissDialog(mDialog!!)
                Utils.displayAlert(activity!!, "Request Sent fail!!")
            }
        })
    }

    private fun acceptVanDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity!!)
        alertDialogBuilder.setTitle("Alert")
            .setMessage("Your request to print vanstock has been Approve by supervisor!")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, which ->
                dialog.dismiss()
                Utils.dialogPrint(activity!!, object : OnSearchableDialog {

                    override fun onItemSelected(selection: String?) {

                        if (selection.equals("yes", ignoreCase = true)) {
                            val jsonArray = createPrintData("")

                            val `object` = PrintLog(
                                activity!!,
                                activity!!
                            )
                            `object`.execute("", jsonArray)

                        }
                    }
                })
            }
        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        // show it
        alertDialog.show()

    }

    val broadcastReceiver_Van = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("status") != null) {
                if (intent.getStringExtra("status") == "Approve") {
                    if (intent.hasExtra("status")) {
                        if (mProgressDialog != null) {
                            if (mProgressDialog!!.isShowing)
                                mProgressDialog!!.dismiss()
                        }
                        try {
                            if (str_broadcast == "1") {
                                acceptVanDialog()
                            }
                        } catch (e: Exception) {

                        }
                    }
                } else {
                    if (intent.hasExtra("reason")) {
                        if (mProgressDialog != null) {
                            if (mProgressDialog!!.isShowing)
                                mProgressDialog!!.dismiss()
                        }
                        if (str_broadcast == "1") {
                            Utils.rejectReasonDialog(activity!!)
                        }
                    }

                }
            } else {
                Utils.displayAlert(
                    context,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            activity!!.unregisterReceiver(broadcastReceiver_Van)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Van)
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            activity!!.unregisterReceiver(broadcastReceiver_Van)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Van)
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity!!.unregisterReceiver(broadcastReceiver_Van)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Van)
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

    override fun onDetach() {
        super.onDetach()
        try {
            activity!!.unregisterReceiver(broadcastReceiver_Van)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Van)
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }


    override fun onPause() {
        super.onPause()
        str_broadcast = "0"
        try {
            activity!!.unregisterReceiver(broadcastReceiver_Van)
            LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadcastReceiver_Van)
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

}