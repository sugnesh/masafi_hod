package com.engear.vansales.merchandiser

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.engear.vansales.BuildConfig
import com.engear.vansales.R
import com.engear.vansales.adapter.ComplaintPagerAdaptor
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityAssetDetailBinding
import com.engear.vansales.model.Assets
import com.engear.vansales.model.CustomerData
import com.engear.vansales.model.Transaction
import com.engear.vansales.utils.FileCompressor
import com.engear.vansales.utils.Utils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AssetDetailActivity : BaseActivity() {

    lateinit var binding: ActivityAssetDetailBinding
    var customerCode: String? = null
    private var assetsId: String? = null
    private var mAssets: Assets? = null
    private var customerObj: CustomerData.DataCustomer? = null
    var mPhotoFile: File? = null
    var mCompressor: FileCompressor? = null
    var arrImage = ArrayList<String>()
    private val TAKE_PHOTO_REQUEST = 1
    private val REQUEST_GALLERY_PHOTO = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetDetailBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {


        customerCode = intent.getStringExtra("customer_code")
        assetsId = intent.getStringExtra("assets_id")
        customerObj = db!!.getCustomerDetail(customerCode!!)

        mAssets = db!!.getAssetsDetail(customerObj!!.userDetail.id.toString(), assetsId!!)
        setToolbarTitle(mAssets!!.name!!)

        mCompressor = FileCompressor(this)
        setData()

        if (db!!.checkAssetsCustomerPosted(
                customerObj!!.userDetail.id.toString(),
                assetsId!!
            )
        ) {
            binding.btnSubmit.visibility = View.GONE
        } else {
            binding.btnSubmit.visibility = View.VISIBLE
        }
    }

    private fun setData() {
        if (mAssets!!.image != null) {
            Glide.with(me).load(mAssets!!.image)
                .into(binding.ivAssetsImage)
        }

        if (mAssets!!.feedBack!!.isNotEmpty()) {
            binding.edtFeedback.setText(mAssets!!.feedBack!!)
        }

        if (mAssets!!.postImages!!.isNotEmpty()) {
            var splitImage = mAssets!!.postImages!!.split(",")
            for (mPath in splitImage) {
                arrImage.add(mPath)
            }

            val viewPagerAdapter = ComplaintPagerAdaptor(me, arrImage)
            binding.viewPager.adapter = viewPagerAdapter
            binding.dotsIndicators.setViewPager(binding.viewPager)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_assets, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                startActivity(Intent(me, AssetSurveyActivity::class.java).apply {
                    this.putExtra("customer_code", customerCode)
                    this.putExtra("assetId", assetsId)
                    this.putExtra("name", mAssets!!.name)
                })
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    private fun setClickEvent() {

        binding.btnAdd.setOnClickListener {
            if (arrImage.size < 4) {
                selectImage()
            } else {
                Toast.makeText(
                    applicationContext,
                    "You can upload max 4 images",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSubmit.setOnClickListener {
            val alertDialogBuilder =
                android.app.AlertDialog.Builder(this@AssetDetailActivity)
            alertDialogBuilder.setTitle("Alert")
                .setMessage("Are you sure you want to submit assets?")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    addAssets()
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.dismiss() }

            alertDialogBuilder.show()
        }

    }

    private fun addAssets() {

        var mComplaint = Assets()
        mComplaint.uniqueId = Utils.getAssetsNo(mPreferenceManager!!.getUserId()!!)
        mComplaint.assetsId = assetsId
        mComplaint.customer = customerObj!!.userDetail.id.toString()
        mComplaint.feedBack = binding.edtFeedback.text.toString()
        var strImages = ""
        if (arrImage.size > 0) {
            for (mData in arrImage) {
                if (strImages.isEmpty()) {
                    strImages = mData
                } else {
                    strImages = "$strImages,$mData"
                }
            }
        }
        mComplaint.postImages = strImages
        db!!.insertAssetsPost(mComplaint)

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_ASSETS
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = customerCode
        transaction.tr_customer_name =
            customerObj!!.userDetail!!.firstname + " " + customerObj!!.userDetail!!.lastname
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = mComplaint.uniqueId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)

        db!!.updateCustomerTransaction(customerObj!!.userDetail!!.id.toString(), "merchandising")

        finish()
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
            binding.viewPager.visibility = View.VISIBLE
            arrImage.add(filePat)
            val viewPagerAdapter = ComplaintPagerAdaptor(me, arrImage)
            binding.viewPager.adapter = viewPagerAdapter
            binding.dotsIndicators.setViewPager(binding.viewPager)
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
            binding.viewPager.visibility = View.VISIBLE
            arrImage.add(filePat)
            val viewPagerAdapter = ComplaintPagerAdaptor(me, arrImage)
            binding.viewPager.adapter = viewPagerAdapter
            binding.dotsIndicators.setViewPager(binding.viewPager)
        }
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
        val builder = AlertDialog.Builder(this@AssetDetailActivity)
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
}