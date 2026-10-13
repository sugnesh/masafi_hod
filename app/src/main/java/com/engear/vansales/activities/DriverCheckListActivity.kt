package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.Constants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDashboardBinding
import com.engear.vansales.databinding.ActivityDriverCheckListBinding
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverCheckListActivity : BaseActivity() {

    lateinit var binding: ActivityDriverCheckListBinding
    var mStartTime: String = ""
    var mOdometer: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverCheckListBinding.inflate(
            LayoutInflater.from(this), baseBinding.frameContainer, true
        )
        Utils.logData(
            this@DriverCheckListActivity, "On CheckList Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("CheckList Form")

        mStartTime = intent.getStringExtra("Time")!!
        mOdometer = intent.getStringExtra("Reading")!!

        binding.editTextOdometer.setText(mOdometer)

        binding.btnSave.setOnClickListener {

            if (binding.editTextOdometer.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter Odometer", Toast.LENGTH_SHORT)
                    .show()
            } else if (binding.editTextSiteCode.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter Vehicle No", Toast.LENGTH_SHORT)
                    .show()
            } else {
                checkList()
            }
        }
    }

    private fun checkList() {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("employee_id", mPreferenceManager!!.getUserId())
        jObj.addProperty("start_time", mStartTime)
        jObj.addProperty("location_operating_from", "")
        jObj.addProperty("vehicle_plate_number", binding.editTextSiteCode.text.toString())
        jObj.addProperty("odometer_km_reading", binding.editTextOdometer.text.toString())
        jObj.addProperty(
            "employee_name",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.firstname + " " + mPreferenceManager!!.getUserInfo()!!.data.userInfo.lastname
        )

        var mUniform = ""
        var selectedRadioButtonId: Int = binding.rgUniform.checkedRadioButtonId;
        if (selectedRadioButtonId != -1) {
            var selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
            if (selectedRadioButton.text.toString() == "Yes Available") {
                mUniform = "1"
            } else {
                mUniform = "0"
            }
        }

        var mSafety = ""
        var seSafetyId: Int = binding.rgSafety.checkedRadioButtonId;
        if (seSafetyId != -1) {
            var selcSafetyRadio: RadioButton = findViewById(seSafetyId)
            if (selcSafetyRadio.text.toString() == "Yes Available") {
                mSafety = "1"
            } else {
                mSafety = "0"
            }
        }

        var mHelperUniform = ""
        var seHelperId: Int = binding.rgHelperUniform.checkedRadioButtonId;
        if (seHelperId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seHelperId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mHelperUniform = "1"
            } else {
                mHelperUniform = "0"
            }
        }

        var mHelperSafety = ""
        var seHelperSafteyId: Int = binding.rgShoes1.checkedRadioButtonId;
        if (seHelperSafteyId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seHelperSafteyId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mHelperSafety = "1"
            } else {
                mHelperSafety = "0"
            }
        }

        var mRcard = ""
        var seRcardId: Int = binding.rgCard.checkedRadioButtonId;
        if (seRcardId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seRcardId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mRcard = "1"
            } else {
                mRcard = "0"
            }
        }

        var mRPermit = ""
        var sePermitId: Int = binding.rgDocument.checkedRadioButtonId;
        if (sePermitId != -1) {
            var selcHelperRadio: RadioButton = findViewById(sePermitId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mRPermit = "1"
            } else {
                mRPermit = "0"
            }
        }

        var mFire = ""
        var seFireId: Int = binding.rgFire.checkedRadioButtonId;
        if (seFireId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seFireId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mFire = "1"
            } else {
                mFire = "0"
            }
        }

        var mEmergency = ""
        var seEmergId: Int = binding.rgEmergency.checkedRadioButtonId;
        if (seEmergId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seEmergId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mEmergency = "1"
            } else {
                mEmergency = "0"
            }
        }

        var mSpare = ""
        var seSpareId: Int = binding.rgSpare.checkedRadioButtonId;
        if (seSpareId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seSpareId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mSpare = "1"
            } else {
                mSpare = "0"
            }
        }

        var mSticker = ""
        var seSrickerId: Int = binding.rgReflective.checkedRadioButtonId;
        if (seSrickerId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seSrickerId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mSticker = "1"
            } else {
                mSticker = "0"
            }
        }

        var mComplain = ""
        var seComplainId: Int = binding.rgComplain.checkedRadioButtonId;
        if (seComplainId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seComplainId)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mComplain = "1"
            } else {
                mComplain = "0"
            }
        }

        var mStatement8 = ""
        var seStatement8Id: Int = binding.rgStatement8.checkedRadioButtonId;
        if (seStatement8Id != -1) {
            var selcHelperRadio: RadioButton = findViewById(seStatement8Id)
            if (selcHelperRadio.text.toString() == "Yes Available") {
                mStatement8 = "1"
            } else {
                mStatement8 = "0"
            }
        }

        var mLight = ""
        var seLightId: Int = binding.rgLight.checkedRadioButtonId;
        if (seLightId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seLightId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mLight = "1"
            } else {
                mLight = "0"
            }
        }

        var mOil = ""
        var seOilId: Int = binding.rgOil.checkedRadioButtonId;
        if (seOilId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seOilId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mOil = "1"
            } else {
                mOil = "0"
            }
        }

        var mRaditor = ""
        var seRadiorId: Int = binding.rgRadiator.checkedRadioButtonId;
        if (seRadiorId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seRadiorId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mRaditor = "1"
            } else {
                mRaditor = "0"
            }
        }

        var mSteering = ""
        var seSteeringId: Int = binding.rgSteering.checkedRadioButtonId;
        if (seSteeringId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seSteeringId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mSteering = "1"
            } else {
                mSteering = "0"
            }
        }

        var mAir = ""
        var seAirId: Int = binding.rgTyre.checkedRadioButtonId;
        if (seAirId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seAirId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mAir = "1"
            } else {
                mAir = "0"
            }
        }

        var mSmoke = ""
        var seSmokeId: Int = binding.rgSmock.checkedRadioButtonId;
        if (seSmokeId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seSmokeId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mSmoke = "1"
            } else {
                mSmoke = "0"
            }
        }

        var mInterior = ""
        var seInteriroId: Int = binding.rgInterior.checkedRadioButtonId;
        if (seInteriroId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seInteriroId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mInterior = "1"
            } else {
                mInterior = "0"
            }
        }

        var mExterior = ""
        var seExteriorId: Int = binding.rgExterior.checkedRadioButtonId;
        if (seExteriorId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seExteriorId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mExterior = "1"
            } else {
                mExterior = "0"
            }
        }

        var mService = ""
        var seServiceId: Int = binding.rgOilService.checkedRadioButtonId;
        if (seServiceId != -1) {
            var selcHelperRadio: RadioButton = findViewById(seServiceId)
            if (selcHelperRadio.text.toString() == "Yes") {
                mService = "1"
            } else {
                mService = "0"
            }
        }


        jObj.addProperty("driver_uniform_shirt_pant", mUniform)
        jObj.addProperty("driver_safety_shoes", mSafety)
        jObj.addProperty("helper_uniform_shirt_pant", mHelperUniform)
        jObj.addProperty("helper_safety_shoes", mHelperSafety)
        jObj.addProperty("registration_card", mRcard)
        jObj.addProperty("distribution_permit", mRPermit)
        jObj.addProperty("fire_extinguisher", mFire)
        jObj.addProperty("emergency_triangle", mEmergency)
        jObj.addProperty("spare_tyre", mSpare)
        jObj.addProperty("reflective_sticker", mSticker)
        jObj.addProperty("driver_complain_sticker", mComplain)
        jObj.addProperty("statement_8", mStatement8)
        jObj.addProperty("all_lights_working", mLight)
        jObj.addProperty("engine_oil_check", mOil)
        jObj.addProperty("radiator_coolant_check", mRaditor)
        jObj.addProperty("steering_oil_check", mSteering)
        jObj.addProperty("tyre_air_pressure_check", mAir)
        jObj.addProperty("exhaust_smoke_check", mSmoke)
        jObj.addProperty("interior_cleaning", mInterior)
        jObj.addProperty("exterior_cleaning", mExterior)
        jObj.addProperty("oil_service_due", mService)
        jObj.addProperty("remarks", binding.etOther.text.toString())

        Log.e("CheckList", jObj.toString())
        ApiClient.getClient(this)
            .postCheckList(
                "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    dismissDialog()

                    if (response.isSuccessful) {
                        if (response.body()!!["status"].asBoolean) {
                            mPreferenceManager!!.setString(AppConstants.IS_STARTDAY, "1")
                            mPreferenceManager!!.setString(
                                AppConstants.DAY_START,
                                "1"
                            )
                            if (mPreferenceManager!!.getFTokenId()!! == "2" || mPreferenceManager!!.getFTokenId()!! == "3" || mPreferenceManager!!.getFTokenId()!! == "4") {
                                startActivity(Intent(me, ManageInventoryActivity::class.java))
                                finish()
                            }

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error while begin trip",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                    Toast.makeText(applicationContext, "Error while begin trip", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    override fun onBackPressed() {
        Toast.makeText(
            this@DriverCheckListActivity,
            "Please complete the checklist Form",
            Toast.LENGTH_SHORT
        ).show()
    }
}