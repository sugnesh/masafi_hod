package com.engear.vansales.activities

import android.R
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.engear.vansales.adapter.BadULItemAdapter
import com.engear.vansales.adapter.CategoryAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.databinding.ActivityUnloadBinding
import com.engear.vansales.databinding.DialogInputLoadVarianceBinding
import com.engear.vansales.databinding.DialogUnloadVarianceBinding
import com.engear.vansales.interfaces.OnCategoryClickListener
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.interfaces.OnSearchableDialog
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.*
import com.engear.vansales.printer.PrintLog
import com.engear.vansales.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BadReturnUnloadActivity : BaseActivity(), OnItemClickListener, OnCategoryClickListener {

    lateinit var binding: ActivityUnloadBinding
    var allLoadItem: MutableList<UomData> = mutableListOf()
    var allLoadCategoryItem: MutableList<UomData> = mutableListOf()
    var allEndLoadCategoryItem: MutableList<UomData> = mutableListOf()
    var allEndLoadItem: MutableList<UomData> = mutableListOf()
    private var countPlus = 0
    private var unloadStep = 2

    private var unloadVarieanceNumber = ""
    var mAdapter: BadULItemAdapter? = null
    var mUlAdapter: BadULItemAdapter? = null
    var loadType: String = "Van Load"
    private var unloadNumber = ""
    private var jsonArray: JSONArray? = null
    var arrEndData: ArrayList<UnloadSummary> = ArrayList()
    private var unloadBadNumber = ""
    private var isPerform: Boolean = false
    var salesman_route_print_enable: String = ""

    var arrFreshUnload: MutableList<UomData> = mutableListOf()

    var lobIdList = ""
    var isClick = false
    var isPeformClick: Boolean = false
    var mCatAdapter: CategoryAdapter? = null
    var categoryList: ArrayList<CategoryData>? = ArrayList()
    var catItemList: ArrayList<ItemData.Data>? = ArrayList()
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()

    var catPosition = 0
    var selectCatId = ""
    val arrMItem = ArrayList<ItemData.Data>()

    companion object {

        var mActivity: BadReturnUnloadActivity? = null
        var progressDialog: ProgressDialog? = null
        var badDamageItemList: ArrayList<UomData>? = ArrayList()
        var badExpiryItemList: ArrayList<UomData>? = ArrayList()
        var allBadItemList: ArrayList<UomData>? = ArrayList()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnloadBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@BadReturnUnloadActivity,
            "On Fresh Unload Screen "
        )
        mActivity = this@BadReturnUnloadActivity
        init()
    }

    private fun init() {

        setToolbarTitle("Bad Return Unload")

        binding.tvTitle.text = "Select Bad Return Unload Item"
        binding.stepView.setStepsNumber(unloadStep)
        binding.stepView.go(countPlus, true)

        progressDialog = ProgressDialog(this)

        /* allLoadItem = db!!.getAllVanItem()
          setUpRecycler()*/
        allLoadCategoryItem = db!!.getBadReturnItem()
        for (msales in allEndLoadCategoryItem) {
            for (mBadReturn in allBadItemList!!) {

                if (msales.itemId == mBadReturn.itemId) {
                    allLoadCategoryItem = allEndLoadCategoryItem

                }
            }

        }
        if (allLoadCategoryItem.isNullOrEmpty()) {

        } else {
            callAdaapter()
        }

        salesman_route_print_enable =
            mPreferenceManager!!.getString(AppConstants.PRINTENABLE).toString()

        binding.btnUnload.setOnClickListener {

            if (countPlus == 0) {

                if (!isPerform) {
                    val alertDialogBuilder =
                        android.app.AlertDialog.Builder(this@BadReturnUnloadActivity)
                    alertDialogBuilder.setTitle("Alert")
                        .setMessage("Are you sure you want to confirm unload?")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Yes"
                        ) { dialog, which ->

                            isPerform = true
                            countPlus++
                            binding.stepView.go(countPlus, true)
                            updateData(countPlus)
                        }
                        .setNegativeButton(
                            "No"
                        ) { dialog, which ->
                            dialog.dismiss()
                        }

                    alertDialogBuilder.show()
                }

            } else {
                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(this@BadReturnUnloadActivity)
                alertDialogBuilder.setTitle("Alert")
                    .setMessage("Are you sure you want to Unload?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        val task = getAsyncTask(this@BadReturnUnloadActivity)
                        task.execute("")
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, which ->
                        dialog.dismiss()
                    }

                alertDialogBuilder.show()
            }
        }

      //  badDamageItemList = db!!.getBadReturnDamageItem()
        badExpiryItemList = db!!.getBadReturnExpiryItem()
       // allBadItemList = db!!.getBadReturnItem()
    }

    //Category

    fun callAdaapter() {
        for (mLobId in allLoadCategoryItem) {
            if (lobIdList.isEmpty()) {
                lobIdList = mLobId.itemId.toString()
            } else {
                lobIdList = lobIdList + "," + mLobId.itemId.toString()
            }
        }

        categoryList = ArrayList()
        categoryList = db!!.getAllLoadConfirmCategory(lobIdList)
        itemsList = db!!.getAllReturnCategoryItem(lobIdList)

        setUpCategoryRecycler()
    }

    private fun setUpCategoryRecycler() {
        binding.rvCategory!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mCatAdapter = CategoryAdapter(this, categoryList!!)
        binding.rvCategory!!.adapter = mCatAdapter

        categoryList!![catPosition].isClick = "1"
        selectCatId = categoryList!![catPosition].catId.toString()

        catItemList = ArrayList()
        catItemList = getCategoryItem(selectCatId)
    }


    private fun getCategoryItem(catId: String): ArrayList<ItemData.Data>? {
        allLoadItem.clear()

        for (allitem in allLoadCategoryItem) {
            for (i in itemsList!!) {
                if (i.majorCategory.toString() == catId) {
                    if (allitem.itemName == i.itemName) {
                        if (allLoadItem.contains(allitem)) {

                        } else {
                            arrMItem.add(i)
                            allLoadItem.add(allitem)
                        }

                    }
                }

            }
        }
        if (countPlus == 1) {
            updateData(1)
        } else {
            setUpRecycler()
        }


        return arrMItem
    }

    private fun setUpRecycler() {
        loadType = "Unload"
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = BadULItemAdapter(this, allLoadItem)
        binding.rvList.adapter = mAdapter
    }

    private fun setUpULRecycler() {

        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mUlAdapter = BadULItemAdapter(this, allEndLoadItem)
        binding.rvList.adapter = mUlAdapter
    }

    private fun updateData(countPlus: Int) {
        setUpRecycler()
        if (countPlus == (unloadStep - 1)) {
            binding.tvTitle.text =
                "All Remaining item consider as End Inventory(Carry Over)"
            binding.btnUnload.setImageResource(com.engear.vansales.R.drawable.ic_check_white)
            allEndLoadItem!!.clear()
            allEndLoadItem = ArrayList()
            //  getEndItem()

            // setUpULRecycler()
        }
    }

    private fun getEndItem() {
        allEndLoadItem!!.clear()
        allEndLoadItem = ArrayList()
        for (mLoadItem in allLoadItem!!) {
            if (mLoadItem.actualQty.toDouble() > 0) {

                var isLower = false

                if (mLoadItem.uomId == mLoadItem.uomId) {
                    isLower = true
                    mLoadItem.enterQty = mLoadItem.actualQty
                } else {
                    var mDisplayQty =
                        Utils.getUOMQty(
                            db!!.getItemUOMUPC(
                                mLoadItem.itemId,
                                mLoadItem.uomId
                            ), mLoadItem.actualQty
                        )
                    var mDQty = mDisplayQty.toDouble().toInt()

                    var mDisplayTotalQty = Utils.getUOMLowerQty(
                        db!!.getItemUOMUPC(mLoadItem.itemId, mLoadItem.uomId),
                        mDQty.toString()
                    )

                    if (mDisplayTotalQty.toDouble() != mLoadItem.actualQty.toDouble()) {
                        isLower = true
                        mLoadItem.enterQty = mLoadItem.actualQty
                    } else {
                        mLoadItem.enterQty = mDQty.toString()
                    }
                }


                mLoadItem.varianceBatch = emptyList()
                /* if (isLower) {
                     mLoadItem.primary_qty = "1"
                 } else {
                     mLoadItem.primary_qty = "0"
                 }*/
                allEndLoadItem.add(mLoadItem)
            }
        }
    }


    override fun onItemsClick(position: Int, tag: String) {
        if (countPlus == 0) {
            makeDilog(this, allLoadItem[position], position)
        } else {
            makeULDilog(this, allLoadItem[position], position)
        }
    }

    private fun makeDilog(context: Context, item: UomData, position: Int) {
        val reasons = listOf("Select Reason", "ROT", "Truck Damage", "Van Expiry", "Theft/Variance")
        val dataAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, reasons).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val dialogBinding = DialogInputLoadVarianceBinding.inflate(LayoutInflater.from(context))
        val dialogView = dialogBinding.root
        dialogBinding.spReason.adapter = dataAdapter
        dialogBinding.txtName.text = item.itemName
        dialogBinding.llSelling.visibility = View.GONE

        val alertDialog = AlertDialog.Builder(context).create().apply {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val mUom = db?.getItemUOMDetail(item.itemId, item.uomId)
        val isLower = true // `if (item.uomId == item.uomId)` is always true

        if (isLower && mUom != null) {
            dialogBinding.edtSaleAlter.setText("${item.uomQty} ${mUom.uom.name}")
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val qtyText = dialogBinding.edtActAlter.text.toString()
            if (qtyText.isEmpty()) return@setOnClickListener

            val inputQty = qtyText.toDoubleOrNull() ?: 0.0
            val originalQty = item.uomQty.toDoubleOrNull() ?: 0.0

            val lowerQty = if (isLower) {
                inputQty
            } else {
                Utils.getUOMLowerQty(
                    db!!.getItemUOMUPC(item.itemId, item.uomId),
                    qtyText
                ).toDouble()
            }

            if (lowerQty > originalQty) {
                Toast.makeText(context, "You cannot enter qty more than system Qty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val difference = originalQty - lowerQty
            allLoadItem[position].actualQty = difference.toString()
            allLoadItem[position].enterQty = qtyText

            val arrBatchs = allLoadItem[position].batchs ?: listOf()
            val vanBatch = allLoadItem[position].varianceBatch?.toMutableList() ?: mutableListOf()
            val varianceBatch = mutableListOf<BatchdetailsList>()

            if (lowerQty > 0) {
                var remainingQty = lowerQty

                for (batch in arrBatchs) {
                    if (remainingQty <= 0) break

                    val availableQty = batch.qty.toDoubleOrNull() ?: continue
                    val usedQty = if (availableQty >= remainingQty) remainingQty else availableQty

                    varianceBatch.add(
                        BatchdetailsList(
                            batch.batchId,
                            batch.batchNumber,
                            batch.expiryDate,
                            usedQty.toString(),
                            "", "", ""
                        )
                    )

                    remainingQty -= usedQty
                }

                // Adjust vanBatch with varianceBatch
                for (vBatch in varianceBatch) {
                    val index = vanBatch.indexOfFirst { it.batchNumber == vBatch.batchNumber }
                    if (index != -1) {
                        val existingQty = vanBatch[index].qty.toDoubleOrNull() ?: 0.0
                        val usedQty = vBatch.qty.toDoubleOrNull() ?: 0.0
                        vanBatch[index].qty = (existingQty - usedQty).coerceAtLeast(0.0).toString()
                    }
                }

                allLoadItem[position].varianceBatch = varianceBatch
                arrFreshUnload.removeAll { it.itemId == item.itemId }
                arrFreshUnload.add(allLoadItem[position])
            }

            mAdapter?.notifyDataSetChanged()
            alertDialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun makeULDilog(context: Context, item: UomData, position: Int) {
        val dialogBinding = DialogUnloadVarianceBinding.inflate(LayoutInflater.from(context))
        val dialogView = dialogBinding.root
        dialogBinding.txtName.text = item.itemName

        val alertDialog = AlertDialog.Builder(context).create().apply {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val mUom = db?.getItemUOMDetail(item.itemId, item.uomId)

        dialogBinding.btnConfirm.setOnClickListener {
            val freshQtyText = dialogBinding.edtActAlter.text.toString().trim()
            val carryoverQtyText = dialogBinding.edtCarActAlter.text.toString().trim()

            if (freshQtyText.isEmpty()) {
                Toast.makeText(context, "Please enter fresh Unload qty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (carryoverQtyText.isEmpty()) {
                Toast.makeText(context, "Please enter carryover qty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val freshQty = freshQtyText.toDoubleOrNull()
            val carryoverQty = carryoverQtyText.toDoubleOrNull()
            val systemQty = item.uomQty.toDoubleOrNull()

            if (freshQty == null || carryoverQty == null || systemQty == null) {
                Toast.makeText(context, "Invalid numeric input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalQty = freshQty + carryoverQty

            if (totalQty > systemQty) {
                Toast.makeText(context, "You cannot enter qty more than system Qty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            allLoadItem[position].apply {
                actualQty = freshQty.toString()
                enterQty = carryoverQty.toString()
                varianceBatch = emptyList()
                // Uncomment below if needed
                // loadQty = Utils.getUOMLowerQty(mUom?.uomUPC ?: "", totalQty.toString())
            }

            mUlAdapter?.notifyDataSetChanged()
            alertDialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    class getAsyncTask internal constructor(
        context: BadReturnUnloadActivity
    ) :
        AsyncTask<String, String, String?>() {

        private val activityReference: WeakReference<BadReturnUnloadActivity> =
            WeakReference(context)

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val activity = activityReference.get()
            if (progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }

            if (activity!!.salesman_route_print_enable == "1") {
                activity!!.printDialog()
            } else {
                activity!!.finish()
            }

        }

        override fun doInBackground(vararg params: String?): String? {

            val activity = activityReference.get()


            activity!!.addVariance()


//            if (activity!!.unloadVarieanceNumber.isNotEmpty()) {
//                activity!!.insertVarianceUnloadTransaction()
//            }

            activity.parseEndInventory()

            activity!!.insertUnloadTransaction()

            activity!!.addBadVariance()

            return ""
        }

        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()
            if (activity != null) {
                progressDialog!!.setMessage("Please wait...")
                progressDialog!!.show()
            }
        }
    }

    private fun addVariance() {

        //Add Collection
        unloadNumber =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )


        var allVariance: MutableList<UomData> = mutableListOf()
        for (mLoadItem in arrFreshUnload) {
            // if (mLoadItem.varianceQty.isNotEmpty()) {
            if (mLoadItem.enterQty.toDouble() > 0) {

                var vanBatch: List<BatchdetailsList> = mLoadItem.batchs!!
                var varianceBatch: MutableList<BatchdetailsList> = mutableListOf()

                if (mLoadItem.enterQty.toDouble() > 0) {
                    var arrBatchs: List<BatchdetailsList> = mutableListOf()
                    arrBatchs = mLoadItem.batchs!!

                 /*   Collections.sort(
                        arrBatchs
                    ) { o1, o2 ->
                        val format: DateFormat =
                            SimpleDateFormat("yyyy-MM-dd")
                        var mm: Date? = null
                        var nn: Date? = null
                        try {
                            mm = format.parse(o1.expiryDate)
                            nn = format.parse(o2.expiryDate)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                        nn!!.compareTo(mm)
                    }*/

                    var varncQTy = "0"
                    /* if (mLoadItem.primary_qty == "1") {
                         varncQTy = mLoadItem.enterQty
                     } else {
                         varncQTy = Utils.getUOMLowerQty(
                             db!!.getItemUOMUPC(
                                 mLoadItem.itemId,
                                 mLoadItem.displayUomId
                             ), mLoadItem.enterQty.toString()
                         )
                     }*/
                    varncQTy = mLoadItem.enterQty
                    var remaingQTy = varncQTy.toDouble()

                    for (mVBatch in arrBatchs) {

                        var mBatchdetailsList: BatchdetailsList? = null
                        if (remaingQTy > 0) {
                            if (mVBatch.qty.toDouble() < remaingQTy) {
                                mBatchdetailsList = BatchdetailsList(
                                    mVBatch.batchId,
                                    mVBatch.batchNumber,
                                    mVBatch.expiryDate,
                                    mVBatch.qty,
                                    "",
                                    "",
                                    ""
                                )
                                remaingQTy -= mVBatch.qty.toDouble()
                            } else if (mVBatch.qty.toDouble() > remaingQTy) {
                                mBatchdetailsList = BatchdetailsList(
                                    mVBatch.batchId,
                                    mVBatch.batchNumber,
                                    mVBatch.expiryDate,
                                    remaingQTy.toString(),
                                    "",
                                    "",
                                    ""
                                )
                                remaingQTy -= remaingQTy
                            } else {
                                mBatchdetailsList = BatchdetailsList(
                                    mVBatch.batchId,
                                    mVBatch.batchNumber,
                                    mVBatch.expiryDate,
                                    mVBatch.qty,
                                    "",
                                    "",
                                    ""
                                )
                                remaingQTy -= mVBatch.qty.toDouble()
                            }

                            varianceBatch.add(mBatchdetailsList!!)
                        } else {
                            break
                        }

                    }

                    for (mVRBatch in varianceBatch!!) {
                        for (mVanBatch in vanBatch!!) {
                            var index = vanBatch.indexOf(mVanBatch)
                            if (mVRBatch.batchNumber == mVanBatch.batchNumber) {
                                if (mVRBatch.qty == mVanBatch.qty) {
                                    vanBatch!![index].qty = "0"
                                } else {
                                    var mUpQty =
                                        mVanBatch.qty.toDouble() - mVRBatch.qty.toDouble()
                                    vanBatch!![index].qty = mUpQty.toString()
                                }
                            }
                        }
                    }
                }

                val mItem = UomData()
                mItem.itemId = mLoadItem.itemId
                mItem.itemName = mLoadItem.itemName
                mItem.itemCode = mLoadItem.itemCode
                mItem.itemCode = mLoadItem.itemCode
                //mItem.uomQty = mLoadItem.actualQty
                mItem.uomQty = mLoadItem.enterQty
                mItem.uomType = mLoadItem.uomType
                mItem.reason = ""
                mItem.uomId = mLoadItem.uomId
                try {
                    // mItem.batchNo = varianceBatch!![0].batchNumber
                    mItem.invNo = varianceBatch!![0].qty
                }catch (e:Exception){

                }
                allVariance.add(mItem)

               /* if (varianceBatch!!.size > 1) {

                    for (mItemBatch in varianceBatch!!) {
                        val mItem = UomData()
                        mItem.itemId = mLoadItem.itemId
                        mItem.itemName = mLoadItem.itemName
                        mItem.itemCode = mLoadItem.itemCode
                        mItem.itemCode = mLoadItem.itemCode
                        mItem.uomQty = mItemBatch.qty
                        mItem.uomType = mLoadItem.uomType
                        mItem.reason = ""
                        mItem.uomId = mLoadItem.uomId
                        mItem.batchNo = mItemBatch.batchNumber
                        mItem.invNo = mItemBatch.qty
                        allVariance.add(mItem)
                    }
                } else {
                    val mItem = UomData()
                    mItem.itemId = mLoadItem.itemId
                    mItem.itemName = mLoadItem.itemName
                    mItem.itemCode = mLoadItem.itemCode
                    mItem.itemCode = mLoadItem.itemCode
                    //mItem.uomQty = mLoadItem.actualQty
                    mItem.uomQty = mLoadItem.enterQty
                    mItem.uomType = mLoadItem.uomType
                    mItem.reason = ""
                    mItem.uomId = mLoadItem.uomId
                    try {
                        mItem.batchNo = varianceBatch!![0].batchNumber
                        mItem.invNo = varianceBatch!![0].qty
                    } catch (e: Exception) {

                    }
                    allVariance.add(mItem)
                }*/

            }
            // }
        }

        if (allEndLoadItem.size > 0) {
            var mType = "3"
            db!!.insertFreshItemUnload(
                unloadNumber,
                "3",
                mType,
                mPreferenceManager!!.getTripId(),
                allVariance!!
            )
        } else {
            var mType = "3"
            db!!.insertFreshItemUnload(
                unloadNumber,
                "3",
                mType,
                mPreferenceManager!!.getTripId(),
                allVariance!!
            )
        }

        //set Order Last Number
        mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)


    }

    private fun addBadVariance() {
        getEndItem()

        //Add Collection
        unloadNumber =
            Utils.getLastIndexNew(
                mPreferenceManager!!.getString(AppConstants.UNLOAD_LAST).toString(), "unload"
            )


        var allVariance: MutableList<UomData> = mutableListOf()

        allVariance.clear()


        for (mLoadItem in allEndLoadItem) {
            System.out.println("Check--> "+mLoadItem.enterQty)
            if (mLoadItem.enterQty.isNotEmpty()) {
                if (mLoadItem.enterQty.toDouble() > 0) {

                    if (mLoadItem.enterQty.toDouble() == mLoadItem.actualQty.toDouble()) {
                        val mItem = UomData()
                        mItem.itemId = mLoadItem.itemId
                        mItem.itemName = mLoadItem.itemName
                        mItem.itemCode = mLoadItem.itemCode
                        mItem.uomQty = mLoadItem.actualQty
                        mItem.uomType = mLoadItem.uomType
                        mItem.reason = mLoadItem.reason
                        mItem.uomId = mLoadItem.uomId
                        allVariance.add(mItem)
                        /*  if (mLoadItem.batchs!!.size > 1) {

                              for (mItemBatch in mLoadItem.batchs!!) {

                                  if (mItemBatch.qty.toDouble() > 0) {
                                      val mItem = UomData()
                                      mItem.itemId = mLoadItem.itemId
                                      mItem.itemName = mLoadItem.itemName
                                      mItem.itemCode = mLoadItem.itemCode
                                      mItem.uomQty = mItemBatch.qty
                                      mItem.uomType = mLoadItem.uomType
                                      mItem.reason = mLoadItem.reason
                                      mItem.uomId = mLoadItem.uomId
                                      mItem.batchNo = mItemBatch.batchNumber
                                      mItem.invNo = mItemBatch.qty
                                      allVariance.add(mItem)
                                  }

                              }
                          } else {
                              try {
                                 // if (mLoadItem.batchs!![0].qty.toDouble() > 0) {
                                      val mItem = UomData()
                                      mItem.itemId = mLoadItem.itemId
                                      mItem.itemName = mLoadItem.itemName
                                      mItem.itemCode = mLoadItem.itemCode
                                      mItem.uomQty = mLoadItem.actualQty
                                      // mItem.uomQty = mLoadItem.enterQty
                                      mItem.uomType = mLoadItem.uomType
                                      mItem.reason = mLoadItem.reason
                                      mItem.uomId = mLoadItem.uomId
                                      mItem.batchNo = mLoadItem.batchs!![0].batchNumber
                                      mItem.invNo = mLoadItem.batchs!![0].qty
                                      allVariance.add(mItem)

                              } catch (e: Exception) {
                                  val mItem = UomData()
                                  mItem.itemId = mLoadItem.itemId
                                  mItem.itemName = mLoadItem.itemName
                                  mItem.itemCode = mLoadItem.itemCode
                                  mItem.uomQty = mLoadItem.actualQty
                                  // mItem.uomQty = mLoadItem.enterQty
                                  mItem.uomType = mLoadItem.uomType
                                  mItem.reason = mLoadItem.reason
                                  mItem.uomId = mLoadItem.uomId
                                  mItem.batchNo =mLoadItem.batchs!![0].batchNumber
                                  mItem.invNo = ""
                                  allVariance.add(mItem)
                              }

                          }*/

                    } else {

                        val mItem = UomData()
                        mItem.itemId = mLoadItem.itemId
                        mItem.itemName = mLoadItem.itemName
                        mItem.itemCode = mLoadItem.itemCode
                        mItem.uomQty = mLoadItem.actualQty
                        mItem.uomType = mLoadItem.uomType
                        mItem.reason = mLoadItem.reason
                        mItem.uomId = mLoadItem.uomId
                        /*  mItem.batchNo = mItemBatch.batchNumber
                          mItem.invNo = mItemBatch.qty*/
                        allVariance.add(mItem)
                        /*   for (mItemBatch in mLoadItem.batchs!!) {

                             //  if (mItemBatch.qty.toDouble() > 0) {
                                   val mItem = UomData()
                                   mItem.itemId = mLoadItem.itemId
                                   mItem.itemName = mLoadItem.itemName
                                   mItem.itemCode = mLoadItem.itemCode
                                   mItem.uomQty = mLoadItem.actualQty
                                   mItem.uomType = mLoadItem.uomType
                                   mItem.reason = mLoadItem.reason
                                   mItem.uomId = mLoadItem.uomId
                                 *//*  mItem.batchNo = mItemBatch.batchNumber
                                mItem.invNo = mItemBatch.qty*//*
                                allVariance.add(mItem)
                           // }

                        }*/
                    }

                }
            }
        }

        var mType = "7"
        if (allEndLoadItem.size > 0) {
            db!!.insertFreshItemUnload(
                unloadNumber,
                "7",
                mType,
                mPreferenceManager!!.getTripId(),
                allVariance!!
            )


            //set Order Last Number
            mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)
            insertUnloadVarianceTransaction()
        }

    }

    private fun printDialog() {

        Utils.dialogPrint(mActivity!!, object : OnSearchableDialog {

            override fun onItemSelected(selection: String?) {

                if (selection.equals("yes", ignoreCase = true)) {
                    val `object` = PrintLog(
                        this@BadReturnUnloadActivity,
                        this@BadReturnUnloadActivity
                    )
                    `object`.execute("", jsonArray)
                } else {
                    finish()
                }
            }
        })
    }


    private fun insertUnloadTransaction() {

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintUnloadData(unloadNumber)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        //INSERT TRANSACTION
        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_UNLOAD
        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = unloadNumber
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        //set Order Last Number
        //mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)

        db!!.markForPostUnload()

        /*mPreferenceManager!!.setString(Constants.IS_UNLOAD, "2")
        mPreferenceManager!!.setString(Constants.IS_ENDDAY, "1")*/
        mPreferenceManager!!.setString(Constants.IS_UNLOAD_BAD_POST, "1")

        if (db!!.getPostUnBadRequestCount() === 0) {
            db!!.updateUnloadTransaction(unloadNumber, "1", "")
            mPreferenceManager!!.setString(Constants.IS_UNLOAD_POST, "2")
        }

    }

    private fun insertUnloadVarianceTransaction() {

        //get Print Data
        var data: JSONObject? = null
        try {
            jsonArray = createPrintUnloadData(unloadNumber)
            data = JSONObject()
            data.put("data", jsonArray as JSONArray?)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        //INSERT TRANSACTION
        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_UNLOAD
        transaction.tr_date_time =
            Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = unloadNumber
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = data.toString()
        db!!.insertTransaction(transaction)

        //set Order Last Number
        //mPreferenceManager!!.setString(AppConstants.UNLOAD_LAST, unloadNumber)

        db!!.markForPostUnload()

        mPreferenceManager!!.setString(Constants.IS_UNLOAD, "1")
        // mPreferenceManager!!.setString(Constants.IS_ENDDAY, "1")

        // if (mPreferenceManager!!.getString(Constants.IS_ENDDAY) == "1") {
        if (db!!.getPostUnLoadAuditVarianceCount() === 0) {
            db!!.updateUnloadTransaction(unloadNumber, "1", "")
            mPreferenceManager!!.setString(Constants.IS_UNLOAD_POST, "1")
        }
        // }

    }


    private fun parseEndInventory() {

        arrEndData = ArrayList()

        var vanItem = db!!.getAllVanSalesItemList()

        for (mItem in vanItem) {

            var mUnloadSM = UnloadSummary()
            mUnloadSM.itemCode = mItem.itemCode
            var isResale = "0"
            if (mItem.itemIsReSale == "1") {
                isResale = "1"
                mUnloadSM.name = "(Resalable) " + mItem.itemName
            } else {
                mUnloadSM.name = mItem.itemName
            }

            mUnloadSM.uom = mItem.itemUOMName
            mUnloadSM.loadQty = mItem.itemQty

            var saleQty = db!!.getSaleItemQty(mItem.itemId.toString(), isResale)
            var returnQTy = db!!.getReturnItemQty(mItem.itemId.toString(), isResale)
            var expTy = db!!.getExpiryReturnItemQty(mItem.itemId.toString())
            var unloadQTy = db!!.getUnloadItemQty(mItem.itemId.toString(), isResale)
            var uvaraieenceQTy = db!!.getUnloadVarienaceItemQty(mItem.itemId.toString(), isResale)
            var freshQty = db!!.getUnloadFreshItemQty(mItem.itemId.toString(), isResale)
            mUnloadSM.salesQty = saleQty.toString()
            mUnloadSM.returnQty = returnQTy.toString()
            mUnloadSM.expiryhQty = expTy.toString()
            mUnloadSM.unloadQty = unloadQTy.toString()
            mUnloadSM.variance = uvaraieenceQTy.toString()
            mUnloadSM.freshQty = freshQty.toString()



            arrEndData.add(mUnloadSM)
        }

        if (FreshUnloadActivity.allBadItemList!!.size > 0) {
            for (mBadItem in FreshUnloadActivity.allBadItemList!!) {
                var isCOntain = false
                for (mEndItem in arrEndData) {
                    if (mEndItem.itemCode == mBadItem.itemCode) {
                        isCOntain = true
                    }
                }

                if (!isCOntain) {
                    var mUnloadSM = UnloadSummary()
                    mUnloadSM.itemCode = mBadItem.itemCode
                    mUnloadSM.name = mBadItem.itemName
                    mUnloadSM.uom = mBadItem.uomType
                    mUnloadSM.loadQty = "0.0"
                    mUnloadSM.salesQty = "0.0"
                    var expTy = db!!.getExpiryReturnItemQty(mBadItem.itemId.toString())
                    mUnloadSM.expiryhQty = expTy.toString()
                    var returnQTy = db!!.getReturnItemQty(mBadItem.itemId.toString(), "0")
                    mUnloadSM.returnQty = returnQTy.toString()
                    mUnloadSM.unloadQty = "0.0"
                    mUnloadSM.variance = "0.0"
                    mUnloadSM.freshQty = "0.0"
                    arrEndData.add(mUnloadSM)
                }
            }
        }
    }


    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintUnloadData(invNum: String?): JSONArray? {

        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.END_INVENTORY)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Load Qty")
            HEADERS.put("Sale Qty")
            HEADERS.put("Return Qty")
            HEADERS.put("Variance Qty")
            HEADERS.put("Fresh Qty")
            HEADERS.put("Unload Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (obj in arrEndData!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(obj.name) // DESC
                data.put(obj.uom) // UOM
                data.put(obj.loadQty) //Load Qty
                data.put(obj.salesQty) //Sale Qty
                data.put(obj.returnQty) //UnLoadQty
                data.put(obj.variance) //Bad Qty
                data.put(obj.freshQty) //fresh Qty
                data.put(obj.unloadQty) //VR Qty
                data.put(obj.expiryhQty) //Ex Qty
                jData.put(data)
            }
            mainArr.put("data", jData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintBadDamagedData(invNum: String?, mItemList: List<UomData>): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.UNLOAD_DAMAGED_REQUEST)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val TOTAL = JSONArray()
            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (mItemData in mItemList!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItemData.itemName) // DESC
                data.put(mItemData.uomType) // UOM
                data.put(mItemData.uomQty) //  QTY
                jData.put(data)

            }
            mainArr.put("data", jData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    /*****************************************************************************************
     * @ Creating data for print and saving it in DB for future printing
     */
    fun createPrintBadExpiryData(invNum: String?, mItemList: List<UomData>): JSONArray? {
        val jArr = JSONArray()
        try {
            val jInter = JSONArray()
            val jDict = JSONObject()
            jDict.put(AppConstants.REQUEST, AppConstants.UNLOAD_EXPIRY_REQUEST)
            val mainArr = JSONObject()
            mainArr.put("ROUTE", mPreferenceManager!!.getString(AppConstants.ROUTENAME))
            mainArr.put("DOC DATE", Utils.formatDate(Date(), "dd-MM-yyyy"))
            mainArr.put("TIME", Utils.formatTime(Date(), "hh:mm"))
            mainArr.put("SALESMAN", mPreferenceManager!!.getString(AppConstants.DRIVERNAME))
            mainArr.put("CONTACTNO", "-")
            mainArr.put("DOCUMENT NO", invNum) //Load Summary No
            mainArr.put("TRIP START DATE", mPreferenceManager!!.getTripDate())
            mainArr.put("supervisorname", "-")
            mainArr.put("orderNo", invNum) //Invoice No

            val HEADERS = JSONArray()
            HEADERS.put("Sr No")
            HEADERS.put("Description")
            HEADERS.put("Unit")
            HEADERS.put("Qty")

            mainArr.put("HEADERS", HEADERS)

            //SALES INVOICE
            val jData = JSONArray()
            for (mItemData in mItemList!!) {

                //Add Base UOM
                val data = JSONArray()
                data.put("" + (jData.length() + 1))
                data.put(mItemData.itemName) // DESC
                data.put(mItemData.uomType) // UOM
                data.put(mItemData.uomQty) //  QTY
                jData.put(data)

            }
            mainArr.put("data", jData)
            jDict.put("mainArr", mainArr)
            jInter.put(jDict)
            jArr.put(jInter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jArr
    }

    override fun onCategoryClick(position: Int, tag: String) {
        categoryList!![position].isClick = "1"
        categoryList!![catPosition].isClick = "0"
        catPosition = position
        mCatAdapter!!.notifyDataSetChanged()
        selectCatId = categoryList!![catPosition].catId.toString()
        catItemList = getCategoryItem(selectCatId)

    }
}

