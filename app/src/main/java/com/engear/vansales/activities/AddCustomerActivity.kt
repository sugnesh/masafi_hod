package com.engear.vansales.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityAddCustomerBinding
import com.engear.vansales.localDB.CustomerSyncEntity
import com.engear.vansales.model.ChannelList
import com.engear.vansales.model.ChannelListResponse
import com.engear.vansales.model.ChannelSubList
import com.engear.vansales.model.CustomerResponseData
import com.engear.vansales.model.DiscountCardList
import com.engear.vansales.model.DiscountCardListResponse
import com.engear.vansales.model.LandmarkListResponse
import com.engear.vansales.model.PaymentTermList
import com.engear.vansales.model.PaymentTermListResponse
import com.engear.vansales.model.RegionList
import com.engear.vansales.model.RegionResponse
import com.engear.vansales.model.SubChannelList
import com.engear.vansales.model.SubChannelListResponse
import com.engear.vansales.model.SubSecTradeChannelResponse
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddCustomerActivity : BaseActivity() {
    lateinit var binding: ActivityAddCustomerBinding
    private var allChannels: List<ChannelList> = listOf()
    private var allSubChannels: List<ChannelSubList> = listOf()
    private var allDiscountCardels: List<DiscountCardList> = listOf()
    private lateinit var locationManager: LocationManager
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var regions: List<RegionList> = emptyList()
    private var selectedRegionId: Int? = null
    private var numberOfDays: Int? = null
    private var landmarkId: Int? = null
    private var routeId: String = ""
    private var regionId: String = ""
    var paymentTermList: List<PaymentTermList> = listOf()
    private var tradeChannelId: Int = 0
    private var tradeSubChannelId: Int = 0
    private var subSecTradeChannelId: Int = 0
    private var discountCardId: Int = 0
    private lateinit var progressDialog: ProgressDialog
    private var base64StringCard: String = ""
    private var image_uri: Uri? = null
    var mStampInvoice: String = ""
    var encodedImage: String? = ""
    private var imageUri: Uri? = null
    private val CAMERA = 200
    private val pickImage = 300

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_CODE_CARD_FILE = 1
    }

    private val locationPickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                result.data?.let { data ->

                    val address = data.getStringExtra("address") ?: ""
                    var latitude = data.getDoubleExtra("latitude", 0.0)
                    val longitude = data.getDoubleExtra("longitude", 0.0)

                    binding.etCustLatitude!!.setText(latitude.toString())
                    binding.etCusLongitude!!.setText(longitude.toString())
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAddCustomerBinding.inflate(
            LayoutInflater.from(this), baseBinding.frameContainer, true
        )
        Utils.logData(
            this@AddCustomerActivity, "On Add customer Screen "
        )
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        init()

    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        setToolbarTitle("Add Customer")

        binding.etRoute!!.setText(mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.routeCode)
        routeId =
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.route.id.toString()
        regionId =
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.regionId

        initCustomerTypeDropdown()
        getNextAddCustomer()
        getCusChannelResponse()
        tradeChannelId = 1
        getCusTradeSubChannedl()
        //getSubSecTradeChannel()
        setupCheckBoxListeners()
        getPaymentTermList()
        getUaeRegions()
        getLandmarkList()


        binding.etshopStartTime?.setOnClickListener {

            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                binding.etshopStartTime!!.setText(timeFormat.format(calendar.time))
            }


            val timePickerDialog = TimePickerDialog(
                this@AddCustomerActivity,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePickerDialog.show()
        }

        binding.btnMap!!.setOnClickListener {

            val intent = Intent(
                this,
                LocationPickerActivity::class.java
            )

            locationPickerLauncher.launch(intent)
        }


        binding.etShopEndTime?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                // Format the time as you wish
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                binding.etShopEndTime!!.setText(timeFormat.format(calendar.time))
            }

            val timePickerDialog = TimePickerDialog(
                this@AddCustomerActivity,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePickerDialog.show()
        }

        binding.etCardExpiryDate!!.setOnClickListener {
            showDatePickerDialog(binding.etCardExpiryDate!!)
        }

        binding.buttonCardId!!.setOnClickListener {
            // openFileChooser(REQUEST_CODE_CARD_FILE)
            //pickFromCamera()
            showPictureDialog()
        }

        binding.btnSave.setOnClickListener {
            if (areRequiredFieldsEmpty()) {

            } else {

                AlertDialog.Builder(this@AddCustomerActivity)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to proceed?")
                    .setPositiveButton("OK") { dialog, which ->
                        // User clicked OK, proceed with sending the data
                        val payload = collectFormData()
                        Log.e("Payload", payload.toString())
                        val customer = CustomerSyncEntity(
                            payload = payload.toString(),
                            customerCode = payload.getString("customer_code"),
                            customerName = payload.getString("firstname"),
                            isSynced = false
                        )
                        sendCustomerData(payload, customer)
                    }
                    .setNegativeButton("Cancel", null) // Do nothing on cancel
                    .show()
            }
        }

