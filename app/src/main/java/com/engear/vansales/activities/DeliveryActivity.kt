package com.engear.vansales.activities

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.engear.vansales.R
import com.engear.vansales.adapter.DeliveryItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityDeliveryBinding
import com.engear.vansales.databinding.DeliveryReasonDialogBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.databinding.StampDialogBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.FileCompressor
import com.engear.vansales.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DeliveryActivity : BaseActivity(), OnItemClickListener {
    lateinit var binding: ActivityDeliveryBinding
    var deliveryList: MutableList<DeliveryHeader>? = ArrayList()
    var customerId: String = ""
    var lobCode: String = ""
    var mAdapter: DeliveryItemAdapter? = null
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var balance = ""
    private val pickImage = 100
    private val CAMERA = 200
    private var imageUri: Uri? = null
    var encodedImage: String? = ""
    var str_reason: String = ""
    var mProgressDialog: ProgressDialog? = null
    var invoice_stamp: Int = 1;
    var int_position: Int = 0
    var mDeliveNo: String = ""
    var mICashBalance: String = ""
    var mCustomerBalance: String = ""
    var accountNymber: String = ""
    var mMobile: String = ""
    var mEmail: String = ""
    private var binarizedBitmap: Bitmap? = null
    var mCompressor: FileCompressor? = null

    //Invoice cancel
    private var image_uri: Uri? = null
    val arrCancelReason = arrayOf(
        "Customer Change their Mind",
        "Recurring Order - Not Needed",
        "Wrong Order",
        "Window Time Missed / Dark Store Space",
        "IT Issue (Duplicate Order / Account / Price Issue)"
    )

    val arrRESReason = arrayOf(
        "Customer not at Home - No Outdoor Delivery Allowed",
        "Customer not at Home - No iCash/Card Payment",
        "Item Not Loaded or OOS",
        "Wrong Route",
        "Customer premises not accessible",
        "Vehicle Breakdown"
    )

    companion object {
        var isDelvieryDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@DeliveryActivity,
            "On Delivery Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Delivery")

        mCompressor = FileCompressor(this)

        if (!OpenCVLoader.initDebug()) {
            Log.e(ContentValues.TAG, "OpenCV initialization failed.")
        } else {
            Log.d(ContentValues.TAG, "OpenCV initialization succeeded.")
        }

        var customerCode = intent.getStringExtra("customer_code")
        balance = intent.getStringExtra("balance")!!
        mICashBalance = intent.getStringExtra("iCashBalance")!!
        accountNymber = intent.getStringExtra("accountNumber")!!
        mMobile = intent.getStringExtra("mobile")!!
        mEmail = intent.getStringExtra("email")!!
        mCustomerBalance = intent.getStringExtra("accountBalance")!!
        customerObj = db!!.getCustomerDetail(customerCode!!)
        customerId = customerObj!!.user_id!!.toString()

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
        }

        userInfo = mPreferenceManager!!.getUserInfo()
        setActivity()
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getDelivery()
        })

        getCustomerDelivery()
    }

    private fun getCustomerDelivery() {
        deliveryList!!.clear()
        deliveryList = ArrayList()

        deliveryList = db!!.getCustomerDelivery(customerId, lobCode)

        Collections.sort(deliveryList, Comparator<DeliveryHeader?> { o1, o2 ->
            try {
                return@Comparator SimpleDateFormat("h:mm:ss").parse(o1!!.deliveryTime)
                    .compareTo(SimpleDateFormat("hh:mm:ss").parse(o2!!.deliveryTime))
            } catch (e: ParseException) {
                return@Comparator 0
            }
        })
//        for (it in deliveryList!!){
//            Log.e("after_time",it.deliveryTime)
//        }
        setUpRecycler()
    }

    private fun setUpRecycler() {


        binding.rvDeliveryList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = DeliveryItemAdapter(this, deliveryList!!)
        binding.rvDeliveryList.adapter = mAdapter

    }

    override fun onItemsClick(position: Int, tag: String) {
        when (tag) {
            "click" -> {
                if (deliveryList!![position].status != 1) {
                    startActivity(Intent(this, DeliveryDetailActivity::class.java).apply {
                        this.putExtra("delivery_no", deliveryList!![position].deliveryNumber)
                        this.putExtra("customer_code", db!!.getCustomerCode(customerId))
                        this.putExtra("delivery_id", deliveryList!![position].id.toString())
                        this.putExtra("order_id", deliveryList!![position].orderId.toString())
                        this.putExtra("lob_code", lobCode)
                        this.putExtra("balance", balance)
                        this.putExtra("iCashBalance", mICashBalance)
                        this.putExtra("accountBalance", mCustomerBalance)
                        this.putExtra("accountNumber", accountNymber)
                        this.putExtra("mobile", mMobile)
                        this.putExtra("email", mEmail)
                    })
                }
            }

            "dialog" -> {
                if (deliveryList!![position].status == 0) {
                    int_position = position
                    showDIalog(position)
                }
            }

            "InvoiceCancel" -> {
                int_position = position
                mDeliveNo = deliveryList!![position].deliveryNumber
                reasonDialog(deliveryList!![position].deliveryNumber)
            }

            "StampUpload" -> {
                if (Utils.isNetworkAvailable(this@DeliveryActivity)) {
                    StampUploadDialog(deliveryList!![position].deliveryNumber)
                } else {
                    Toast.makeText(
                        this@DeliveryActivity,
                        "Network not available!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_survey -> {
                startActivity(
                    Intent(this, TransactionsActivity::class.java).apply {
                        this.putExtra("customer", customerObj!!.customer_code)

                    })
                return true
            }
        }
        return false
    }

    private fun showDIalog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Select Option")
            .setMessage("Do you want to Cancel or Reschedule Delivery?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                "Cancel"
            ) { dialog, whichButton ->
                dialog.dismiss()
                deleteDialog(position)
                //deleteDialog(position)
            }
            .setNegativeButton("Reschedule", { dialog, whichButton ->
                holdDialog(position)
            }).show()
    }

    private fun holdDialog(position: Int) {
        showConfirmAlertDialog(
            "Are you sure you want to Reschedule this Delivery?",
            object : OnConfirmationDialogClick {
                override fun onOk() {
                    showDateDialog(position)
                }

                override fun onCancel() {
                }
            })
    }

    private fun showDateDialog(position: Int) {
        val c = Calendar.getInstance()

// After 7 PM → minimum date is day after tomorrow
        if (c.get(Calendar.HOUR_OF_DAY) >= 19) {
            c.add(Calendar.DAY_OF_MONTH, 2)
        }

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DAY_OF_MONTH, 1)
        }

        val dpd = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val mDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )

                encodedImage = ""
                deliveryCommentDialog(position, "Hold", mDate)
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )

        dpd.datePicker.minDate = c.timeInMillis

        dpd.show()
    }


    private fun deleteDialog(position: Int) {
        showConfirmAlertDialog(
            "Are you sure you want to delete this Delivery?",
            object : OnConfirmationDialogClick {
                override fun onOk() {
                    encodedImage = ""
                    deliveryCommentDialog(position, "delete", "")
                }

                override fun onCancel() {
                }
            })
    }

    private fun getDelivery() {

        var date = mPreferenceManager!!.getString(AppConstants.STARTDATE)
        if (date!!.isEmpty()) {
            date = Utils.getCurrentDate()
        }
        val jObj = JsonObject()
        jObj.addProperty("date", date)
        jObj.addProperty(
            "salesman_id",
            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.userId
        )

        ApiClient.getClient(this).getDeliveryDate(
            "Bearer ${mPreferenceManager!!.getString(AppConstants.TOKEN)}",
            MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<DeliveryData> {
            override fun onResponse(
                call: Call<DeliveryData>,
                response: Response<DeliveryData>
            ) {
                if (response.isSuccessful) {
                    //Utils.logData(this@DownloadingDataActivity, "Customer Response")
                    Log.e("deliveries", Gson().toJson(response.body()))
                    if (response.body()!!.status) {
                        db!!.insertDelivery(response.body()!!.data)
                    }
                }
                getCustomerDelivery()
                binding.swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<DeliveryData>, error: Throwable) {
                //Utils.logData(this@DownloadingDataActivity, "Order Type Failed")
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = db!!.getCustomerCode(customerId)
        mVisited.activityName = "delivery"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "2")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(
            broadcastReceiver_Dinvoice,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION_DELIVERY)
        )

        if (DeliveryDetailActivity.isDeliveryDone) {
            DeliveryDetailActivity.isDeliveryDone = false
            isDelvieryDone = true
            getCustomerDelivery()
        }
    }

    override fun onBackPressed() {
        var action = ""
        if (deliveryList!!.size > 0) {
            var isPending = false
            for (minv in deliveryList!!) {

                if (minv.status != 0) {
                    if (minv.isDeleted != "1") {
                        var mInvoice = minv.deliveryNumber!!
                        if (customerObj!!.channel_id == "1") {
                            mInvoice = mInvoice!!.replace("S1", "HH")
                        }
                        var mStamp = db!!.getInvoiceStamp(mInvoice)
                        if (mStamp == "") {
                            isPending = true
                            break
                        }
                    }
                }
            }

            if (isPending) {
                Utils.displayAlert(this, "Kindly take stamp for invoice")
            } else {
                super.onBackPressed()
            }
        } else {
            if (isDelvieryDone) {
                isDelvieryDone = false
                CustomerDetailActivity.completedTask++
                action = "Delivery completed"
                var endTime = Utils.get24CurrentTime()
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            } else {
                db!!.deleteVisitActivity(activityId!!)
            }
            super.onBackPressed()
        }

    }

    private fun deliveryCommentDialog(position: Int, mType: String, mDate: String) {
        val factory = LayoutInflater.from(this)
        val binding: DeliveryReasonDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.delivery_reason_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Reason"

        var arrReason: Array<String>? = null
        var reason = ""

        if (mType == "Hold") {
            arrReason = arrRESReason
            binding.txtCancle!!.visibility = View.GONE
            binding.imgStamp!!.visibility = View.GONE
            binding.lytReason!!.visibility = View.VISIBLE
            binding.edtComment!!.visibility = View.GONE
        } else {
            arrReason = arrCancelReason
            binding.txtCancle!!.visibility = View.VISIBLE
            binding.imgStamp!!.visibility = View.VISIBLE
            binding.lytReason!!.visibility = View.VISIBLE
            binding.edtComment!!.visibility = View.GONE
        }

        imgStamp = deleteDialogView.findViewById(R.id.imgStamp)

        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)


        val dataReasonAdapter = arrReason?.let {
            ArrayAdapter<String>(
                applicationContext,
                R.layout.my_spinner_item, it
            )
        }
        dataReasonAdapter!!.setDropDownViewResource(R.layout.my_spinner_item)
        binding.reasonSpinner!!.adapter = dataReasonAdapter
        binding.reasonSpinner.setSelection(0)

        binding.reasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                reason = arrReason[position]!!.toString()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        binding.imgStamp!!.setOnClickListener {
            showPictureDialog(binding.imgStamp)
        }

        binding.btnConfirm.setOnClickListener {

            var orderComment = reason
            var mSType = ""
            if (mType == "Hold") {
                mSType = "Reschedule"
            } else {
                mSType = "Cancel"
            }

            //    if (mType == "Hold") {
            if (orderComment.isEmpty()) {
                Toast.makeText(this, "Please select Reason", Toast.LENGTH_SHORT).show()
            } else {
                deleteDialog.dismiss()
                holdDeliveryRequest(
                    deliveryList!![position].mOrder.order_number,
                    mDate,
                    position,
                    reason,
                    mSType
                )
            }
//            } else {
//
//                if (orderComment.isEmpty()) {
//                    Toast.makeText(this, "Please select Reason", Toast.LENGTH_SHORT).show()
//                } else if (encodedImage!!.isEmpty()) {
//                    Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show()
//                } else {
//
//                    db!!.updateDelvieryDeletePost(
//                        deliveryList!![position].deliveryNumber,
//                        orderComment,
//                        encodedImage!!
//                    )
//                    db!!.updateDelvieryItemDeletePost(deliveryList!![position].deliveryNumber)
//                    deliveryList!![position].isDeleted = "1"
//                    deliveryList!![position].status = 1
//                    mAdapter!!.notifyDataSetChanged()
//
//                    val transaction = Transaction()
//                    transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_DELIVERY_CANCEL
//                    transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
//                    transaction.tr_customer_num = customerObj!!.customer_code
//                    transaction.tr_customer_name =
//                        customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
//                    transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
//                    transaction.tr_invoice_id = deliveryList!![position].deliveryNumber
//                    transaction.tr_order_id = ""
//                    transaction.tr_collection_id = ""
//                    transaction.tr_pyament_id = ""
//                    transaction.tr_printData = ""
//                    transaction.tr_is_posted = "No"
//                    db!!.insertTransaction(transaction)
//
//                    deleteDialog.dismiss()
//                }
//
//            }
//

        }

        deleteDialog.show()
    }

    private fun StampUploadDialog(invoiceNo: String?) {

        val factory = LayoutInflater.from(this)
        val binding: StampDialogBinding =
            DataBindingUtil.inflate(factory, com.engear.vansales.R.layout.stamp_dialog, null, false)

        val deleteDialogView: View = binding.root

        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)
        deleteDialog.setCancelable(false)

        var mInvoice = invoiceNo!!
        if (customerObj!!.channel_id == "1") {
            mInvoice = mInvoice!!.replace("S1", "HH")
        }

        binding.txtInvoice.text = "Invoice Number: " + mInvoice.toString()
        binding.txtStatus.text = "Status: " + "Pending"

        imgStamp = deleteDialogView.findViewById(com.engear.vansales.R.id.imgStamp)

        imgStamp.setOnClickListener {
            showPictureDialog(binding.imgStamp)
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val text = if (R.id.rgYes == checkedId) "Yes" else "No"
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            if (text == "Yes") {
                invoice_stamp = 1
            } else {
                invoice_stamp = 0
            }
        }

        binding.btnUpload.setOnClickListener {

            if (encodedImage == "") {
                Toast.makeText(
                    this@DeliveryActivity,
                    "Choose Image!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //  getStampUpload(invoiceNo, deleteDialog.dismiss())
                insertStampSync(mInvoice!!)
                deleteDialog.dismiss()
            }

        }

        deleteDialog.show()
    }

    fun insertStampSync(invNo: String) {

        db!!.insertStamp(invNo, invoice_stamp.toString(), encodedImage!!)

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_STAMP_INVOICE
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = ""
        transaction.tr_collection_id = invNo
        transaction.tr_pyament_id = ""
        transaction.tr_is_posted = "No"
        transaction.tr_printData = ""
        db!!.insertTransaction(transaction)

        db!!.updateStampStatus(
            invNo.toString(),
            invoice_stamp.toString()
        )
        // orderList!![int_position].isStamp = invoice_stamp.toString()
        mAdapter!!.notifyDataSetChanged()

    }

    private fun showPictureDialog(imgStamp: ImageView) {
        val pictureDialog = android.app.AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> pickFromCamera()
            }
        }
        pictureDialog.show()
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

    private fun compressImageToSize(imageFile: File, targetSize: Int): File {
        var bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

        // Create an output stream to save the compressed image
        val byteArrayOutputStream = ByteArrayOutputStream()

        // Start with a high quality compression, then adjust to get the desired size
        var quality = 100
        var fileSize: Int

        // Loop and keep reducing quality until the file size is less than the target size
        do {
            byteArrayOutputStream.reset() // Reset the output stream before reusing it
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            fileSize = byteArray.size

            quality -= 5 // Reduce quality by 5 each time (tune this value for better control)

        } while (fileSize > targetSize && quality > 10) // Stop if size is under target or quality is too low

        // Save the compressed image to a file
        val compressedFile = File(cacheDir, "compressed_image.jpg")
        val fileOutputStream = FileOutputStream(compressedFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()

        return compressedFile
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

    lateinit var imgStamp: ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imgStamp.setImageURI(imageUri)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage = encodeImage(selectedImage)
        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            // For camera images, use the stored image_uri

            try {

                var mPhotoFile = compressImage(
                    Utils.getRealPathFromUri(
                        this,
                        image_uri!!
                    ), deliveryList!![int_position].deliveryNumber, 0.5
                )


                // val file = File(image_uri!!.path!!)
                val mCropUri = Uri.fromFile(mPhotoFile!!)
                encodedImage = mCropUri.path
                Log.e("Image Path", encodedImage!!)
                val originalBitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, mCropUri)

                if (originalBitmap != null) {

                    binarizedBitmap = originalBitmap

                    // Set the binarized image to the ImageView
                    Glide.with(this)
                        .load(binarizedBitmap)
                        .into(imgStamp)

                } else {
                    Toast.makeText(this, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            //   val thumbnail = data!!.extras!!.get("data") as Bitmap
            //   imgStamp.setImageBitmap(thumbnail)
            //   encodedImage = encodeImage(thumbnail)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            // Handle the result of the cropping activity
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                // Get the cropped image URI
                val croppedUri = result.uri

                //val selectedImage = data!!.data
//                var mPhotoFile: File? = null
//                try {
//                    mPhotoFile = mCompressor!!.compressToFile(
//                        File(
//                            Utils.getRealPathFromUri(
//                                this,
//                                croppedUri
//                            )
//                        )
//                    )
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//                val mCropUri = Uri.fromFile(mPhotoFile!!)
                //val filePat: String = mPhotoFile!!.absolutePath

                try {
                    // Load the cropped image
                    val croppedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, croppedUri)

                    // Convert the cropped image to a binarized image
                    //binarizedBitmap = binarizeImage(croppedBitmap)
                    binarizedBitmap = croppedBitmap

                    // Set the binarized image to the ImageView
                    Glide.with(this)
                        .load(binarizedBitmap)
                        .into(imgStamp)

                    saveImage(deliveryList!![int_position].deliveryNumber, binarizedBitmap!!)
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

    private fun binarizeImage(originalBitmap: Bitmap): Bitmap? {
        // Convert the input Bitmap to a Mat (OpenCV format)
        val srcMat = Mat(originalBitmap.width, originalBitmap.height, CvType.CV_8UC1)
        org.opencv.android.Utils.bitmapToMat(originalBitmap, srcMat)

        // Convert the Mat to grayscale
        val grayMat = Mat()
        //Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY)

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, Size(5.0, 5.0), 0.0)

        // Apply adaptive thresholding
        val binaryMat = Mat()
        Imgproc.adaptiveThreshold(
            grayMat,
            binaryMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY,
            11,  // Block size (adjust as needed)
            2.0 // Constant subtracted from the mean
        )

        // Convert the binary Mat back to a Bitmap
        val binarizedBitmap =
            Bitmap.createBitmap(binaryMat.cols(), binaryMat.rows(), Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(binaryMat, binarizedBitmap)

        // Release Mats to free up memory
        srcMat.release()
        grayMat.release()
        binaryMat.release()
        return binarizedBitmap
    }

    private fun startImageCropActivity(sourceUri: Uri) {
        CropImage.activity(sourceUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

//Stamp API call

    private fun getStampUpload(invoiceNo: String?, dismissDialog: Unit) {

        showOnlyProgressDialog()
        encodedImage = "data:image/jpeg;base64," + encodedImage

        val jObj = JsonObject()
        jObj.addProperty("invoice_id", invoiceNo!!)
        jObj.addProperty("stamp", invoice_stamp)
        jObj.addProperty("stamp_image", encodedImage)
        Log.e("Stamp Body", jObj.toString())

        ApiClient.getClient(this)
            .postStampUpload(
                "Bearer ${mPreferenceManager!!.getUserInfo()!!.data.accessToken}",
                MyApplication.X_DOMAIN, jObj
            )
            .enqueue(object :
                Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            Utils.logData(
                                this@DeliveryActivity,
                                "Stamp Response  ${response.body()!!.toString()}"
                            )
                            Log.e("Stamp Body", response.body()!!.toString())

                            if (response.body()!!["status"].asBoolean) {

                                try {
                                    var jData = response.body()!!["data"].asJsonObject
                                    db!!.updateStampStatus(
                                        invoiceNo.toString(),
                                        jObj["stamp"].asString
                                    )
                                    mAdapter!!.notifyDataSetChanged()
                                } catch (e: Exception) {
                                    Utils.displayAlert(
                                        mContext = this@DeliveryActivity,
                                        message = "Invoice not found!!"
                                    )
                                }


                            }
                        }
                    } else {
                        // Fail to Post
                        Utils.logData(
                            this@DeliveryActivity,
                            "Stamp Response Fail ${response.errorBody()!!.toString()}"
                        )

                    }
                    dismissDialog()


                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    // Fail to Post
                    dismissDialog()
                    if (error.message != null) {
                        Utils.logData(
                            this@DeliveryActivity,
                            "PosStamp Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@DeliveryActivity,
                            "Stamp Response Fail"
                        )

                    }
                }
            })
    }

    private fun reasonDialog(invoiceNo: String?) {

        val factory = LayoutInflater.from(this)
        val binding: OrderCommentDialogBinding =
            DataBindingUtil.inflate(factory, R.layout.order_comment_dialog, null, false)

        val deleteDialogView: View = binding.root

        binding.txtName.text = "Enter Reason"

        val deleteDialog: android.app.AlertDialog = android.app.AlertDialog.Builder(this).create()
        deleteDialog.setView(deleteDialogView)

        binding.btnConfirm.setOnClickListener {

            var comment = binding.edtComment.text.toString()
            str_reason = comment
            deleteDialog.dismiss()
            dialogPrint(this@DeliveryActivity, invoiceNo, comment)

        }

        deleteDialog.show()
    }

    fun dialogPrint(
        mContext: Context?,
        invoiceNo: String?,
        comment: String
    ) {
        val alertDialog = Dialog(mContext!!)
        alertDialog.setCancelable(false)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_load_request_permission)

        var txtMsg: TextView? = alertDialog.findViewById<Button>(R.id.txt_msg)
        txtMsg!!.text =
            "If you want to cancel this invoice you have to take approval from supervisor.Click on Yes send request to the supervisor for approval."


        var btnConfirm: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnConfirm)

        var btnCancel: android.widget.Button? =
            alertDialog.findViewById<Button>(R.id.btnCancel)

        btnConfirm!!.setOnClickListener {
            sendInvoiceCancelApprovalRequest(invoiceNo!!, comment)
            alertDialog.dismiss()
        }

        btnCancel!!.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun sendInvoiceCancelApprovalRequest(
        invoiceNo: String,
        comment: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("invoice_number", invoiceNo)

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(this@DeliveryActivity).sendInvoiceCancel(
            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
            jObj
        ).enqueue(object : Callback<InvoiceCancelData> {
            override fun onResponse(
                call: Call<InvoiceCancelData>,
                response: Response<InvoiceCancelData>
            ) {
                Log.e("Van GeoApproval Success", "Success")
                Log.e("Van GeoApproval request", response.body().toString())
                dismissDialog()
                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        showWaitDialog()
                    } else {
                        Utils.displayAlert(this@DeliveryActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@DeliveryActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<InvoiceCancelData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                dismissDialog()
                Utils.displayAlert(this@DeliveryActivity, "Request Sent fail!!")
            }
        })
    }

    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(this@DeliveryActivity);
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }

    private val broadcastReceiver_Dinvoice = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (mProgressDialog != null) {

                if (mProgressDialog!!.isShowing)
                    mProgressDialog!!.dismiss()
            }

            if (intent.getStringExtra("status") != null) {

                if (intent.getStringExtra("status") == "Apporve") {
                    if (intent.hasExtra("status")) {
                        showSimpleDialog(
                            "Your request has been approved by supervisor!",
                            "Alert",
                            object : OnDialog {
                                override fun onOk() {
                                    getStoreVanStockInvoice(int_position)
                                }
                            })
                    }
                } else {
                    Utils.displayAlert(
                        this@DeliveryActivity,
                        "Your request has been rejected by supervisor!"
                    )
                }
            } else {
                Utils.displayAlert(
                    this@DeliveryActivity,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

    private fun getStoreVanStockInvoice(position: Int) {

        var orderItemsAdmiralList: ArrayList<ItemData.Data>? = ArrayList()
        orderItemsAdmiralList = db!!.getAllInvoiceItem(deliveryList!![position].deliveryNumber!!)
        for (mData in orderItemsAdmiralList) {

            var mQty = 0.0
            for (mUOM in mData.itemSelectUOM) {
                mQty += Utils.getUOMLowerQty(mUOM.uomUPC, mUOM.uomQty).toDouble()
            }

            var existQty =
                db!!.getVanStockExistItemQty(
                    mData.id.toString(),
                    mData.vanUOM,
                    "Remaining"
                )
                    .toDouble()

            var mUpdateQty = existQty + mQty

            db!!.updateVanStockQty(
                mData.id.toString(),
                mData.vanUOM,
                mUpdateQty.toString()
            )

        }

        db!!.updateDelvieryOpen(deliveryList!![int_position].deliveryNumber!!)
        db!!.updateDelvieryItemOpenPost(deliveryList!![int_position].deliveryNumber!!)

        db!!.deleteInvoice(deliveryList!![int_position].deliveryNumber!!)
        db!!.deleteInvoiceItem(deliveryList!![int_position].deliveryNumber!!)
        db!!.deleteInvoiceBatchItem(deliveryList!![int_position].deliveryNumber!!)
        db!!.deleteCollection(deliveryList!![int_position].deliveryNumber!!)
        mAdapter!!.notifyDataSetChanged()
    }

    private fun holdDeliveryRequest(
        mDeliveryNo: String,
        mDate: String,
        position: Int,
        mReason: String,
        mType: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("order_number", mDeliveryNo)
        //jObj.addProperty("delivery_id", mDeliveryNo)
        jObj.addProperty("date", mDate)
        jObj.addProperty("reschedule_type", mType)
        jObj.addProperty("reason", mReason)

        Log.e("Delivery Request", jObj.toString())

        ApiClient.getClient(this).holdDelivery(
            "Bearer ${userInfo!!.data.accessToken}",
            MyApplication.X_DOMAIN, jObj
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    dismissDialog()
                    if (response.isSuccessful) {
                        //Utils.logData(this@DownloadingDataActivity, "Journey Plan Response")
                        if (response.body()!!["status"].asBoolean) {

                            Toast.makeText(
                                this@DeliveryActivity,
                                "Delivery Hold Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            db!!.updateDelvieryDeleteHoldPost(
                                deliveryList!![position].deliveryNumber,
                                mReason,
                                ""
                            )
                            db!!.updateDelvieryItemDeletePost(deliveryList!![position].deliveryNumber)

//                            db!!.updateDelvieryDeletePost(mDeliveryNo)
//                            db!!.updateDelvieryItemDeletePost(mDeliveryNo)
                            deliveryList!![position].isDeleted = "1"
                            deliveryList!![position].status = 1
                            mAdapter!!.notifyDataSetChanged()

                        } else {
                            Toast.makeText(
                                this@DeliveryActivity,
                                "Delivery Hold Fail!!",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } else {
                        Toast.makeText(
                            this@DeliveryActivity,
                            "Delivery Hold Fail!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    Toast.makeText(
                        this@DeliveryActivity,
                        "Delivery Hold Fail!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun saveImageAfterCompress(invoiceNo: String?, binarizedBitmap: Bitmap) {
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
        filename = invoiceNo!! + ".png"
        val dest = File(documentsFolder, filename)
        encodedImage = dest.absolutePath
        try {
            val out: FileOutputStream = FileOutputStream(dest)
            binarizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun saveImage(invoiceNo: String?, binarizedBitmap: Bitmap) {
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
        filename = invoiceNo!! + ".png"
        val dest = File(documentsFolder, filename)
        encodedImage = dest.absolutePath
        try {
            val out: FileOutputStream = FileOutputStream(dest)
            binarizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}