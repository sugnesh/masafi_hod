package com.engear.vansales.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.common.SharedPreferenceManager
import com.engear.vansales.databinding.FragmentTargetBinding
import com.engear.vansales.databinding.FragmentTargetVMBinding
import com.engear.vansales.databinding.TargetDialogBinding
import com.engear.vansales.localDB.DatabaseHandler
import com.engear.vansales.model.SalesTargetData
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [TargetVMFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TargetVMFragment : Fragment() {

    lateinit var binding: FragmentTargetVMBinding
    var mPreferenceManager: SharedPreferenceManager? = null
    var db: DatabaseHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_target_v_m, container, false
        )

        mPreferenceManager = SharedPreferenceManager(activity!!)
        db = DatabaseHandler(activity!!)

        if (!db!!.getTargetCOunt()) {
            getSalesTargetData()
        } else {
            setTargetData()
        }

        binding.swcDtdMtd!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                setDailyTargetData()
            } else {
                setTargetData()
            }
        })

        return binding.root
    }

    private fun setTargetData() {

        var tituseTarget = db!!.getCategoryTargetValoume("9")
        var tituseMTD = db!!.getCategoryTargetMTDVL("9")
        binding.txtTotal.text = "Target\n$tituseTarget"
        binding.txtTISSUETotal.text = "Achived\n$tituseMTD"

        var pureTarget = db!!.getCategoryTargetValoume("11")
        var pureMTD = db!!.getCategoryTargetMTDVL("11")
        binding.txtPureTotal.text = "Target\n$pureTarget"
        binding.txtPureAcv.text = "Achived\n$pureMTD"

        var aklTarget = db!!.getCategoryTargetValoume("13")
        var aklMTD = db!!.getCategoryTargetMTDVL("13")
        binding.txtAlkTotal.text = "Target\n$aklTarget"
        binding.txtAlkAcv.text = "Achived\n$aklMTD"

        var zeroTarget = db!!.getCategoryTargetValoume("12")
        var zeroMTD = db!!.getCategoryTargetMTDVL("12")
        binding.txtZeroTotal.text = "Target\n$zeroTarget"
        binding.txtZeroAcv.text = "Achived\n$zeroMTD"

        var fourTarget = db!!.getCategoryTargetValoume("4")
        var fourMTD = db!!.getCategoryTargetMTDVL("4")
        binding.txt4GTotal.text = "Target\n$fourTarget"
        binding.txt4GAcv.text = "Achived\n$fourMTD"

        var bwipsTarget = db!!.getCategoryTargetValoume("14")
        var bwipsMTD = db!!.getCategoryTargetMTDVL("14")
        binding.txtBWPTotal.text = "Target\n$bwipsTarget"
        binding.txtBWPAcv.text = "Achived\n$bwipsMTD"

        binding.lytTissue.setOnClickListener {
            targetDialog("9")
        }

        binding.lytPure.setOnClickListener {
            targetDialog("11")
        }

        binding.lytAlkalife.setOnClickListener {
            targetDialog("13")
        }

        binding.lytZero.setOnClickListener {
            targetDialog("12")
        }

        binding.lyt4G.setOnClickListener {
            targetDialog("4")
        }

        binding.lytBMWP.setOnClickListener {
            targetDialog("14")
        }


    }

    private fun setDailyTargetData() {

        var tituseTarget = db!!.getCategoryTargetValoume("9")
        var tituseMTD = "0"
        if (tituseTarget.toDouble() > 0) {
            tituseTarget = Utils.twoDigit((tituseTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("9")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                tituseMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                tituseMTD = db!!.getItemSalesValue(mItem).toString()
            }

        }

        binding.txtTotal.text = "Target\n$tituseTarget"
        binding.txtTISSUETotal.text = "Achived\n$tituseMTD"

        var pureTarget = db!!.getCategoryTargetValoume("11")
        var pureMTD = "0"
        if (pureTarget.toDouble() > 0) {
            pureTarget = Utils.twoDigit((pureTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("11")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                pureMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                pureMTD = db!!.getItemSalesValue(mItem).toString()
            }
        }
        binding.txtPureTotal.text = "Target\n$pureTarget"
        binding.txtPureAcv.text = "Achived\n$pureMTD"

        var aklTarget = db!!.getCategoryTargetValoume("13")
        var aklMTD = "0"
        if (aklTarget.toDouble() > 0) {
            aklTarget = Utils.twoDigit((aklTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("13")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                aklMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                aklMTD = db!!.getItemSalesValue(mItem).toString()
            }
        }
        binding.txtAlkTotal.text = "Target\n$aklTarget"
        binding.txtAlkAcv.text = "Achived\n$aklMTD"

        var zeroTarget = db!!.getCategoryTargetValoume("12")
        var zeroMTD = "0"
        if (zeroTarget.toDouble() > 0) {
            zeroTarget = Utils.twoDigit((zeroTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("12")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                zeroMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                zeroMTD = db!!.getItemSalesValue(mItem).toString()
            }
        }
        binding.txtZeroTotal.text = "Target\n$zeroTarget"
        binding.txtZeroAcv.text = "Achived\n$zeroMTD"

        var fourTarget = db!!.getCategoryTargetValoume("4")
        var fourMTD = "0"
        if (fourTarget.toDouble() > 0) {
            fourTarget = Utils.twoDigit((fourTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("4")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                fourMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                fourMTD = db!!.getItemSalesValue(mItem).toString()
            }
        }
        binding.txt4GTotal.text = "Target\n$fourTarget"
        binding.txt4GAcv.text = "Achived\n$fourMTD"

        var bwipsTarget = db!!.getCategoryTargetValoume("14")
        var bwipsMTD = "0"
        if (bwipsTarget.toDouble() > 0) {
            bwipsTarget = Utils.twoDigit((bwipsTarget.toDouble() / 30).toFloat()).toString()
            var mItem = db!!.getCategoryItem("14")
            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "1") {
                bwipsMTD = db!!.getOrderItemSalesValue(mItem).toString()
            } else {
                bwipsMTD = db!!.getItemSalesValue(mItem).toString()
            }
        }
        binding.txtBWPTotal.text = "Target\n$bwipsTarget"
        binding.txtBWPAcv.text = "Achived\n$bwipsMTD"

        binding.lytTissue.setOnClickListener {
            targetDialog("9")
        }

        binding.lytPure.setOnClickListener {
            targetDialog("11")
        }

        binding.lytAlkalife.setOnClickListener {
            targetDialog("13")
        }

        binding.lytZero.setOnClickListener {
            targetDialog("12")
        }

        binding.lyt4G.setOnClickListener {
            targetDialog("4")
        }

        binding.lytBMWP.setOnClickListener {
            targetDialog("14")
        }


    }

    private fun getSalesTargetData() {
        Log.e("API CALL", "SalesTarget Call")

        val jObj = JsonObject()
        jObj.addProperty(
            "route_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.routeId.toString()
        )

        ApiClient.getClient(activity!!).getSalesTargetData(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<SalesTargetData> {
            override fun onResponse(
                call: Call<SalesTargetData>,
                response: Response<SalesTargetData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    if (response.body()!!.status!!) {
                        db!!.insertSalesTargetData(response.body()!!.data)
                    }
                }

                setTargetData()
            }

            override fun onFailure(call: Call<SalesTargetData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                setTargetData()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun targetDialog(mCategory: String) {
        val factory = LayoutInflater.from(activity!!)
        val binding: TargetDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.target_dialog, null, false)

        val deleteDialogView: View = binding.root


        val deleteDialog: AlertDialog = AlertDialog.Builder(activity!!).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        var tituseTarget = ""
        var tituseMTD = ""
        var mtdBudget = 0.0
        var balaTGT = 0.0
        var mtdPer = 0.0

        tituseTarget = db!!.getCategoryTargetValoume(mCategory)
        tituseMTD = db!!.getCategoryTargetMTDVL(mCategory)

        if (tituseTarget.toDouble() > 0) {
            mtdBudget = tituseTarget.toDouble() / 30
            balaTGT = tituseTarget.toDouble() - tituseMTD.toDouble()
            mtdPer = (tituseMTD.toDouble() / tituseTarget.toDouble()) * 100
        }


        binding.txtTarget.text = tituseTarget
        binding.txtMTDAchive.text = tituseMTD
        binding.txtMTD.text = Utils.twoDigit(mtdBudget.toFloat()).toString()
        binding.txtBalTGT.text = Utils.twoDigit(balaTGT.toFloat()).toString()
        binding.txtMTDPe.text = Utils.twoDigit(mtdPer.toFloat()).toString() + "%"

        binding.ivClose.setOnClickListener {
            deleteDialog.dismiss()
        }
        deleteDialog.show()
    }

}