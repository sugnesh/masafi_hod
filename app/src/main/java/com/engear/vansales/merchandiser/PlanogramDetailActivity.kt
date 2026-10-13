package com.engear.vansales.merchandiser

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.engear.vansales.BuildConfig
import com.engear.vansales.adapter.ComplaintPagerAdaptor
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityPlanogramDetailBinding
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Planogram
import com.engear.vansales.model.Transaction
import com.engear.vansales.utils.FileCompressor
import com.engear.vansales.utils.Utils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PlanogramDetailActivity : BaseActivity() {

    lateinit var binding: ActivityPlanogramDetailBinding
    var customerCode: String? = null
    private var displayToolId: String? = null
    private var planogramId: String? = null
    private var name: String? = null
    private var mPlanogram: Planogram? = null
    private var customerObj: CustomerData.DataCustomer? = null
    var mPhotoFile: File? = null
    var mCompressor: FileCompressor? = null
    private val TAKE_PHOTO_REQUEST = 1
    private val REQUEST_GALLERY_PHOTO = 2
    private var strImageType = ""
    private var strBeforeImage = ""
    private var strAfterImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanogramDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {

        mCompressor = FileCompressor(this)
        customerCode = intent.getStringExtra("customer_code")
        displayToolId = intent.getStringExtra("display_tool_id")
        planogramId = intent.getStringExtra("planogramId")
        name = intent.getStringExtra("name")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        setToolbarTitle(name!!)

        mPlanogram = db!!.getPlanogramDetail(
            customerObj!!.userDetail.id.toString(),
            displayToolId!!,
            planogramId!!
        )
        setData()

        if (db!!.checkPlanogramCustomerPosted(
                customerObj!!.userDetail.id.toString(),
                displayToolId!!, planogramId!!
            )
        ) {
            binding.layoutSave.visibility = View.GONE
        } else {
            binding.layoutSave.visibility = View.VISIBLE
        }
    }

    private fun setData() {
        if (mPlanogram != null) {
            val viewPagerAdapter = ComplaintPagerAdaptor(me, mPlanogram!!.arrImages)
            binding.viewPager.adapter = viewPagerAdapter
            binding.dotsIndicators.setViewPager(binding.viewPager)

            if (mPlanogram!!.beforeImage!!.isNotEmpty()) {
                binding.layoutImage1.visibility = View.GONE
                binding.image1.visibility = View.VISIBLE
                Glide.with(this)
                    .load(mPlanogram!!.beforeImage)
                    .into(binding.image1)
            }

            if (mPlanogram!!.afterImage!!.isNotEmpty()) {
                binding.layoutImage2.visibility = View.GONE
                binding.image2.visibility = View.VISIBLE
                Glide.with(this)
                    .load(mPlanogram!!.afterImage)
                    .into(binding.image2)
            }

            binding.edtPlanogramComment.setText(mPlanogram!!.comment)
        }
    }

    private fun setClickEvent() {

        binding.layoutImage1.setOnClickListener {
            strImageType = "Before"
            selectImage()
        }

        binding.layoutImage2.setOnClickListener {
            strImageType = "After"
            selectImage()
        }

        binding.layoutSave.setOnClickListener {
            if (isValid()) {
                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(this@PlanogramDetailActivity)
                alertDialogBuilder.setTitle("Alert")
                    .setMessage("Are you sure you want to submit?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        addPlanogram()
                    }.setNegativeButton(
                        "No"
                    ) { dialog, which -> dialog.dismiss() }

                alertDialogBuilder.show()
            }
        }

    }

    private fun addPlanogram() {

        var uniqId = Utils.getPlanogramNo(mPreferenceManager!!.getUserId()!!)
        var mPlanogram = Planogram()
        mPlanogram.uniqueId = uniqId
        mPlanogram.planogramId = planogramId
        mPlanogram.distributionId = displayToolId
        mPlanogram.customer = customerObj!!.userDetail.id.toString()
        mPlanogram.beforeImage = strBeforeImage
        mPlanogram.afterImage = strAfterImage
        mPlanogram.comment = binding.edtPlanogramComment.text.toString()
        db!!.insertPlanogramPost(mPlanogram)


        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PLANOGRAM
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = uniqId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "merchandising")

        finish()
    }

    private fun isValid(): Boolean {

        if (strBeforeImage.isEmpty()) {
            showSnackBar(this, "Please capture before image!!")
            return false
        } else if (strAfterImage.isEmpty()) {
            showSnackBar(this, "Please capture after image!!")
            return false
        }
        return true
    }

    /**
     * Create file with current timestamp name
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "SFA_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val mFile = File.createTempFile(mFileName, ".png", storageDir)
        return mFile
    }

    /**
     * Capture image from camera
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST)
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO)
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(this@PlanogramDetailActivity)
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Take Photo") {
                requestStoragePermission(true)
            } else if (items[item] == "Choose from Library") {
                requestStoragePermission(false)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private fun requestStoragePermission(isCamera: Boolean) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        if (isCamera) {
                            dispatchTakePictureIntent()
                        } else {
                            dispatchGalleryIntent()
                        }
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .withErrorListener { error ->
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                    .show()
            }
            .onSameThread()
            .check()
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage(
            "This app needs permission to use this feature. You can grant them in app settings."
        )
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            try {
                mPhotoFile = mCompressor!!.compressToFile(mPhotoFile!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val filePat: String = mPhotoFile!!.absolutePath

            if (strImageType == "Before") {
                strBeforeImage = filePat
                binding.layoutImage1.visibility = View.GONE
                binding.image1.visibility = View.VISIBLE
                Glide.with(this)
                    .load(filePat)
                    .into(binding.image1)
            } else {
                strAfterImage = filePat
                binding.layoutImage2.visibility = View.GONE
                binding.image2.visibility = View.VISIBLE
                Glide.with(this)
                    .load(filePat)
                    .into(binding.image2)
            }
        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            val selectedImage = data!!.data
            try {
                mPhotoFile = mCompressor!!.compressToFile(
                    File(
                        Utils.getRealPathFromUri(
                            this,
                            selectedImage!!
                        )
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val filePat: String = mPhotoFile!!.absolutePath
            if (strImageType == "Before") {
                strBeforeImage = filePat
                binding.layoutImage1.visibility = View.GONE
                binding.image1.visibility = View.VISIBLE
                Glide.with(this)
                    .load(filePat)
                    .into(binding.image1)
            } else {
                strAfterImage = filePat
                binding.layoutImage2.visibility = View.GONE
                binding.image2.visibility = View.VISIBLE
                Glide.with(this)
                    .load(filePat)
                    .into(binding.image2)
            }
        }
    }
}