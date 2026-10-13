package com.engear.vansales.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
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
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.engear.vansales.R
import com.engear.vansales.adapter.InvoiceItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityInvoiceBinding
import com.engear.vansales.databinding.DialogCancelInvoiceBinding
import com.engear.vansales.databinding.OrderCommentDialogBinding
import com.engear.vansales.databinding.StampDialogBinding
import com.engear.vansales.firebase.MyFirebaseMessagingService
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Invoice
import com.engear.vansales.model.InvoiceCancelData
import com.engear.vansales.model.ItemData
import com.engear.vansales.model.Transaction
import com.engear.vansales.model.VisitedList
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.ResponseBody
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


class InvoiceActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityInvoiceBinding
    var customerCode: String? = null
    private var orderList: ArrayList<Invoice>? = ArrayList()
    var customerObj: CustomerData.DataCustomer? = null
    var visitId: String? = null
    var activityId: String? = null
    var lobCode: String = ""
    var balance: String = ""
    private var lobList: ArrayList<CustomerData.DataCustomer.Customerlob>? = null

    //Upload Stamp
    private val pickImage = 100
    private val CAMERA = 200
    private var imageUri: Uri? = null
    var encodedImage: String? = ""
    var mAdapter: InvoiceItemAdapter? = null
    var int_position: Int = 0
    var mProgressDialog: ProgressDialog? = null
    private var mDialog: Dialog? = null
    var cust_id: String = ""
    var mCUstLat: String = ""
    var mCustLon: String = ""
    var str_reason: String = ""
    var invoice_stamp: Int = 1;
    var mStampInvoice: String = ""
    private var binarizedBitmap: Bitmap? = null

    //Invoice cancel
    var invoiceCancelEnable: String = ""
    private var image_uri: Uri? = null
    var mICashBalance: String = ""
    var mCustomerBalance: String = ""

    companion object {
        var isInvoiceDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@InvoiceActivity,
            "On Invoice Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Sale")

        if (!OpenCVLoader.initDebug()) {
            Log.e(ContentValues.TAG, "OpenCV initialization failed.")
        } else {
            Log.d(ContentValues.TAG, "OpenCV initialization succeeded.")
        }

        customerCode = intent.getStringExtra("customer_code")
        balance = intent.getStringExtra("balance")!!
        mICashBalance = intent.getStringExtra("iCashBalance")!!
        mCustomerBalance = intent.getStringExtra("accountBalance")!!


        invoiceCancelEnable =
            mPreferenceManager!!.getString(AppConstants.INVOICECANCELENABLE).toString()

        customerObj = db!!.getCustomerDetail(customerCode!!)
        cust_id = customerObj!!.user_id.toString()
        mCUstLat = customerObj!!.latitude.toString()
        mCustLon = customerObj!!.longitude.toString()

        if (customerObj!!.customer_type_id.toString() == "2") {
            customerObj!!.credit_days = "0"
        } else {
            var mLimit = customerObj!!.credit_limit.toFloat()
            var approveLimit = db!!.getCustomerApproveLimit(customerObj!!.userDetail.id.toString())
            if (approveLimit.toFloat() > 0) {
                var approveOutstad =
                    db!!.getCustomerApproveLimitOutstanding(customerObj!!.userDetail.id.toString())
                var mApproveOutLimit = approveLimit.toFloat() + approveOutstad.toFloat()
                /// var mFinalLimit = mLimit + approveLimit.toFloat()
                var amtDue = 0.0
                amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                Log.e("cresitmilit", mApproveOutLimit.toString() + "--" + amtDue)
                balance = Utils.twoDigit((mApproveOutLimit - amtDue.toFloat())).toString()
            } else {
                var mFinalLimit = mLimit
                var amtDue = 0.0
                amtDue = db!!.getCustomerBalance(customerObj!!.userDetail.id.toString())
                Log.e("cresitmilit", mFinalLimit.toString() + "--" + amtDue)
                balance = Utils.twoDigit((mFinalLimit - amtDue.toFloat())).toString()
            }
        }

        if (intent.getStringExtra("lob_code") != null) {
            lobCode = intent.getStringExtra("lob_code")!!
            System.out.println("LOB-->" + lobCode)
        }
        setActivity()

        binding.btnAdd.setOnClickListener {
            Utils.logData(
                this@InvoiceActivity,
                "On Add button click"
            )

            if (mPreferenceManager!!.getString(AppConstants.ROLE) == "3") {
                showInvoiceDialog()
            } else {
                startActivity(Intent(me, SalesActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("isCoupon", "0")
                    this.putExtra("balance", balance.toString())
                    this.putExtra("iCashBalance", mICashBalance)
                    this.putExtra("accountBalance", mCustomerBalance)
                })
            }

        }

        getCustomerInvoice()

    }

    private fun showInvoiceDialog() {

        val arrUData = arrayOf<String>("Coupon Sales", "Normal Sales")

        val mBuilder = android.app.AlertDialog.Builder(this@InvoiceActivity)
        mBuilder.setTitle("Select an option")
        mBuilder.setSingleChoiceItems(arrUData, -1) { dialogInterface, i ->
            if (i == 0) {
                startActivity(Intent(me, SalesActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("isCoupon", "1")
                    this.putExtra("balance", balance.toString())
                    this.putExtra("iCashBalance", mICashBalance)
                    this.putExtra("accountBalance", mCustomerBalance)
                })
            } else {
                startActivity(Intent(me, SalesActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("lob_code", lobCode)
                    this.putExtra("isCoupon", "0")
                    this.putExtra("balance", balance.toString())
                    this.putExtra("iCashBalance", mICashBalance)
                    this.putExtra("accountBalance", mCustomerBalance)
                })
            }
            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun getCustomerInvoice() {

        orderList = ArrayList()
        orderList = db!!.getCustomerInvoices(customerCode!!)

        setUpRecycler()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //onBackPressed()
                onBackPressed()
                return true
            }

            R.id.action_survey -> {
                startActivity(
                    Intent(this, TransactionsActivity::class.java).apply {
                        this.putExtra("customer", customerCode)

                    })
                return true
            }
        }
        return false
    }

    private fun setUpRecycler() {
        binding.rvItemList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //binding.rvItemList.adapter = InvoiceItemAdapter(this, orderList)
        mAdapter = InvoiceItemAdapter(this, orderList)
        binding.rvItemList.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        getCustomerInvoice()
        registerReceiver(
            broadcastReceiver_invoice,
            IntentFilter(MyFirebaseMessagingService.BROADCAST_ACTION)
        )
    }

    override fun onItemsClick(position: Int, tag: String) {
        if (tag.equals("dialog")) {

            if (orderList!![position].isAccept == "1") {
                startActivity(Intent(this, OrderPostActivity::class.java).apply {
                    this.putExtra("order_no", orderList!![position].invoiceNo)
                    this.putExtra("type", "sales")
                    this.putExtra("customer_code", customerCode)
                })
            } else {
                startActivity(Intent(this, OrderDetailActivity::class.java).apply {
                    this.putExtra("order_no", orderList!![position].invoiceNo)
                    this.putExtra("type", "invoice")
                })
            }

        } else if (tag.equals("StampUpload")) {
            if (Utils.isNetworkAvailable(this@InvoiceActivity)) {
                StampUploadDialog(orderList!![position].invoiceNo)
                int_position = position
            } else {
                Toast.makeText(
                    this@InvoiceActivity,
                    "Network not available!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else if (tag.equals("InvoiceCancel")) {
            if (Utils.isNetworkAvailable(this@InvoiceActivity)) {
//                if (invoiceCancelEnable == "0") {
//
//                   // showCancelInvoiceDialog(position)
//                    int_position = position
//                    reasonDialog(orderList!![position].invoiceNo)
//                    //  getStoreVanStockInvoice(int_position)
//                } else {
                int_position = position
                reasonDialog(orderList!![position].invoiceNo)
                //   }

            } else {
                Toast.makeText(
                    this@InvoiceActivity,
                    "Network not available!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            showCancelInvoiceDialog(position)
        }
    }


    private fun showCancelInvoiceDialog(position: Int?) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Cancel Invoice")
        builder.setMessage("Are you sure you want to cancel " + orderList!![position!!].invoiceNo + " Invoice?")
        builder.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            dialog.cancel()
            getReasonCancelInvoiceDialog(position)

        }
        builder.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun getReasonCancelInvoiceDialog(position: Int) {
        val dialogBinding = DialogCancelInvoiceBinding.inflate(LayoutInflater.from(this))
        val dialogView = dialogBinding.root

        val alertDialog = AlertDialog.Builder(this).create().apply {
            setView(dialogView)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }

        dialogBinding.btnConfirm.setOnClickListener {
            if (dialogBinding!!.edtReason.text.toString().isNotEmpty()) {
                val transaction = Transaction()
                transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_INVOICE_CANCEL
                transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
                transaction.tr_customer_num = orderList!![position!!].invoiceNo
                transaction.tr_customer_name =
                    customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
                transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
                transaction.tr_invoice_id = orderList!![position!!].invoiceNo
                transaction.tr_order_id = ""
                transaction.tr_collection_id = ""
                transaction.tr_pyament_id = ""
                transaction.tr_printData = ""
                transaction.tr_is_posted = "No"
                transaction.tr_message = dialogBinding!!.edtReason.text.toString()
                db!!.insertTransaction(transaction)
                db!!.updateInvoiceDeletePost(
                    orderList!![position!!].invoiceNo!!,
                    dialogBinding!!.edtReason.text.toString()
                )
                alertDialog.dismiss()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please add cancel invoice reason",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        dialogBinding.btnCancel.setOnClickListener {

            alertDialog.dismiss()

        }

        alertDialog.show()

        ////////////////////////////
    }

    private fun postReason(reasonStr: String, id: String) {
        showOnlyProgressDialog()
        val jObj = JsonObject()

        jObj.addProperty("reason", reasonStr)
        var arr = JsonArray()
        arr.add(id)
        jObj.add("name", arr)
        Log.e("reasonrequest", jObj.toString())
        ApiClient.getClient(this)
            .PostReason("Bearer ${userInfo!!.data.accessToken}", MyApplication.X_DOMAIN, id, jObj)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    dismissDialog()
                    if (response.isSuccessful) {

                        Log.e("postReason", response.body()!!.string())
                    } else
                        Log.e("postReason", response.errorBody()!!.string())
                }

                override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                    // Utils.logData(this@DownloadingDataActivity, "Item Failed")
                    dismissDialog()
                }
            })
    }

    override fun onBackPressed() {
        var action = ""

        if (orderList!!.size > 0) {
            var isPending = false
            for (minv in orderList!!) {
                if (minv.isStamp == "") {
                    isPending = true
                    break
                }
            }

            if (isPending) {
                Utils.displayAlert(this, "Kindly take stamp for invoice")
            } else {
                super.onBackPressed()
            }
        } else {
            if (isInvoiceDone) {
                isInvoiceDone = false
                CustomerDetailActivity.completedTask++
                action = "Invoice created"
                var endTime = Utils.get24CurrentTime()
                db!!.updateCustomerVisitActivity(endTime, action, visitId!!, activityId!!)
            } else {
                db!!.deleteVisitActivity(activityId!!)
            }
            super.onBackPressed()
        }

    }

    private fun setActivity() {

        val mVisited = VisitedList()
        mVisited.customerId = customerObj!!.userDetail.id.toString()
        mVisited.customerCode = customerCode
        mVisited.activityName = "invoice"
        mVisited.activityAction = ""
        mVisited.startTime = Utils.get24CurrentTime()
        mVisited.endTime = ""
        activityId = Utils.getVisitActivityNo(customerObj!!.userDetail.id.toString(), "1")
        mVisited.activityId = activityId
        visitId = db!!.getCustomerVisitId(customerObj!!.userDetail.id.toString())
        mVisited.visitedOffId = visitId

        db!!.insertVisitedCustomerActivity(mVisited)
    }

    //Stamp Upload
    lateinit var radioButton: RadioButton


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
            mInvoice = invoiceNo!!.replace("S1", "HH")
        }

        binding.txtInvoice.text = "Invoice Number: " + mInvoice.toString()
        binding.txtStatus.text = "Status: " + "Pending"

        imgStamp = deleteDialogView.findViewById(com.engear.vansales.R.id.imgStamp)

        imgStamp.setOnClickListener {
            mStampInvoice = mInvoice
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
                    this@InvoiceActivity,
                    "Choose Image!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // getStampUpload(invoiceNo, deleteDialog.dismiss())
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
        orderList!![int_position].isStamp = invoice_stamp.toString()
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

    fun choosePhotoFromGallary() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
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

            var mPhotoFile = compressImage(
                Utils.getRealPathFromUri(
                    this,
                    image_uri!!
                ), mStampInvoice, 0.5
            )

            // val file = File(image_uri!!.path!!)
            val mCropUri = Uri.fromFile(mPhotoFile!!)
            encodedImage = mCropUri.path
            Log.e("Image Path", encodedImage!!)

            val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, mCropUri)

            if (originalBitmap != null) {

                binarizedBitmap = originalBitmap

                // Set the binarized image to the ImageView
                Glide.with(this)
                    .load(binarizedBitmap)
                    .into(imgStamp)

                // Start the image cropping activity
                // startImageCropActivity(image_uri!!)
            } else {
                Toast.makeText(this, "Failed to retrieve the image", Toast.LENGTH_SHORT).show()
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
                try {
                    // Load the cropped image
                    val croppedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, croppedUri)

                    // Convert the cropped image to a binarized image
                    binarizedBitmap = binarizeImage(croppedBitmap)

                    // Set the binarized image to the ImageView
                    Glide.with(this)
                        .load(binarizedBitmap)
                        .into(imgStamp)

                    saveImage(orderList!![int_position].invoiceNo, binarizedBitmap!!)

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

    private fun binarizeImage(originalBitmap: Bitmap): Bitmap? {
        // Convert the input Bitmap to a Mat (OpenCV format)
        val srcMat = Mat(originalBitmap.width, originalBitmap.height, CvType.CV_8UC1)
        org.opencv.android.Utils.bitmapToMat(originalBitmap, srcMat)

        // Convert the Mat to grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY)

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
                                this@InvoiceActivity,
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
                                    orderList!![int_position].isStamp = jObj["stamp"].asString
                                    mAdapter!!.notifyDataSetChanged()
                                } catch (e: Exception) {
                                    Utils.displayAlert(
                                        mContext = this@InvoiceActivity,
                                        message = "Invoice not found!!"
                                    )
                                }


                            }
                        }
                    } else {
                        // Fail to Post
                        Utils.logData(
                            this@InvoiceActivity,
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
                            this@InvoiceActivity,
                            "PosStamp Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@InvoiceActivity,
                            "Stamp Response Fail"
                        )

                    }
                }
            })
    }

    //Invoice Cancel Reason Dialog
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
            if (comment.isEmpty()) {
                Toast.makeText(this, "kindly please add reason for cancel", Toast.LENGTH_SHORT)
                    .show()
            } else {
                str_reason = comment
                deleteDialog.dismiss()
                dialogPrint(this@InvoiceActivity, invoiceNo, comment)
            }


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

