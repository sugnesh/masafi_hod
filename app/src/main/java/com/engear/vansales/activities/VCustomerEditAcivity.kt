package com.engear.vansales.activities

import CusEditMonitoring
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.engear.vansales.R
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityVcustomerEditAcivityBinding
import com.engear.vansales.model.ChannelList
import com.engear.vansales.model.ChannelListResponse
import com.engear.vansales.model.PaymentTermList
import com.engear.vansales.model.PaymentTermListResponse
import com.engear.vansales.model.RegionList
import com.engear.vansales.model.RegionResponse
import com.engear.vansales.model.SubSecTradeChannelResponse
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VCustomerEditAcivity : BaseActivity(), LocationListener {
    lateinit var binding: ActivityVcustomerEditAcivityBinding
    private var paymentTermsList: List<PaymentTermList> = listOf()
    private var paymentTermListName: String? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var locationManager: LocationManager
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var selectedRegionId: Int? = null
    private var allChannels: List<ChannelList> = listOf()
    private var tradeChannelId: Int = 0
    private var tradeSubChannelId: Int = 0
    private var subSecTradeChannelId: Int = 0
    private var regions: List<RegionList> = emptyList()
    private var base64StringOwner: String? = null
    private var base64StringSponsor: String? = null
    private var numberOfDays: Int? = null
    private var paymentOpt: Int = 0
    private var base64StringEmirates: String? = null
    private var base64StringTradeLicense: String? = null
    private var base64StringOtherFiles: String? = null

    companion object {
        private const val REQUEST_CODE_OWNER_FILE = 1
        private const val REQUEST_CODE_SPONSOR_FILE = 2
        private const val REQUEST_CODE_EMIRATES_ID = 3
        private const val REQUEST_CODE_TRADE_LICENSE = 4
        private const val REQUEST_CODE_OTHER_FILES = 5
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVcustomerEditAcivityBinding.inflate(
            LayoutInflater.from(this), baseBinding.frameContainer, true
        )
        Utils.logData(
            this@VCustomerEditAcivity, "On customer Edit Screen "
        )
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0L, 0f, this as LocationListener
            )
        } catch (e: SecurityException) {

            e.printStackTrace()
        }
        init()
    }

    private fun init() {
        setToolbarTitle("Edit Customer")
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        binding.rlEditCus.visibility = View.GONE
        getPaymentTermBeforeList()
        binding.etshopStartTime.setOnClickListener {

            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                binding.etshopStartTime!!.setText(timeFormat.format(calendar.time))
            }


            val timePickerDialog = TimePickerDialog(
                this@VCustomerEditAcivity,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePickerDialog.show()
        }



        binding.etShopEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                // Format the time as you wish
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                binding.etShopEndTime!!.setText(timeFormat.format(calendar.time))
            }

            val timePickerDialog = TimePickerDialog(
                this@VCustomerEditAcivity,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePickerDialog.show()
        }
        binding.etOwnerExpiryDate!!.setOnClickListener {
            showDatePickerDialog(binding.etOwnerExpiryDate!!)
        }

        binding.etSponsorExpiryDate!!.setOnClickListener {
            showDatePickerDialog(binding.etSponsorExpiryDate!!)
        }

        binding.etTradeLicenceExpiry!!.setOnClickListener {
            showDatePickerTDialog(binding.etTradeLicenceExpiry!!)
        }

        binding.etbankGuarrteeValueExpiryDate!!.setOnClickListener {
            showDatePickerDialog(binding.etbankGuarrteeValueExpiryDate!!)
        }

        binding.buttonOwnerChooseFile!!.setOnClickListener {
            openFileChooser(REQUEST_CODE_OWNER_FILE)
        }

        binding.buttonSponsorChooseFiles!!.setOnClickListener {
            openFileChooser(REQUEST_CODE_SPONSOR_FILE)
        }

        binding.buttonEmiratesId!!.setOnClickListener {
            openFileChooser(REQUEST_CODE_EMIRATES_ID)
        }

        binding.buttonTradeLicence!!.setOnClickListener {
            openFileChooser(REQUEST_CODE_TRADE_LICENSE)
        }

        binding.buttonOtherFiles!!.setOnClickListener {
            openFileChooser(REQUEST_CODE_OTHER_FILES)
        }
        binding.btnSave.setOnClickListener {
            if (areRequiredFieldsEmpty()) {
                // Do not proceed with saving, as a toast message has already been shown for the empty field.
            } else {
                // Show a confirmation dialog
                AlertDialog.Builder(this@VCustomerEditAcivity)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton("OK") { dialog, which ->
                        // User clicked OK, proceed with sending the data
                        val payload = collectFormData()
                        updateCustomerData(payload)
                    }
                    .setNegativeButton("Cancel", null) // Do nothing on cancel
                    .show()
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude

        binding.etCustLatitude!!.text = Editable.Factory.getInstance().newEditable(latitude.toString())
        binding.etCusLongitude!!.text = Editable.Factory.getInstance().newEditable(longitude.toString())

        Log.d("EditCustomerActivity", "Latitude: $latitude, Longitude: $longitude")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }
    private fun areRequiredFieldsEmpty(): Boolean {
        when {
            binding.editTextSiteCode!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Site Code.", Toast.LENGTH_LONG).show()
            }
            binding.editTextCustomerCode!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Customer Code.", Toast.LENGTH_LONG).show()
            }
            binding.editTextSiteName!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Site Name.", Toast.LENGTH_LONG).show()
            }
            binding.editTextCustomerType!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Customer Type.", Toast.LENGTH_LONG).show()
            }
            binding.etAddressBillto!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter an Office Address (Bill to).", Toast.LENGTH_LONG).show()
            }
            binding.etAddressShipto!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter an Home Address:(Ship to).", Toast.LENGTH_LONG).show()
            }
            binding.etStreet!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Street.", Toast.LENGTH_LONG).show()
            }
            binding.etCity!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a City.", Toast.LENGTH_LONG).show()
            }
            binding.etlocation!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Location.", Toast.LENGTH_LONG).show()
            }
            binding.etRegion!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Region.", Toast.LENGTH_LONG).show()
            }
            binding.etPhoneNumber!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Phone Number.", Toast.LENGTH_LONG).show()
            }

            binding.ettradeLicence!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter an Trade Licence Number.", Toast.LENGTH_LONG).show()
            }
            binding.etTradeChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Trade Channel.", Toast.LENGTH_LONG).show()
            }
            binding.etTradeSubChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Trade Sub Channel.", Toast.LENGTH_LONG).show()
            }
            binding.etsubSecTradeChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Sub Sec Trade Channel.", Toast.LENGTH_LONG).show()
            }
            binding.etCustLatitude!!.text.isNullOrEmpty() || binding.etCusLongitude?.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Latitude & Longitude are empty! Please allow access to your location.", Toast.LENGTH_LONG).show()
                requestLocationUpdates()
            }
            else -> {
                return false
            }
        }
        return true

    }
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            try {

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0L, 0f, this as LocationListener
                )
            } catch (e: SecurityException) {

                e.printStackTrace()
            }
        }
    }
    private fun openFileChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type =
            "*/*" // To allow any file type, or "application/pdf|image/*" for PDF and images
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a file"), requestCode
            )
        } catch (ex: ActivityNotFoundException) {
            // Handle the error
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val fileName = uri?.let { getFileName(it) }

            when (requestCode) {
                REQUEST_CODE_OWNER_FILE -> {
                    binding.fileChosenOwner!!.text = fileName
                    uri?.let {
                        base64StringOwner = uri?.let { convertToBase64(it) }
                        Log.d("Owner File Base64", "Owner File Base64: $base64StringOwner")
                    }
                }

                REQUEST_CODE_SPONSOR_FILE -> {
                    binding.fileChosenSponsor!!.text = fileName
                    uri?.let {
                        base64StringSponsor = convertToBase64(it)
                        Log.d("Sponsor File Base64:", "Sponsor File Base64: $base64StringSponsor")
                    }
                }

                REQUEST_CODE_EMIRATES_ID -> {
                    binding.fileChosenEmiratesId!!.text = fileName
                    uri?.let {
                        base64StringEmirates = convertToBase64(it)
                        Log.d("Emirates ID Base64", "Emirates ID Base64: $base64StringEmirates")
                    }
                }

                REQUEST_CODE_TRADE_LICENSE -> {
                    binding.textViewTradeLicence!!.text = fileName
                    uri?.let {
                        base64StringTradeLicense = convertToBase64(it)
                        Log.d(
                            "Trade License Base64",
                            "Trade License Base64: $base64StringTradeLicense"
                        )
                    }
                }

                REQUEST_CODE_OTHER_FILES -> {
                    binding.textViewOtherFiles!!.text = fileName
                    uri?.let {
                        base64StringOtherFiles = convertToBase64(it)
                        Log.d("Other Files Base64", "Other Files Base64: $base64StringOtherFiles")
                    }
                }
            }
        }
    }

    private fun convertToBase64(uri: Uri): String {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("convertToBase64", "Error converting to Base64", e)
            ""
        }
    }


    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun showDatePickerTDialog(textView: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                textView.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun showDatePickerDialog(textView: AutoCompleteTextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                textView.setText(selectedDate)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun initCustomerTypeDropdown() {
        val customerTypes = listOf("Credit", "Cash", "Bill to Bill")
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, customerTypes)
        binding.editTextCustomerType.setAdapter(adapter)
        binding.editTextCustomerType.setOnItemClickListener { _, _, position, _ ->

            val selectedType = customerTypes[position]
            when (selectedType) {
                "Credit" -> {
                    binding.creditLimit.visibility = View.VISIBLE
                    binding.paymentOptions.visibility = View.VISIBLE
                    binding.creditLimitHeader.text = "Credit"
                    binding.numberOfInvoice.visibility = View.GONE
                    binding.creditLimitHeader.visibility = View.VISIBLE
                }

                "Bill to Bill" -> {
                    binding.creditLimit.visibility = View.VISIBLE
                    binding.paymentOptions.visibility = View.VISIBLE
                    binding.creditLimitHeader.visibility = View.VISIBLE
                    binding.creditLimitHeader.text = "Bill to Bill"
                    binding.numberOfInvoice.visibility = View.VISIBLE
                }

                else -> {
                    binding.creditLimit.visibility = View.GONE
                    binding.paymentOptions.visibility = View.GONE
                    binding.creditLimitHeader.visibility = View.GONE
                    binding.numberOfInvoice.visibility = View.GONE

                }
            }
        }

    }
    private fun updateCustomerData(payload: JSONObject) {
        val customer_code = intent.getStringExtra("customer_code") ?: return
        progressDialog.show()
        val requestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())

        val call = ApiClient.getClient(this).saveCustomerEDetails(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            customer_code,
            requestBody
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle success
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@VCustomerEditAcivity,
                        "Customer Updated successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val jsonError = JSONObject(errorBody)
                        val message = jsonError.getString("message")
                        val errors = jsonError.getString("errors")
                        "$message: $errors"
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        "Failed to save customer data"
                    }

                    Log.e("API Error", "API call failed with message: $errorMessage")
                    Toast.makeText(this@VCustomerEditAcivity, errorMessage, Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                // Handle network error
                Log.e("Network Error", "Failed to make API call: ${error.message}")
                progressDialog.dismiss()
                Toast.makeText(
                    this@VCustomerEditAcivity,
                    "Network error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
    private fun collectFormData(): JSONObject {

        val userType = "10"
        val parentId = "3"
        val siteCode = binding.editTextSiteCode!!.text.toString()
        val cusCode = binding.editTextCustomerCode!!.text.toString()
        val siteName = binding.editTextSiteName!!.text.toString()
        val customerType = binding.editTextCustomerType!!.text.toString()
        val customerAddress1 = binding.etAddressBillto!!.text.toString()
        val customerAddress2 = binding.etAddressShipto!!.text.toString()
        val trnName = binding.ettrnName!!.text.toString()
        val trnNo = binding.ettrnNo!!.text.toString()
        val zipCodePO = binding.etzipCode!!.text.toString()
        val street = binding.etStreet!!.text.toString()
        val city = binding.etCity!!.text.toString()
        val location = binding.etlocation!!.text.toString()
        //  val country = binding.etCountry!!.text.toString()
        val regionid = selectedRegionId ?: JSONObject.NULL
        val phoneNumber = binding.etPhoneNumber!!.text.toString()
        val email = binding.etemail!!.text.toString()
        val ownerPassportExpiry = binding.etOwnerExpiryDate!!.text.toString()
        val sponserPassportExpiry = binding.etSponsorExpiryDate!!.text.toString()
        val tradeLicense = binding.ettradeLicence!!.text.toString()
        val tradeLicenseExpiry = binding.etTradeLicenceExpiry!!.text.toString()
        val accNumberBank = binding.etaccNumberBank!!.text.toString()
        val creditLimit = binding.etcreditLimit!!.text.toString()
        val NoOfInvoice = binding.etnumberOfInvoice!!.text.toString()
        val estimateMonthlyPurchase = binding.etestimateMonthlyPurchase!!.text.toString()
        val estimateQtyAmt = binding.etTradeQtyAmt!!.text.toString()
        val bankGarrnteeValue = binding.etBankGuarrteeValue!!.text.toString()
        val estimateExpiryDate = binding.etbankGuarrteeValueExpiryDate!!.text.toString()
        val nameOfGoodRecevier = binding.etNameOfGoodReceiver!!.text.toString()
        val serviceStartTime = binding.etshopStartTime!!.text.toString()
        val serviceEndTime = binding.etShopEndTime!!.text.toString()


        val requestBodyJson = JSONObject()
        requestBodyJson.put("is_lob", 0)
        requestBodyJson.put("status", "1")
        requestBodyJson.put("usertype", userType)
        requestBodyJson.put("parent_id", parentId)
        requestBodyJson.put("balance", 0)


        requestBodyJson.put("bill_to_payer", cusCode)
        requestBodyJson.put("payer", siteCode)
        requestBodyJson.put("ship_to_party", siteCode)
        requestBodyJson.put("sold_to_party", siteCode)
        requestBodyJson.put("customer_code", cusCode)

        requestBodyJson.put("firstname", siteName)
        requestBodyJson.put("firstnameFormControl", siteName)
        requestBodyJson.put("lastname", "")

        when (customerType) {
            "Credit" -> {
                requestBodyJson.put("customertype", 1)
                requestBodyJson.put("customer_type_id", 1)
            }

            "Cash" -> {
                requestBodyJson.put("customertype", 2)
                requestBodyJson.put("customer_type_id", 2)
            }

            "Bill to Bill" -> {
                requestBodyJson.put("customertype", 3)
                requestBodyJson.put("customer_type_id", 3)
            }

            else -> {
                Log.e("collectFormData", "Unexpected customertype value: $customerType")
            }
        }

        requestBodyJson.put("customer_address_1", customerAddress1)
        requestBodyJson.put("customer_address_1_lat", latitude)
        requestBodyJson.put("customer_address_1_lang", longitude)
        requestBodyJson.put("customer_address_2", customerAddress2)
        requestBodyJson.put("customer_address_2_lat", 0)
        requestBodyJson.put("customer_address_2_lang", 0)
        requestBodyJson.put("trn_name", trnName)
        requestBodyJson.put("trn_no", trnNo)
        //.....................AddressInfo..................
        requestBodyJson.put("customerZipCode", zipCodePO)
        requestBodyJson.put("customer_zipcode", zipCodePO)
        requestBodyJson.put("Street", street)
        requestBodyJson.put("customerCity", city)
        requestBodyJson.put("customer_city", city)
        requestBodyJson.put("location", location)
        requestBodyJson.put("country_id", 9)
        requestBodyJson.put("region_id", regionid)
        requestBodyJson.put("phoneNumber", "")
        requestBodyJson.put("customerPhone", phoneNumber)
        requestBodyJson.put("customer_phone", phoneNumber)

        if (!binding.etemail.text.isNullOrEmpty()) {
            requestBodyJson.put("email", email)
            requestBodyJson.put("emailFormControl", email)
        }
        //.....................OwnerInfo..................
        if (!base64StringOwner.isNullOrEmpty()) {
            requestBodyJson.put("owner_visa_passport", base64StringOwner)

        }
        requestBodyJson.put("owner_passport_copy_expiry_date", ownerPassportExpiry)
        if (!base64StringSponsor.isNullOrEmpty()) {
            requestBodyJson.put("sponsor_visa_passport", base64StringSponsor)

        }
        requestBodyJson.put("sponser_passport_copy_expiry_date", sponserPassportExpiry)
        if (!base64StringTradeLicense.isNullOrEmpty()) {
            requestBodyJson.put("trade_license_copy", base64StringTradeLicense)

        }
        requestBodyJson.put("trade_lince_expiry_date", tradeLicenseExpiry)
        if (!base64StringOtherFiles.isNullOrEmpty()) {
            requestBodyJson.put("other_image", base64StringOtherFiles)
        }

        requestBodyJson.put("trade_license", tradeLicense)
        requestBodyJson.put("account_number_bank", accNumberBank)

        //.....................customerType..................
        if (numberOfDays == null) {
            numberOfDays = paymentOpt
        }
        requestBodyJson.put("credit_days", numberOfDays)
        requestBodyJson.put("credit_limit", creditLimit)
        requestBodyJson.put("no_of_invoice", NoOfInvoice)
        //.....................Credit Requested..................

        requestBodyJson.put("estimate_monthly_purchase", estimateMonthlyPurchase)
        requestBodyJson.put("estimate_qty_amt", estimateQtyAmt)
        requestBodyJson.put("bank_guarrtee_value", bankGarrnteeValue)
        requestBodyJson.put("estimate_expiry_date", estimateExpiryDate)
        requestBodyJson.put("name_of_good_recevier", nameOfGoodRecevier)
        requestBodyJson.put("channel_id", tradeChannelId)
        requestBodyJson.put("customer_category_id", tradeSubChannelId)
        requestBodyJson.put("sub_secondary_channel_id", subSecTradeChannelId)

        //.....................route Optimization..................

        requestBodyJson.put("service_start_time", serviceStartTime)
        requestBodyJson.put("service_end_time", serviceEndTime)


        val deliveryDaysArray = JSONArray()
        if (binding.checkBoxSunday!!.isChecked) deliveryDaysArray.put(0)
        if (binding.checkBoxMonday!!.isChecked) deliveryDaysArray.put(1)
        if (binding.checkBoxTuesday!!.isChecked) deliveryDaysArray.put(2)
        if (binding.checkBoxWednesday!!.isChecked) deliveryDaysArray.put(3)
        if (binding.checkBoxThursday!!.isChecked) deliveryDaysArray.put(4)
        if (binding.checkBoxFriday!!.isChecked) deliveryDaysArray.put(5)
        if (binding.checkBoxSaturday!!.isChecked) deliveryDaysArray.put(6)

        requestBodyJson.put("customer_delivery_day", deliveryDaysArray)


        return requestBodyJson
    }

    private fun getUaeRegions() {

        val requestBody = JsonObject().apply {
            add("list_data", JsonArray().apply { add("region") })
            addProperty("function_for", "customer")
        }
        ApiClient.getClient(this).getRegionListResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            requestBody
        ).enqueue(object : Callback<RegionResponse> {
            override fun onResponse(
                call: Call<RegionResponse>, response: Response<RegionResponse>
            ) {
                Log.d("getRegionListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {

                    regions = response.body()?.data?.region ?: emptyList()
                    val regionNames = regions.map { it.region_name }
                    val adapter = ArrayAdapter(
                        this@VCustomerEditAcivity,
                        android.R.layout.simple_dropdown_item_1line,
                        regionNames
                    )
                    binding.etRegion!!.setAdapter(adapter)
                    binding.etRegion!!.setOnItemClickListener { parent, view, position, id ->
                        selectedRegionId = regions[position].id
                    }
                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegionResponse>, error: Throwable) {

                Log.e("getRegionListResponse", "onFailure", error)
            }
        })
    }
    private fun getPaymentTermBeforeList() {

        ApiClient.getClient(this).getPaymentTermListResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
        ).enqueue(object : Callback<PaymentTermListResponse> {
            override fun onResponse(
                call: Call<PaymentTermListResponse>, response: Response<PaymentTermListResponse>
            ) {
                Log.d("PaymentTermListResponse", "onResponse")
                if (response.isSuccessful) {
                    paymentTermsList = response.body()?.data ?: listOf()
                    getCusEditDetails()
                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    getCusEditDetails()
                }
            }

            override fun onFailure(call: Call<PaymentTermListResponse>, error: Throwable) {

                Log.e("PaymentTermListResponse", "onFailure", error)
                getCusEditDetails()
            }
        })
    }


    private fun getPaymentTermList() {

        ApiClient.getClient(this).getPaymentTermListResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
        ).enqueue(object : Callback<PaymentTermListResponse> {
            override fun onResponse(
                call: Call<PaymentTermListResponse>, response: Response<PaymentTermListResponse>
            ) {
                Log.d("PaymentTermListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {

                    val paymentTermNames = response.body()?.data?.map { it.name } ?: listOf()
                    val adapter = ArrayAdapter(
                        this@VCustomerEditAcivity,
                        android.R.layout.simple_dropdown_item_1line,
                        paymentTermNames

                    )

                    binding.etpaymentOptions!!.setAdapter(adapter)
                    binding.etpaymentOptions!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = response.body()?.data?.get(position)
                        numberOfDays = selectedPaymentTerm?.numberOfDays ?: 0

                    }


                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")

                }
            }

            override fun onFailure(call: Call<PaymentTermListResponse>, error: Throwable) {

                Log.e("PaymentTermListResponse", "onFailure", error)

            }
        })
    }

    private fun flattenChannels(channels: List<ChannelList>, level: Int = 0): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            val prefix = if (channel.nodeLevel == 1) "     • " else "• "
            flattened.add("$prefix${channel.name}")
            flattened.addAll(flattenChannels(channel.children, level + 1))
        }
        return flattened
    }

    private fun getCusChannelResponse() {

        ApiClient.getClient(this).getNewCusChannelResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
        ).enqueue(object : Callback<ChannelListResponse> {
            override fun onResponse(
                call: Call<ChannelListResponse>, response: Response<ChannelListResponse>
            ) {
                Log.d("ChannelListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {

                    allChannels = response.body()?.data ?: listOf()
                    val flattenedChannelNames = flattenChannels(allChannels)
                    val adapter = ArrayAdapter(
                        this@VCustomerEditAcivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedChannelNames
                    )
                    binding.etTradeChannel?.setAdapter(adapter)
                    binding.etTradeChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = response.body()?.data?.get(position)
                        tradeChannelId = selectedPaymentTerm?.id ?: 0

                    }

                } else {
                    // Handle API error

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    // Handle errors as required
                }
            }

            override fun onFailure(call: Call<ChannelListResponse>, error: Throwable) {
                Log.e("ChannelListResponse", "onFailure", error)

                // Handle network error as required
            }
        })
    }

    private fun getCusTradeSubChannedl() {

        ApiClient.getClient(this).getCusTradeSubResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
        ).enqueue(object : Callback<ChannelListResponse> {
            override fun onResponse(
                call: Call<ChannelListResponse>, response: Response<ChannelListResponse>
            ) {
                Log.d("ChannelListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {

                    allChannels = response.body()?.data ?: listOf()
                    val flattenedChannelNames = flattenChannels(allChannels)
                    val adapter = ArrayAdapter(
                        this@VCustomerEditAcivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedChannelNames
                    )
                    binding.etTradeSubChannel?.setAdapter(adapter)
                    binding.etTradeSubChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = response.body()?.data?.get(position)
                        tradeSubChannelId = selectedPaymentTerm?.id ?: 0

                    }
                } else {
                    // Handle API error

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    // Handle errors as required
                }
            }

            override fun onFailure(call: Call<ChannelListResponse>, error: Throwable) {

                Log.e("ChannelListResponse", "onFailure", error)
                // Handle network error as required
            }
        })
    }

    private fun updateDeliveryDayCheckboxes(deliveryDays: List<String>) {
        // Reset all checkboxes
        binding.checkBoxAll.isChecked = false
        binding.checkBoxMonday.isChecked = false
        binding.checkBoxTuesday.isChecked = false
        binding.checkBoxWednesday.isChecked = false
        binding.checkBoxThursday.isChecked = false
        binding.checkBoxFriday.isChecked = false
        binding.checkBoxSaturday.isChecked = false
        binding.checkBoxSunday.isChecked = false

        for (day in deliveryDays) {
            when (day) {
                "0" -> binding.checkBoxSunday.isChecked = true
                "1" -> binding.checkBoxMonday.isChecked = true
                "2" -> binding.checkBoxTuesday.isChecked = true
                "3" -> binding.checkBoxWednesday.isChecked = true
                "4" -> binding.checkBoxThursday.isChecked = true
                "5" -> binding.checkBoxFriday.isChecked = true
                "6" -> binding.checkBoxSaturday.isChecked = true
            }
        }

        if (deliveryDays.size == 7) {
            binding.checkBoxAll.isChecked = true
        }
    }


    private fun getSubSecTradeChannel() {

        ApiClient.getClient(this).getCusTradeSubSecResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
        ).enqueue(object : Callback<SubSecTradeChannelResponse> {
            override fun onResponse(
                call: Call<SubSecTradeChannelResponse>,
                response: Response<SubSecTradeChannelResponse>
            ) {
                Log.d("ChannelListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {

                    val channelListResponse = response.body()
                    val channelNames =
                        channelListResponse?.data?.map { it.subSecondaryChannelName } ?: listOf()

                    val adapter = ArrayAdapter(
                        this@VCustomerEditAcivity,
                        android.R.layout.simple_dropdown_item_1line,
                        channelNames
                    )

                    binding.etsubSecTradeChannel?.setAdapter(adapter)
                    binding.etsubSecTradeChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = response.body()?.data?.get(position)
                        subSecTradeChannelId = selectedPaymentTerm?.id ?: 0
                    }

                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")

                }
            }

            override fun onFailure(call: Call<SubSecTradeChannelResponse>, error: Throwable) {
                Log.e("ChannelListResponse", "onFailure", error)


            }
        })
    }

    private fun setupCheckBoxListeners() {
        val dayCheckBoxes = listOf(
            binding.checkBoxSunday,
            binding.checkBoxMonday,
            binding.checkBoxTuesday,
            binding.checkBoxWednesday,
            binding.checkBoxThursday,
            binding.checkBoxFriday,
            binding.checkBoxSaturday
        )

        // Update all checkboxes when "All" is clicked
        binding.checkBoxAll!!.setOnClickListener {
            val isChecked = binding.checkBoxAll!!.isChecked
            dayCheckBoxes.forEach { it!!.isChecked = isChecked }
        }

        // Update "All" checkbox based on individual checkboxes
        dayCheckBoxes.forEach { checkBox ->
            checkBox!!.setOnClickListener {
                binding.checkBoxAll!!.isChecked = dayCheckBoxes.all { it!!.isChecked }
            }
        }
    }

    private fun getCusEditDetails() {
        val customer_code = intent.getStringExtra("customer_code") ?: return
        Log.d("cusEditDetails", "customer_code: $customer_code")
        ApiClient.getClient(this).getCustomerEDetails(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            customer_code
        ).enqueue(object : Callback<CusEditMonitoring> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<CusEditMonitoring>, response: Response<CusEditMonitoring>
            ) {
                if (response.isSuccessful) {
                    val cusEditDetails = response.body()
                    val siteCode = cusEditDetails?.data?.payer?.customerCode ?: ""
                    val customerCode = cusEditDetails?.data?.billToPayer?.customerCode ?: ""
                    val siteName = cusEditDetails?.data?.user?.firstname ?: ""
                    val cusType = cusEditDetails?.data?.customerType?.customerTypeName ?: ""
                    val addressBillto = cusEditDetails?.data?.customerAddress1 ?: ""
                    val addressShipto = cusEditDetails?.data?.customerAddress2 ?: ""
                    val trnN0 = cusEditDetails?.data?.trnNo ?: ""
                    val trnName = cusEditDetails?.data?.trnName ?: ""

                    val zipCodeCus = cusEditDetails?.data?.customerZipcode ?: ""
                    val streetCus = cusEditDetails?.data?.street ?: ""
                    val cityCus = cusEditDetails?.data?.customerCity ?: ""
                    val locationCus = cusEditDetails?.data?.location ?: ""
                    val regionCus = cusEditDetails?.data?.region!!.regionName ?: ""
                    val regionCusid = cusEditDetails?.data?.region!!.id ?: ""
                    val phoneNumCus = cusEditDetails?.data?.customerPhone ?: ""
                    val emailCus = cusEditDetails?.data?.user!!.email ?: ""

                    val ownerPassportExpiry = cusEditDetails?.data?.ownerPassportCopyExpiryDate?: ""
                    val sponserPassportExpiry = cusEditDetails?.data?.sponserPassportCopyExpiryDate ?: ""


                    val tradeLicence = cusEditDetails?.data?.tradeLicense ?: ""
                    val tradeLicenceExpiry = cusEditDetails?.data?.tradeLinceExpiryDate ?: ""
                    val accountNumBank = cusEditDetails?.data?.accountNumberBank ?: ""

                    val creditLimit = cusEditDetails?.data?.creditLimit ?: ""

                    val noOfInvoices = cusEditDetails?.data?.noOfInvoice ?: ""

                    val estimateMonthlyPurchase =
                        cusEditDetails?.data?.estimateMonthlyPurchase ?: ""
                    val tradeQtyAmt = cusEditDetails?.data?.estimateQtyAmt ?: ""
                    val bankGuarrteeValue = cusEditDetails?.data?.bankGuarrteeValue ?: ""
                    val bankGuarrteeValueExpiryDate =
                        cusEditDetails?.data?.estimateExpiryDate ?: ""
                    val nameOfGoodReceiver = cusEditDetails?.data?.nameOfGoodRecevier ?: ""
                    val custradeChannelid = cusEditDetails?.data?.channel!!.id ?: ""
                    val custradeSubChannelid =
                        cusEditDetails?.data?.customerCategory!!.id ?: ""
                    val cusSubSecTradeChannelid =
                        cusEditDetails?.data?.subSecondaryChannel!!.id
                            ?: ""
                    val tradeChannel = cusEditDetails?.data?.channel!!.name ?: ""
                    val tradeSubChannel =
                        cusEditDetails?.data?.customerCategory!!.customerCategoryName ?: ""
                    val subSecTradeChannel =
                        cusEditDetails?.data?.subSecondaryChannel!!.subSecondaryChanneleName
                            ?: ""
                    val shopStartTime = cusEditDetails?.data?.serviceStartTime ?: ""
                    val shopEndTime = cusEditDetails?.data?.serviceEndTime ?: ""

                    paymentOpt = cusEditDetails?.data?.creditDays ?: 0
                    val matchingPaymentTerm = paymentTermsList.find { it.numberOfDays == paymentOpt }
                    paymentTermListName = matchingPaymentTerm?.name

                    binding.editTextSiteCode.text =
                        Editable.Factory.getInstance().newEditable(siteCode)
                    binding.editTextCustomerCode.text =
                        Editable.Factory.getInstance().newEditable(customerCode)
                    binding.editTextSiteName.text =
                        Editable.Factory.getInstance().newEditable(siteName)
                    binding.editTextCustomerType.text =
                        Editable.Factory.getInstance().newEditable(cusType)
                    when (cusType) {
                        "Credit" -> {
                            binding.creditLimit.visibility = View.VISIBLE
                            binding.paymentOptions.visibility = View.VISIBLE
                            binding.creditLimitHeader.text = "Credit"
                            binding.numberOfInvoice.visibility = View.GONE
                            binding.creditLimitHeader.visibility = View.VISIBLE
                        }

                        "Bill to Bill" -> {
                            binding.creditLimit.visibility = View.VISIBLE
                            binding.paymentOptions.visibility = View.VISIBLE
                            binding.creditLimitHeader.visibility = View.VISIBLE
                            binding.creditLimitHeader.text = "Bill to Bill"
                            binding.numberOfInvoice.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.creditLimit.visibility = View.GONE
                            binding.paymentOptions.visibility = View.GONE
                            binding.creditLimitHeader.visibility = View.GONE
                            binding.numberOfInvoice.visibility = View.GONE
                        }
                    }
                    initCustomerTypeDropdown()
                    binding.etAddressBillto.text =
                        Editable.Factory.getInstance().newEditable(addressBillto)
                    binding.etAddressShipto.text =
                        Editable.Factory.getInstance().newEditable(addressShipto)
                    binding.ettrnNo.text = Editable.Factory.getInstance().newEditable(trnN0)
                    binding.ettrnName.text = Editable.Factory.getInstance().newEditable(trnName)

                    //Address Info
                    binding.etzipCode.text =
                        Editable.Factory.getInstance().newEditable(zipCodeCus)
                    binding.etStreet.text =
                        Editable.Factory.getInstance().newEditable(streetCus)
                    binding.etCity.text = Editable.Factory.getInstance().newEditable(cityCus)
                    binding.etlocation.text =
                        Editable.Factory.getInstance().newEditable(locationCus)
                    binding.etRegion.text =
                        Editable.Factory.getInstance().newEditable(regionCus)
                    binding.etPhoneNumber.text =
                        Editable.Factory.getInstance().newEditable(phoneNumCus)
                    binding.etemail.text = Editable.Factory.getInstance().newEditable(emailCus)
                    getUaeRegions()

                    //Owner Info
                    binding.etOwnerExpiryDate.text =
                        Editable.Factory.getInstance().newEditable(ownerPassportExpiry)
                    binding.etSponsorExpiryDate.text =
                        Editable.Factory.getInstance().newEditable(sponserPassportExpiry)
                    binding.ettradeLicence.text =
                        Editable.Factory.getInstance().newEditable(tradeLicence)
                    binding.etTradeLicenceExpiry.text =
                        Editable.Factory.getInstance().newEditable(tradeLicenceExpiry)
                    binding.etaccNumberBank.text =
                        Editable.Factory.getInstance().newEditable(accountNumBank)

                    //.....................customerType..................
                    binding.etcreditLimit.text =
                        Editable.Factory.getInstance().newEditable(creditLimit)
                    binding.etpaymentOptions.text = Editable.Factory.getInstance().newEditable(paymentTermListName ?: "")
                    Log.d("PaymentTermName", "Payment Term Name: $paymentTermListName")

                    binding.etnumberOfInvoice.text = Editable.Factory.getInstance().newEditable(
                        noOfInvoices.toString()
                    )
                    getPaymentTermList()

                    //Credit Requested
                    binding.etestimateMonthlyPurchase.text =
                        Editable.Factory.getInstance().newEditable(estimateMonthlyPurchase)
                    binding.etTradeQtyAmt.text =
                        Editable.Factory.getInstance().newEditable(tradeQtyAmt)
                    binding.etBankGuarrteeValue.text =
                        Editable.Factory.getInstance().newEditable(bankGuarrteeValue)
                    binding.etbankGuarrteeValueExpiryDate.text =
                        Editable.Factory.getInstance().newEditable(bankGuarrteeValueExpiryDate)
                    binding.etNameOfGoodReceiver.text =
                        Editable.Factory.getInstance().newEditable(nameOfGoodReceiver)

                    selectedRegionId = regionCusid as Int
                    tradeChannelId = custradeChannelid as Int
                    tradeSubChannelId = custradeSubChannelid as Int
                    subSecTradeChannelId = cusSubSecTradeChannelid as Int

                    binding.etTradeChannel.text =
                        Editable.Factory.getInstance().newEditable(tradeChannel)
                    binding.etTradeSubChannel.text =
                        Editable.Factory.getInstance().newEditable(tradeSubChannel)
                    binding.etsubSecTradeChannel.text =
                        Editable.Factory.getInstance().newEditable(subSecTradeChannel)

                    getCusChannelResponse()
                    getCusTradeSubChannedl()
                    getSubSecTradeChannel()
                    binding.etshopStartTime.text =
                        Editable.Factory.getInstance().newEditable(shopStartTime)
                    binding.etShopEndTime.text =
                        Editable.Factory.getInstance().newEditable(shopEndTime)

                    val deliveryDays =
                        cusEditDetails?.data?.customerDeliveryDay?.map { it.day } ?: emptyList()
                    updateDeliveryDayCheckboxes(deliveryDays)
                    setupCheckBoxListeners()
                    binding.rlEditCus.visibility = View.VISIBLE
                    progressDialog.dismiss()
                    Utils.logData(
                        this@VCustomerEditAcivity,
                        "cusEditDetails: ${response.body().toString()}"
                    )
                } else {
                    Log.d(
                        "cusEditDetails",
                        "Response not successful: ${response.errorBody()?.string()}"

                    )
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<CusEditMonitoring>, error: Throwable) {


                dismissDialog()
                if (error.message != null) {
                    Utils.logData(
                        this@VCustomerEditAcivity,
                        "cusEditDetails Fail: ${error.message.toString()}"
                    )
                    progressDialog.dismiss()
                } else {
                    Utils.logData(
                        this@VCustomerEditAcivity, "cusEditDetails API Fail"
                    )
                    progressDialog.dismiss()
                }
            }
        })
    }


}