//        latitude = AppConstants.Latitude.toDouble()
//        longitude = AppConstants.Longitude.toDouble()

        binding.etCustLatitude!!.text =
            Editable.Factory.getInstance().newEditable(latitude.toString())
        binding.etCusLongitude!!.text =
            Editable.Factory.getInstance().newEditable(longitude.toString())

    }

    private fun showPictureDialog() {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Capture photo from camera", "Pick From Gallery")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> pickFromCamera()
                1 -> choosePhotoFromGallary()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
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
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun areRequiredFieldsEmpty(): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
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
                Toast.makeText(this, "Please enter an Home Address (Bill to).", Toast.LENGTH_LONG)
                    .show()
            }

            binding.etAddressShipto!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter an Home Address:(Ship to).", Toast.LENGTH_LONG)
                    .show()
            }


            binding.etRegion!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Region.", Toast.LENGTH_LONG).show()
            }

            binding.etPhoneNumber!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter a Phone Number.", Toast.LENGTH_LONG).show()
            }


            !binding.etPhoneNumber!!.text!!.matches(Regex("^\\d{10}$")) -> {
                Toast.makeText(this, "Phone Number must be exactly 10 digits.", Toast.LENGTH_LONG)
                    .show()
            }


            binding.etemail!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please enter an Email.", Toast.LENGTH_LONG).show()
            }

            !binding.etemail!!.text!!.matches(emailPattern.toRegex()) -> {
                Toast.makeText(this, "Please enter a valid Email.", Toast.LENGTH_LONG).show()
            }

            binding.etTradeChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Trade Channel.", Toast.LENGTH_LONG).show()
            }

            binding.etTradeSubChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Trade Sub Channel.", Toast.LENGTH_LONG).show()
            }

            binding.etsubSecTradeChannel!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a Sub Sec Trade Channel.", Toast.LENGTH_LONG)
                    .show()
            }


            binding.etLandmark!!.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select Landmark.", Toast.LENGTH_LONG)
                    .show()
            }

            binding.etCustLatitude!!.text.isNullOrEmpty() || binding.etCusLongitude?.text.isNullOrEmpty() -> {
                Toast.makeText(
                    this,
                    "Latitude & Longitude are empty! Please allow access to your location.",
                    Toast.LENGTH_LONG
                ).show()
                requestLocationUpdates()
            }

            else -> {

                if (binding.etTradeSubChannel!!.text.toString() == "CARD HOLDERS") {
                    if (binding.etdCard!!.text.toString().isEmpty()) {
                        Toast.makeText(this, "Please Enter Card Number", Toast.LENGTH_LONG)
                            .show()
                    } else if (binding.etCardExpiryDate!!.text.toString().isEmpty()) {
                        Toast.makeText(this, "Please Select Card Expiry Date", Toast.LENGTH_LONG)
                            .show()
                    } else if (base64StringCard!!.isEmpty()) {
                        Toast.makeText(this, "Please upload card Image", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        return false
                    }
                } else {
                    return false
                }
            }
        }
        return true

    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description")
        image_uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(
            intent,
            CAMERA
        )
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
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
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

    private fun collectFormData(): JSONObject {

        val userType = "10"
        val parentId = "3"
        val siteCode = binding.editTextSiteCode!!.text.toString()
        val cusCode = binding.editTextSiteCode!!.text.toString()
        val siteName = binding.editTextSiteName!!.text.toString()
        val customerType = binding.editTextCustomerType!!.text.toString()
        val customerAddress1 = binding.etAddressBillto!!.text.toString()
        val customerAddress2 = binding.etAddressShipto!!.text.toString()
        val trnName = ""
        val trnNo = ""
        val zipCodePO = binding.etzipCode!!.text.toString()
        val street = binding.etStreet!!.text.toString()
        val city = binding.etCity!!.text.toString()
        val location = binding.etlocation!!.text.toString()
        //  val country = binding.etCountry!!.text.toString()
        val regionid = selectedRegionId ?: JSONObject.NULL
        val phoneNumber = binding.etPhoneNumber!!.text.toString()
        val email = binding.etemail!!.text.toString()
        val serviceStartTime = binding.etshopStartTime!!.text.toString()
        val serviceEndTime = binding.etShopEndTime!!.text.toString()
        val cardExp = binding.etCardExpiryDate!!.text.toString()
        val cardNumber = binding.etdCard!!.text.toString()


        val requestBodyJson = JSONObject()
        requestBodyJson.put("is_lob", 0)
        requestBodyJson.put("status", "1")
        requestBodyJson.put("usertype", userType)
        requestBodyJson.put("parent_id", parentId)
        requestBodyJson.put("balance", 0)


        requestBodyJson.put("bill_to_payer", siteCode)
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
        requestBodyJson.put("routeId", routeId)
        requestBodyJson.put("landmark_id", landmarkId)
        requestBodyJson.put("phoneNumber", "")
        requestBodyJson.put("customerPhone", phoneNumber)
        requestBodyJson.put("customer_phone", phoneNumber)

        requestBodyJson.put("email", email)
        requestBodyJson.put("emailFormControl", email)
        //.....................customerType..................
        requestBodyJson.put("credit_days", numberOfDays)
        requestBodyJson.put("credit_limit", 0)
        requestBodyJson.put("no_of_invoice", 0)
        //.....................Credit Requested..................
        requestBodyJson.put("channel_id", tradeChannelId)
        requestBodyJson.put("customer_category_id", tradeSubChannelId)
        requestBodyJson.put("sub_secondary_channel_id", subSecTradeChannelId)

        //.....................route Optimization..................

        requestBodyJson.put("service_start_time", serviceStartTime)
        requestBodyJson.put("service_end_time", serviceEndTime)

        if (base64StringCard!!.isNotEmpty()) {
            var mBase = "data:image/jpeg;base64," + Utils.encoderBase64(base64StringCard!!)
            requestBodyJson.put("card_doc", mBase)
        }
        requestBodyJson.put("card_expiry", cardExp)
        requestBodyJson.put("card_number", cardNumber)

        val deliveryDaysArray = JSONArray()
        if (binding.checkBoxSunday!!.isChecked) deliveryDaysArray.put(0)
        if (binding.checkBoxMonday!!.isChecked) deliveryDaysArray.put(1)
        if (binding.checkBoxTuesday!!.isChecked) deliveryDaysArray.put(2)
        if (binding.checkBoxWednesday!!.isChecked) deliveryDaysArray.put(3)
        if (binding.checkBoxThursday!!.isChecked) deliveryDaysArray.put(4)
        if (binding.checkBoxFriday!!.isChecked) deliveryDaysArray.put(5)
        if (binding.checkBoxSaturday!!.isChecked) deliveryDaysArray.put(6)

        requestBodyJson.put("customer_delivery_day", deliveryDaysArray)
        val lobiszearray = JSONArray()
        requestBodyJson.put("customerLob", lobiszearray)

        val lobAyyar = JSONArray()
        val requestBodyLOBJson = JSONObject()
        requestBodyLOBJson.put("is_lob", 0)
        requestBodyLOBJson.put("parent_id", parentId)
        requestBodyLOBJson.put("balance", 0)


        requestBodyLOBJson.put("bill_to_payer", cusCode)
        requestBodyLOBJson.put("payer", siteCode)
        requestBodyLOBJson.put("ship_to_party", siteCode)
        requestBodyLOBJson.put("sold_to_party", siteCode)
        requestBodyLOBJson.put("customer_code", cusCode)

        requestBodyLOBJson.put("firstname", siteName)
        requestBodyLOBJson.put("firstnameFormControl", siteName)
        requestBodyLOBJson.put("lastname", "")
        //.....................Credit Requested..................
        requestBodyLOBJson.put("channel_id", tradeChannelId)
        requestBodyLOBJson.put("customer_category_id", tradeSubChannelId)
        requestBodyLOBJson.put("sub_secondary_channel_id", subSecTradeChannelId)

        //.....................route Optimization..................

        requestBodyLOBJson.put("service_start_time", serviceStartTime)
        requestBodyLOBJson.put("service_end_time", serviceEndTime)
        requestBodyLOBJson.put("customer_delivery_day", deliveryDaysArray)
        requestBodyLOBJson.put("customertype", 2)
        requestBodyLOBJson.put("customer_type_id", 2)

        requestBodyLOBJson.put("customer_address_1", customerAddress1)
        requestBodyLOBJson.put("customer_address_1_lat", latitude)
        requestBodyLOBJson.put("customer_address_1_lang", longitude)
        requestBodyLOBJson.put("customer_address_2", customerAddress2)
        requestBodyLOBJson.put("customer_address_2_lat", 0)
        requestBodyLOBJson.put("customer_address_2_lang", 0)
        requestBodyLOBJson.put("trn_name", trnName)
        requestBodyLOBJson.put("trn_no", trnNo)
        //.....................AddressInfo..................
        requestBodyLOBJson.put("customerZipCode", zipCodePO)
        requestBodyLOBJson.put("customer_zipcode", zipCodePO)
        requestBodyLOBJson.put("Street", street)
        requestBodyLOBJson.put("customerCity", city)
        requestBodyLOBJson.put("customer_city", city)
        requestBodyLOBJson.put("location", location)
        requestBodyLOBJson.put("countryId", 9)
        requestBodyLOBJson.put("regionId", regionid)
        requestBodyLOBJson.put("routeId", routeId)
        requestBodyLOBJson.put("landmarkId", landmarkId)
        requestBodyLOBJson.put("phoneNumber", "")
        requestBodyLOBJson.put("customerPhone", phoneNumber)
        requestBodyLOBJson.put("customer_phone", phoneNumber)

        requestBodyLOBJson.put("email", email)
        requestBodyLOBJson.put("emailFormControl", email)
        //.....................customerType..................
        requestBodyLOBJson.put("credit_days", numberOfDays)
        requestBodyLOBJson.put("credit_limit", 0)
        requestBodyLOBJson.put("no_of_invoice", 0)

        lobAyyar.put(requestBodyLOBJson)
        requestBodyJson.put("lobCustomerInfo", lobAyyar)
//        val routeId = userInfo!!.data.userInfo.salesmanInfo.routeId
//        val routeIdArray = JSONArray()
//        routeIdArray.put(routeId)
//        requestBodyJson.put("routeId", routeIdArray)


        return requestBodyJson
    }

    private fun sendCustomerData(payload: JSONObject, customer: CustomerSyncEntity) {
        progressDialog.show()
        Log.e("Customer Payload", payload.toString())
        val requestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())

        val call = ApiClient.getClient(this).saveNewCustomerFormData(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            requestBody
        )

        call.enqueue(object : Callback<CustomerResponseData> {
            override fun onResponse(
                call: Call<CustomerResponseData>,
                response: Response<CustomerResponseData>
            ) {
                if (response.isSuccessful) {
                    // Handle success
                    progressDialog.dismiss()
                    var mDataRes = response.body()!!.data!!
                    Toast.makeText(
                        this@AddCustomerActivity,
                        "Site " + mDataRes.customer_code.toString() + " created successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    insertCustomerToDatabase(
                        payload,
                        mDataRes.user_id.toString(),
                        mDataRes.customer_code.toString()
                    )
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
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with message: $errorMessage")
                    Toast.makeText(this@AddCustomerActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CustomerResponseData>, error: Throwable) {
                // Handle network error
                Log.e("Network Error", "Failed to make API call: ${error.message}")
                progressDialog.dismiss()
                Toast.makeText(
                    this@AddCustomerActivity,
                    "Network error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun insertCustomerToDatabase(payload: JSONObject, user_id: String, mCode: String) {

        val siteName = binding.editTextSiteName!!.text.toString()
        val customerType = binding.editTextCustomerType!!.text.toString()
        val customerAddress1 = binding.etAddressBillto!!.text.toString()
        val customerAddress2 = binding.etAddressShipto!!.text.toString()
        val zipCodePO = binding.etzipCode!!.text.toString()
        val street = binding.etStreet!!.text.toString()
        val city = binding.etCity!!.text.toString()
        val location = binding.etlocation!!.text.toString()
        //  val country = binding.etCountry!!.text.toString()
        val phoneNumber = binding.etPhoneNumber!!.text.toString()
        val email = binding.etemail!!.text.toString()

        var mCustomer: HashMap<String, String> = HashMap()
        mCustomer["customer_id"] = user_id
        mCustomer["customer_code"] = mCode
        mCustomer["firstanme"] = siteName
        mCustomer["email"] = email
        mCustomer["phone"] = phoneNumber
        mCustomer["address1"] = customerAddress1
        mCustomer["address2"] = customerAddress2
        mCustomer["customer_group"] = "0"
        mCustomer["category_id"] = tradeSubChannelId.toString()
        mCustomer["sub_channel_id"] = subSecTradeChannelId.toString()
        mCustomer["channel_id"] = tradeChannelId.toString()
        mCustomer["channel_name"] = binding.etTradeChannel!!.text.toString()
        mCustomer["city"] = city
        mCustomer["state"] = street
        mCustomer["zipcode"] = zipCodePO
        mCustomer["region_id"] = selectedRegionId.toString()
        mCustomer["route_id"] = routeId
        mCustomer["is_lob"] = "0"
        mCustomer["region_name"] = binding.etRegion!!.text.toString()
        mCustomer["latitude"] = latitude.toString()
        mCustomer["longitude"] = longitude.toString()
        mCustomer["parent_id"] = user_id
        mCustomer["customer_type"] = "2"

        db!!.insertNewCustomer(mCustomer)
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

    @SuppressLint("SetTextI18n")
    private fun initCustomerTypeDropdown() {
        val customerTypes = listOf("Cash")
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, customerTypes)
        binding.editTextCustomerType?.setAdapter(adapter)
        binding.editTextCustomerType?.setText(customerTypes[0], false)
        binding.editTextCustomerType?.setOnItemClickListener { _, _, position, _ ->
            val selectedType = customerTypes[position]
        }

    }


//    private fun getNextAddCustomer() {
//        ApiClient.getClient(this).getNextAddCustomerNo(
//            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
//            MyApplication.X_DOMAIN,
//            "customer"
//        ).enqueue(object : Callback<NextAddCustomerMonitoring> {
//            override fun onResponse(
//                call: Call<NextAddCustomerMonitoring>, response: Response<NextAddCustomerMonitoring>
//            ) {
//                Log.d("AddCustomerMonitoring", "onResponse")
//                dismissDialog()
//                if (response.isSuccessful) {
//                    val numberIs = response.body()?.data?.numberIs ?: ""
//                    binding.editTextSiteCode?.setText(numberIs)
//                    binding.editTextCustomerCode?.setText(numberIs)
//                } else {
//                    // Handle API error
//                    Log.e("API Error", "API call failed with code: ${response.code()}")
//                    // progressDialog.dismiss()
//                }
//            }
//
//            override fun onFailure(call: Call<NextAddCustomerMonitoring>, error: Throwable) {
//                Log.e("AddCustomerMonitoring", "onFailure", error)
//                //    progressDialog.dismiss()
//                // Handle network error
//                Log.e("Network Error", "Failed to make API call: ${error.message}")
//            }
//        })
//    }

    private fun getNextAddCustomer() {

        val random7DigitNumber = (1000000..9999999).random().toString()

        binding.editTextSiteCode?.setText(random7DigitNumber)
        binding.editTextCustomerCode?.setText(random7DigitNumber)
    }


    private fun flattenChannels(channels: List<ChannelList>, level: Int = 0): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            val prefix = if (channel.nodeLevel == 1) "     • " else "• "

            if (channel.name == "DELIVERY SERVICE") {
                flattened.add("$prefix${channel.name}")
                // flattened.addAll(flattenChannels(channel.children, level + 1))
            }
        }
        return flattened
    }

    private fun flattenSubChannels(channels: List<ChannelSubList>, level: Int = 0): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            if (channel.customerCategoryName == "HOUSE HOLD") {
                flattened.add(channel.customerCategoryName.toString())
            } else if (channel.customerCategoryName == "CARD HOLDERS") {
                flattened.add(channel.customerCategoryName.toString())
            } else if (channel.customerCategoryName == "GROUP CORPORATE STAFF") {
                flattened.add(channel.customerCategoryName.toString())
            }
        }
        return flattened
    }

    private fun flattenCardListName(
        channels: List<DiscountCardList>,
        level: Int = 0
    ): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            flattened.add(channel.customerCategoryName.toString())
        }
        return flattened
    }

    private fun flattenCardIdListName(
        channels: List<DiscountCardList>,
        level: Int = 0
    ): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            flattened.add(channel.id.toString())
        }
        return flattened
    }

    private fun flattenSubSecondChannels(
        channels: List<SubChannelList>,
        mSubTrade: String
    ): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->

            if (mSubTrade == "HOUSE HOLD") {
                if (channel.subSecondaryChannelName == "HOUSE HOLD") {
                    flattened.add(channel.subSecondaryChannelName)
                }
            } else if (mSubTrade == "GROUP CORPORATE STAFF") {
                val ids = listOf(
                    "27","113","108","8","30","11","0","86","21","18",
                    "142","103","14","112","26","144","147","148",
                    "149","151","152","153","24","154","155","156","28"
                )

                if (ids.contains(channel.id.toString())) {
                    flattened.add(channel.subSecondaryChannelName)
                }
            } else {
                if (channel.subSecondaryChannelName == "HOMATWATAN_FAZA_SAADA") {
                    flattened.add(channel.subSecondaryChannelName)
                } else if (channel.subSecondaryChannelName == "FAZAA") {
                    flattened.add(channel.subSecondaryChannelName)
                } else if (channel.subSecondaryChannelName == "ESAAD") {
                    flattened.add(channel.subSecondaryChannelName)
                } else if (channel.subSecondaryChannelName == "HOMATWATTAN") {
                    flattened.add(channel.subSecondaryChannelName)
                } else if (channel.subSecondaryChannelName == "AL SAADA") {
                    flattened.add(channel.subSecondaryChannelName)
                }
            }

        }
        return flattened
    }

    private fun flattenSubSecondIdChannels(
        channels: List<SubChannelList>,
        mSubTrade: String
    ): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->

            if (mSubTrade == "HOUSE HOLD") {
                if (channel.subSecondaryChannelName == "HOUSE HOLD") {
                    flattened.add(channel.id.toString())
                }
            } else if (mSubTrade == "GROUP CORPORATE STAFF") {
                val ids = listOf(
                    "27","113","108","8","30","11","0","86","21","18",
                    "142","103","14","112","26","144","147","148",
                    "149","151","152","153","24","154","155","156","28"
                )

                if (ids.contains(channel.id.toString())) {
                    flattened.add(channel.id.toString())
                }
            } else {
                if (channel.subSecondaryChannelName == "HOMATWATAN_FAZA_SAADA") {
                    flattened.add(channel.id.toString())
                } else if (channel.subSecondaryChannelName == "FAZAA") {
                    flattened.add(channel.id.toString())
                } else if (channel.subSecondaryChannelName == "ESAAD") {
                    flattened.add(channel.id.toString())
                } else if (channel.subSecondaryChannelName == "HOMATWATTAN") {
                    flattened.add(channel.id.toString())
                } else if (channel.subSecondaryChannelName == "AL SAADA") {
                    flattened.add(channel.id.toString())
                }
            }

        }
        return flattened
    }

    private fun flattenSubChannelsId(channels: List<ChannelSubList>, level: Int = 0): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            if (channel.customerCategoryName == "HOUSE HOLD") {
                flattened.add(channel.id.toString())
            } else if (channel.customerCategoryName == "GROUP CORPORATE STAFF") {
                flattened.add(channel.id.toString())
            } else if (channel.customerCategoryName == "CARD HOLDERS") {
                flattened.add(channel.id.toString())
            }
        }
        return flattened
    }

    private fun flattenChannelsId(channels: List<ChannelList>, level: Int = 0): List<String> {
        val flattened = mutableListOf<String>()
        channels.forEach { channel ->
            val prefix = if (channel.nodeLevel == 1) "     • " else "• "

            if (channel.name == "DELIVERY SERVICE") {
                flattened.add(channel.id.toString())
                // flattened.addAll(flattenChannels(channel.children, level + 1))
            }
        }
        return flattened
    }

    private fun getCusChannelResponse() {
        progressDialog.show()
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
                    progressDialog.dismiss()
                    allChannels = response.body()?.data ?: listOf()
                    val flattenedChannelNames = flattenChannels(allChannels)
                    val flattenedChannelId = flattenChannelsId(allChannels)
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedChannelNames
                    )
                    binding.etTradeChannel?.setAdapter(adapter)
                    binding.etTradeChannel?.setText(flattenedChannelNames[0], false)
                    binding.etTradeChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = flattenedChannelId[position]
                        tradeChannelId = selectedPaymentTerm.toInt()
                        getCusTradeSubChannedl()
                    }

                } else {
                    // Handle API error
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    // Handle errors as required
                }
            }

            override fun onFailure(call: Call<ChannelListResponse>, error: Throwable) {
                Log.e("ChannelListResponse", "onFailure", error)
                progressDialog.dismiss()
                // Handle network error as required
            }
        })
    }

    private fun getCusTradeSubChannedl() {
        progressDialog.show()
        ApiClient.getClient(this).getCusTradeSubResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            tradeChannelId
        ).enqueue(object : Callback<SubChannelListResponse> {
            override fun onResponse(
                call: Call<SubChannelListResponse>, response: Response<SubChannelListResponse>
            ) {
                Log.d("ChannelListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    allSubChannels = (response.body()?.data ?: listOf()) as List<ChannelSubList>
                    //val channelNames = allSubChannels.map { it.customerCategoryName ?: "" }
                    val flattenedSubChannelNames = flattenSubChannels(allSubChannels)
                    val flattenedSubChannelIds = flattenSubChannelsId(allSubChannels)
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedSubChannelNames
                    )

                    binding.etTradeSubChannel?.setAdapter(adapter)
                    binding.etTradeSubChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = flattenedSubChannelIds[position]
                        val subChannelName = flattenedSubChannelNames[position]
                        tradeSubChannelId = selectedPaymentTerm.toInt()
                        binding.etsubSecTradeChannel!!.setText("")
                        binding.etsubSecTradeChannel!!.dismissDropDown()
                        getSubSecTradeChannel(subChannelName)
                        if (subChannelName == "CARD HOLDERS") {
                            getDiscountCardChannedl()
                            binding.lytCardLayt!!.visibility = View.VISIBLE
                        } else {
                            binding.lytCardLayt!!.visibility = View.GONE
                        }
                    }
                } else {
                    // Handle API error
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    // Handle errors as required
                }
            }

            override fun onFailure(call: Call<SubChannelListResponse>, error: Throwable) {
                progressDialog.dismiss()
                Log.e("ChannelListResponse", "onFailure", error)
                // Handle network error as required
            }
        })
    }

    private fun getSubSecTradeChannel(mSubTred: String) {
        progressDialog.show()
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
                    progressDialog.dismiss()
                    val channelListResponse = response.body()
                    val channelNames =
                        channelListResponse?.data?.map { it.subSecondaryChannelName } ?: listOf()
                    val flattenedSubSecChannelNames =
                        flattenSubSecondChannels(channelListResponse?.data!!, mSubTred)
                    val flattenedSubSecChannelId =
                        flattenSubSecondIdChannels(channelListResponse?.data!!, mSubTred)
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedSubSecChannelNames
                    )
                    binding.etsubSecTradeChannel?.setAdapter(adapter)

                    if (binding.etTradeSubChannel!!.text.toString() == "HOUSE HOLD") {
                        binding.etsubSecTradeChannel?.setText(flattenedSubSecChannelNames[0], false)
                        subSecTradeChannelId = flattenedSubSecChannelId[0].toInt()
                    }
                    binding.etsubSecTradeChannel!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = flattenedSubSecChannelId[position]
                        subSecTradeChannelId = selectedPaymentTerm.toInt()
                    }

                } else {
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with code: ${response.code()}")

                }
            }

            override fun onFailure(call: Call<SubSecTradeChannelResponse>, error: Throwable) {
                Log.e("ChannelListResponse", "onFailure", error)
                progressDialog.dismiss()

            }
        })
    }

    private fun getDiscountCardChannedl() {
        progressDialog.show()
        ApiClient.getClient(this).getDiscountCardResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            tradeSubChannelId
        ).enqueue(object : Callback<DiscountCardListResponse> {
            override fun onResponse(
                call: Call<DiscountCardListResponse>, response: Response<DiscountCardListResponse>
            ) {
                Log.d("ChannelListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    allDiscountCardels =
                        (response.body()?.data ?: listOf()) as List<DiscountCardList>
                    //val channelNames = allSubChannels.map { it.customerCategoryName ?: "" }
                    val flattenedSubChannelNames = flattenCardListName(allDiscountCardels)
                    val flattenedSubChannelIds = flattenCardIdListName(allDiscountCardels)
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        flattenedSubChannelNames
                    )
                    binding.etDiscountCard?.setAdapter(adapter)
                    binding.etDiscountCard!!.setOnItemClickListener { parent, view, position, id ->
                        val selectedPaymentTerm = flattenedSubChannelIds[position]
                        discountCardId = selectedPaymentTerm.toInt()
                    }
                } else {
                    // Handle API error
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    // Handle errors as required
                }
            }

            override fun onFailure(call: Call<DiscountCardListResponse>, error: Throwable) {
                progressDialog.dismiss()
                Log.e("ChannelListResponse", "onFailure", error)
                // Handle network error as required
            }
        })
    }

    private fun getPaymentTermList() {
        progressDialog.show()
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
                    progressDialog.dismiss()
                    val paymentTermNames = response.body()?.data?.map { it.name } ?: listOf()
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        paymentTermNames
                    )