//    private fun sendInvoiceCancelApprovalRequest(
//        invoiceNo: String,
//        comment: String
//    ) {
//
//        showOnlyProgressDialog()
//
//        val jObj = JsonObject()
//        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
//        jObj.addProperty("salesman_lat", AppConstants.Latitude)
//        jObj.addProperty("salesman_long", AppConstants.Longitude)
//        jObj.addProperty("invoice_no", invoiceNo)
//        jObj.addProperty("reason", comment)
//        jObj.addProperty("customer_id", cust_id)
//        jObj.addProperty("customer_lat", mCUstLat)
//        jObj.addProperty("customer_long", mCustLon)
//        jObj.addProperty(
//            "supervisor_id",
//            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
//        )
//        jObj.addProperty("type", "Invoice cancellation Void")
//
//        Log.e("Van GeoApproval request", jObj.toString())
//
//        ApiClient.getClient(this@InvoiceActivity).sendSupervisorGeoApprovalRequest(
//            "Bearer ${BaseActivity.userInfo!!.data.accessToken}", MyApplication.X_DOMAIN,
//            jObj
//        ).enqueue(object : Callback<GeoApprovalData> {
//            override fun onResponse(
//                call: Call<GeoApprovalData>,
//                response: Response<GeoApprovalData>
//            ) {
//                Log.e("Van GeoApproval Success", "Success")
//                Log.e("Van GeoApproval request", response.body().toString())
//                dismissDialog()
//                if (response.isSuccessful) {
//                    if (response.body()!!.status!!) {
//                        showWaitDialog()
//                    } else {
//                        Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
//                    }
//                } else {
//                    Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
//                }
//
//            }
//
//            override fun onFailure(call: Call<GeoApprovalData>, error: Throwable) {
//                Log.e("Van GeoApproval Fail", "Fail")
//                Utils.dismissDialog(mDialog!!)
//                Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
//            }
//        })
//    }

    private fun sendInvoiceCancelApprovalRequest(
        invoiceNo: String,
        comment: String
    ) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