//                    binding.etpaymentOptions!!.setAdapter(adapter)
//                    binding.etpaymentOptions!!.setOnItemClickListener { parent, view, position, id ->
//                        val selectedPaymentTerm = response.body()?.data?.get(position)
//                        numberOfDays = selectedPaymentTerm?.numberOfDays ?: 0
//
//                    }


                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<PaymentTermListResponse>, error: Throwable) {
                progressDialog.dismiss()
                Log.e("PaymentTermListResponse", "onFailure", error)

            }
        })
    }

    private fun getLandmarkList() {
        progressDialog.show()

        val jObj = JsonObject()
        if (mPreferenceManager!!.getString(AppConstants.ROLE) == "6") {
            jObj.addProperty(
                "route_id",
                ""
            )
        } else {
            jObj.addProperty(
                "route_id",
                routeId
            )
        }

        Log.e("Route_Id", routeId)
        ApiClient.getClient(this).getLandmarkListResponse(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<LandmarkListResponse> {
            override fun onResponse(
                call: Call<LandmarkListResponse>, response: Response<LandmarkListResponse>
            ) {
                Log.d("LandmarkListResponse", "onResponse")
                dismissDialog()
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val landmarkList = response.body()?.data?.map { it.landmarkName } ?: listOf()
                    val adapter = ArrayAdapter(
                        this@AddCustomerActivity,
                        android.R.layout.simple_dropdown_item_1line,
                        landmarkList
                    )
//                    binding.etLandmark!!.setAdapter(adapter)
//                    binding.etLandmark!!.setOnItemClickListener { parent, view, position, id ->
//                        val selectedPaymentTerm = response.body()?.data?.get(position)
//                        landmarkId = selectedPaymentTerm?.landmarkId ?: 0
//
//                    }

                    binding.etLandmark?.apply {
                        setAdapter(adapter)
                        threshold = 1 // Start showing suggestions after 1 character

                        setOnItemClickListener { _, _, position, _ ->
                            landmarkId = response.body()?.data?.getOrNull(position)?.id ?: 0
                        }

                        // Handle when user types text but doesn't select from dropdown
                        setOnFocusChangeListener { _, hasFocus ->
                            if (!hasFocus) {
                                val typedText = text.toString()
                                val matchedItem = response.body()?.data?.find {
                                    it.landmarkName.equals(
                                        typedText,
                                        ignoreCase = true
                                    )
                                }
                                landmarkId = matchedItem?.id ?: 0
                            }
                        }
                    }

                } else {

                    Log.e("API Error", "API call failed with code: ${response.code()}")
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<LandmarkListResponse>, error: Throwable) {
                progressDialog.dismiss()
                Log.e("LandmarkListResponse", "onFailure", error)

            }
        })
    }

    private fun getUaeRegions() {
        progressDialog.show()
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
                    progressDialog.dismiss()
                    regions = response.body()?.data?.region ?: emptyList()
                    val regionNames = regions.map { it.region_name }
//                    val adapter = ArrayAdapter(
//                        this@AddCustomerActivity,
//                        android.R.layout.simple_dropdown_item_1line,
//                        regionNames
//                    )
//                    binding.etRegion!!.setAdapter(adapter)
                    var mPosition = 0
                    for (mData in regions) {
                        if (regionId == mData.id.toString()) {
                            selectedRegionId = regions[mPosition].id
                            binding.etRegion?.setText(regions[mPosition].region_name, false)
                            binding.etCity?.setText(regions[mPosition].region_name)
                            binding.etCity!!.isClickable = false
                            break
                        }
                        mPosition++
                    }
                    binding.etRegion!!.isClickable = false
//                    binding.etRegion!!.setOnItemClickListener { parent, view, position, id ->
//                        selectedRegionId = regions[position].id
//                    }
                } else {
                    progressDialog.dismiss()
                    Log.e("API Error", "API call failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegionResponse>, error: Throwable) {
                progressDialog.dismiss()
                Log.e("getRegionListResponse", "onFailure", error)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            val uri = data.data
//            val fileName = uri?.let { getFileName(it) }
//
//            when (requestCode) {
//                REQUEST_CODE_CARD_FILE -> {
//                    binding.fileChosenCardId!!.text = fileName
//                    uri?.let {
//                        base64StringCard = uri?.let { convertToBase64(it) }
//                        Log.d("Owner File Base64", "Card File Base64: $base64StringCard")
//                    }
//                }
//            }
//        }
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            var uniqId = "cards" + Utils.getTimeStamp()!!

            var mPhotoFile = compressImage(
                Utils.getRealPathFromUri(
                    this,
                    imageUri!!
                ), uniqId, 0.5
            )

            val fileName = mPhotoFile?.name
            // val file = File(image_uri!!.path!!)
            val mCropUri = Uri.fromFile(mPhotoFile!!)
            //val fileName = File(mCropUri.path!!).name
            encodedImage = mCropUri.path
            Log.e("Image Path", encodedImage!!)
            base64StringCard = encodedImage!!
            binding.fileChosenCardId!!.text = fileName
//            val selectedImage = BitmapFactory.decodeStream(imageStream)
//            encodedImage = encodeImage(selectedImage)
        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            // For camera images, use the stored image_uri

            var uniqId = "cards" + Utils.getTimeStamp()!!

            var mPhotoFile = compressImage(
                Utils.getRealPathFromUri(
                    this,
                    image_uri!!
                ), uniqId, 0.5
            )

            val fileName = mPhotoFile?.name
            // val file = File(image_uri!!.path!!)
            val mCropUri = Uri.fromFile(mPhotoFile!!)
            //val fileName = File(mCropUri.path!!).name
            encodedImage = mCropUri.path
            Log.e("Image Path", encodedImage!!)
            base64StringCard = encodedImage!!
            binding.fileChosenCardId!!.text = fileName

//            val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mCropUri)
//
//            if (originalBitmap != null) {
//
//                binarizedBitmap = originalBitmap
//
//                // Set the binarized image to the ImageView
//                Glide.with(this)
//                    .load(binarizedBitmap)
//                    .into(imgStamp)
//
//                // Start the image cropping activity
//                // startImageCropActivity(image_uri!!)
//            } else {
//                Toast.makeText(this, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
//            }
            //   val thumbnail = data!!.extras!!.get("data") as Bitmap
            //   imgStamp.setImageBitmap(thumbnail)
            //   encodedImage = encodeImage(thumbnail)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Handle the result of the cropping activity
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                // Get the cropped image URI
                val croppedUri = result.uri
                try {
                    // Load the cropped image
                    val croppedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, croppedUri)

                    // Convert the cropped image to a binarized image
//                    binarizedBitmap = binarizeImage(croppedBitmap)
//
//                    // Set the binarized image to the ImageView
//                    Glide.with(this)
//                        .load(binarizedBitmap)
//                        .into(imgStamp)
//
//                    saveImage(orderList!![int_position].invoiceNo, binarizedBitmap!!)

                    //encodedImage = encodeImage(binarizedBitmap!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // Handle cropping error
                val error = result.error
                Toast.makeText(this, "Cropping failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
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

    fun compressImage(filePath: String, mINvoice: String, targetMB: Double = 1.0): File {
        var file = File(filePath)
        var fullSizeBitmap: Bitmap = BitmapFactory.decodeFile(filePath)
        var exif = ExifInterface(filePath)
        val exifOrientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        val exifDegree: Int = exifOrientationToDegrees(exifOrientation)
        fullSizeBitmap = rotateImage(fullSizeBitmap, exifDegree.toFloat())

        try {

            val fileSizeInMB = getFileSizeInMB(filePath)
            Log.d("TAG", "before compressImage: $fileSizeInMB")

            var quality = 100
            if (fileSizeInMB > targetMB) {//1.0 means target MB
                quality = ((targetMB / fileSizeInMB) * 100).toInt()
            }

            val storagestate = Environment.getExternalStorageState()
            var documentsFolder: File? = null
            if (storagestate == Environment.MEDIA_MOUNTED) {
                documentsFolder = File(
                    Environment.getExternalStorageDirectory(),
                    "Download" + File.separator + "Invoice"
                )
                if (!documentsFolder.exists()) {
                    documentsFolder.mkdirs()
                }
            }

            var filename = ""
            filename = "$mINvoice.jpeg"
            val dest = File(documentsFolder, filename)

            val fileOutputStream = FileOutputStream(dest)
            fullSizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.close()
            Log.d("TAG", "after compressImage: ${getFileSizeInMB(dest.path)}")
            file = File(dest.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    //here we get file size
    fun getFileSizeInMB(filePath: String): Double {
        val file = File(filePath)
        val length = file.length()
        val fileSizeInKB = (length / 1024).toString().toDouble()
        return (fileSizeInKB / 1024).toString().toDouble()
    }

    //here we get rotation of the image
    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    //here we change the rotation of the captured image
    fun rotateImage(fullSizeBitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            fullSizeBitmap,
            0,
            0,
            fullSizeBitmap.width,
            fullSizeBitmap.height,
            matrix,
            true
        )
    }

}