//        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserInfo()!!.data.userInfo.id)
//        jObj.addProperty("salesman_lat", AppConstants.Latitude)
//        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("invoice_number", invoiceNo)
        jObj.addProperty("reason", comment)
//        jObj.addProperty("customer_id", cust_id)
//        jObj.addProperty("customer_lat", mCUstLat)
//        jObj.addProperty("customer_long", mCustLon)
//        jObj.addProperty(
//            "supervisor_id",
//            mPreferenceManager!!.getUserInfo()!!.data.userInfo.salesmanInfo.supervisor.id
//        )
//        jObj.addProperty("type", "Invoice cancellation Void")

        Log.e("Van GeoApproval request", jObj.toString())

        ApiClient.getClient(this@InvoiceActivity).sendInvoiceCancel(
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
                        Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
                    }
                } else {
                    Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
                }

            }

            override fun onFailure(call: Call<InvoiceCancelData>, error: Throwable) {
                Log.e("Van GeoApproval Fail", "Fail")
                dismissDialog()
                Utils.displayAlert(this@InvoiceActivity, "Request Sent fail!!")
            }
        })
    }

    fun showWaitDialog() {
        mProgressDialog = ProgressDialog(this@InvoiceActivity);
        mProgressDialog!!.setMessage("Please wait until supervisor approve..."); // Setting Message
        mProgressDialog!!.setTitle(""); // Setting Title
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog!!.show(); // Display Progress Dialog
        mProgressDialog!!.setCancelable(false);
    }

    private val broadcastReceiver_invoice = object : BroadcastReceiver() {
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
                        this@InvoiceActivity,
                        "Your request has been rejected by supervisor!"
                    )
                }
            } else {
                Utils.displayAlert(
                    this@InvoiceActivity,
                    "Your request has been rejected by supervisor!"
                )
            }
        }
    }

    private fun getStoreVanStockInvoice(position: Int) {
//        val transaction = Transaction()
//        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_INVOICE_CANCEL
//        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
//        transaction.tr_customer_num = orderList!![position!!].invoiceNo
//        transaction.tr_customer_name =
//            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
//        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
//        transaction.tr_invoice_id = orderList!![position!!].invoiceNo
//        transaction.tr_order_id = ""
//        transaction.tr_collection_id = ""
//        transaction.tr_pyament_id = ""
//        transaction.tr_printData = ""
//        transaction.tr_is_posted = "No"
//        transaction.tr_message = str_reason
//        db!!.insertTransaction(transaction)
//        db!!.updateInvoiceDeletePost(
//            orderList!![position!!].invoiceNo!!,
//            str_reason
//        )
        var orderItemsAdmiralList: ArrayList<ItemData.Data>? = ArrayList()
        orderItemsAdmiralList = db!!.getAllInvoiceItem(orderList!![position].invoiceNo!!)
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
            orderList!![int_position].isInvoiceCancel = "1"
            orderList!![int_position].isStamp = "0"
            db!!.updatCancelInvoiceStatus(
                orderList!![int_position].invoiceNo!!,
                "1"
            )
            db!!.deleteCollection(orderList!![int_position].invoiceNo!!)
            mAdapter!!.notifyDataSetChanged()

        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver_invoice)
    }


    /*private fun invoiceCancelRequest(mLimit: String, mDays: String, mCustomerId: String) {

        showOnlyProgressDialog()

        val jObj = JsonObject()
        jObj.addProperty("route_id", userInfo!!.data.userInfo.salesmanInfo.routeId)
        jObj.addProperty("salesman_id", mPreferenceManager!!.getUserId())
        jObj.addProperty(
            "supervisor_id",
            mPreferenceManager!!.getString(AppConstants.SUPERVISOR_ID)
        )
        jObj.addProperty("salesman_lat", AppConstants.Latitude)
        jObj.addProperty("salesman_long", AppConstants.Longitude)
        jObj.addProperty("requested_amount", mLimit)
        jObj.addProperty("no_of_days_requested", mDays)
        jObj.addProperty("customer_id", mCustomerId)
        Log.e("Unblock Request", jObj.toString())

        ApiClient.getClient(this).sendUnblockRequest(
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
                            db!!.updateCustomerRequestSend(mCustomerId)
                            db!!.insertOverDueRequest(mCustomerId, mLimit, mDays)
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                "Request sent Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                        }
                    } else {
                        Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                    }

                }

                override fun onFailure(call: Call<JsonObject>, error: Throwable) {
                    dismissDialog()
                    Utils.displayAlert(this@CustomerDetailActivity, "Request Sent fail!!")
                }
            })
    }*/

    public fun saveImage(invoiceNo: String?, binarizedBitmap: Bitmap) {
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