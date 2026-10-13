package com.engear.vansales.merchandiser

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.engear.vansales.BuildConfig
import com.engear.vansales.R
import com.engear.vansales.activities.InputOrderQtyDialogActivity
import com.engear.vansales.adapter.OrderRequestItemAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.databinding.ActivityAddPromotionalBinding
import com.engear.vansales.interfaces.OnItemClickListener
import com.engear.vansales.model.*
import com.engear.vansales.utils.FileCompressor
import com.engear.vansales.utils.Utils
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class AddPromotionalActivity : BaseActivity(), OnItemClickListener {

    lateinit var binding: ActivityAddPromotionalBinding
    private var countPlus = 0
    private val intentCode = 100
    private var strImagePath = ""
    private val TAKE_PHOTO_REQUEST = 1
    private val REQUEST_GALLERY_PHOTO = 2
    var mPhotoFile: File? = null
    var mCompressor: FileCompressor? = null
    var itemsList: ArrayList<ItemData.Data>? = ArrayList()
    var orderItemsList: ArrayList<ItemData.Data>? = ArrayList()
    var mAdapter: OrderRequestItemAdapter? = null
    var itemPosition = -1
    var invAmt = 0.0
    var mPromotionItem: PromotionalItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPromotionalBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
        setClickEvent()
    }

    private fun init() {
        setToolbarTitle("Add Promotional")

        mCompressor = FileCompressor(this)
        binding.stepView.setStepsNumber(3)
        binding.stepView.go(countPlus, true)

        itemsList = db!!.getAllItem()
        setUpRecycler()
    }

    private fun setClickEvent() {
        binding.btnNext.setOnClickListener {
            if (countPlus == 2) {
                if (binding.edtInvoiceNo.text.toString().isEmpty()) {
                    Utils.displayAlert(me, "Please insert invoice number!")
                } else if (strImagePath.equals("", ignoreCase = true)) {
                    Utils.displayAlert(me, "Please upload invoice receipt!")
                } else {
                    addPromotionalData()
                }
            } else {
                if (countPlus == 1) {
                    if (binding.edtCustomerNmae.text.toString().isEmpty()) {
                        Utils.displayAlert(me, "Please enter customer name!")
                    } else if (binding.edtPhone.text.toString().isEmpty()) {
                        Utils.displayAlert(me, "Please enter customer phone number!")
                    } else if (binding.edtLocation.text.toString().isEmpty()) {
                        Utils.displayAlert(me, "Please enter customer location!")
                    } else {
                        countPlus++
                        binding.stepView.go(countPlus, true)
                        updateData(countPlus)
                    }
                } else {

                    orderItemsList!!.clear()
                    invAmt = 0.0
                    for (i in itemsList!!) {
                        if (i.itemSelectUOM != null) {
                            if (i.itemSelectUOM!!.isNotEmpty()) {
                                orderItemsList!!.add(i)

                                for (j in i.itemSelectUOM) {
                                    invAmt += j!!.totalAmt.toFloat()
                                }
                            }
                        }
                    }

                    if (invAmt > 0) {

                        mPromotionItem = db!!.getPromotionalItemDetail("" + invAmt)

                        if (mPromotionItem != null) {
                            if (mPromotionItem!!.itemName != null && !mPromotionItem!!.itemName
                                    .equals("")
                            ) {
                                showDerDialog(me, mPromotionItem!!.itemName!!)
                            } else {
                                showDerDialog(me, "")
                            }

                        } else {
                            showDerDialog(me, "")
                        }
                    } else {
                        Toast.makeText(this, "Please add item", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        binding.btnBack.setOnClickListener {
            if (countPlus != 0) {
                countPlus--
                binding.stepView.go(countPlus, true)
                updateData(countPlus)
            }
        }

        binding.layoutImage.setOnClickListener {
            selectImage()
        }

    }

    private fun setUpRecycler() {
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = OrderRequestItemAdapter(this, itemsList!!,"")
        binding.rvList.adapter = mAdapter
    }

    private fun openDialogActivity(mQty: String, mUom: String) {
        startActivityForResult(
            Intent(this, InputOrderQtyDialogActivity::class.java).apply {
                this.putExtra("item_code", itemsList!![itemPosition].itemCode)
                this.putExtra("item_id", itemsList!![itemPosition].id.toString())
                this.putExtra("cust_id", "")
                this.putExtra(
                    "uom_list",
                    Gson().toJson(itemsList!![itemPosition].itemSelectUOM)
                )
                this.putExtra(
                    "available_qty",
                    mQty
                )
                this.putExtra(
                    "available_uom",
                    mUom
                )
                this.putExtra("type", "load")
            },
            intentCode
        )
    }

    private fun updateData(position: Int) {
        when (position) {
            0 -> {
                binding.lytDetailView.visibility = View.GONE
                binding.lytItemView.visibility = View.VISIBLE
                binding.lytInvoiceView.visibility = View.GONE
            }
            1 -> {
                binding.lytDetailView.visibility = View.VISIBLE
                binding.lytItemView.visibility = View.GONE
                binding.btnNext.setImageResource(R.drawable.ic_arrow_right)
            }
            2 -> {
                binding.lytDetailView.visibility = View.GONE
                binding.lytItemView.visibility = View.GONE
                binding.lytInvoiceView.visibility = View.VISIBLE
                binding.btnNext.setImageResource(R.drawable.ic_check_white)
            }
        }
        if (position == 0) {
            binding.btnBack.visibility = View.GONE
        } else {
            binding.btnBack.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            try {
                mPhotoFile = mCompressor!!.compressToFile(mPhotoFile!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val filePat: String = mPhotoFile!!.absolutePath
            binding.layoutImage.visibility = View.GONE
            binding.layoutPager.visibility = View.VISIBLE
            strImagePath = filePat.substring(filePat.lastIndexOf("/") + 1)
            binding.ivImage.setImageURI(Uri.parse(filePat))
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
            binding.layoutImage.visibility = View.GONE
            binding.layoutPager.visibility = View.VISIBLE
            strImagePath = filePat.substring(filePat.lastIndexOf("/") + 1)
            binding.ivImage.setImageURI(Uri.parse(filePat))
        } else if (requestCode == intentCode) {
            if (resultCode == Activity.RESULT_OK) {

                itemsList!![itemPosition].itemSelectUOM = emptyList()

                val strObj = data!!.getStringExtra("uomList")
                val listOfUomType: Type =
                    object : TypeToken<List<UomData?>?>() {}.type

                var mList =
                    Gson().fromJson(strObj, listOfUomType) as ArrayList<UomData>

                itemsList!![itemPosition].itemSelectUOM = mList

                binding.rvList.adapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private fun selectImage() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(this@AddPromotionalActivity)
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

    override fun onItemsClick(position: Int, tag: String) {
        if (tag == "dialog") {
            itemPosition = position
            openQuantityDialog()
        }
    }

    private fun openQuantityDialog() {
        if (itemsList!![itemPosition].itemUOM == null)
            itemsList!![itemPosition].itemUOM = ArrayList()

        openDialogActivity(mQty = "0", mUom = "")

    }

    private fun addPromotionalData() {

        /*var mPromotionItem = PromotionalItem()
        mPromotionItem = db!!.getPromotionalItemDetail("" + invAmt)

        if (mPromotionItem != null) {
            if (mPromotionItem.itemName != null && !mPromotionItem.itemName
                    .equals("")
            ) {
                showDerDialog(me, mPromotionItem.itemName!!)
            } else {
                showDerDialog(me, "")
            }
        } else {
            showDerDialog(me, "")
        }*/

        var mPromoId = Utils.getPromotionaNo(mPreferenceManager!!.getUserId()!!)
        val mPromotion = Promotional()
        mPromotion.promotionId = mPromoId
        mPromotion.promotionAcountId = mPromotionItem!!.promotionId!!
        mPromotion.customerName = binding.edtCustomerNmae.text.toString()
        mPromotion.customerPhone = binding.edtPhone.text.toString()
        mPromotion.amount = invAmt.toString()
        mPromotion.freeItem = mPromotionItem!!.itemName!!
        mPromotion.invoiceNo = binding.edtInvoiceNo.text.toString()
        mPromotion.invoiceImage = strImagePath
        db!!.insertPromotionPost(mPromotion)

        val transaction = Transaction()
        transaction.tr_type = AppConstants.TRANSACTION_TYPES.TT_PRAMOTION
        transaction.tr_date_time = Utils.getCurrentDate() + " " + Utils.getCurrentTime()
        transaction.tr_customer_num = ""
        transaction.tr_customer_name = ""
        transaction.tr_salesman_id = mPreferenceManager!!.getUserId()
        transaction.tr_invoice_id = ""
        transaction.tr_order_id = mPromoId
        transaction.tr_collection_id = ""
        transaction.tr_pyament_id = ""
        transaction.tr_printData = ""
        transaction.tr_is_posted = "No"
        db!!.insertTransaction(transaction)
    }

    fun showDerDialog(activity: Activity, itemName: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_gift)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.getAttributes()!!.windowAnimations = R.style.DialogAnimation
        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.95).toInt()
        dialog.window!!.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        val txtTitle = dialog.findViewById(R.id.tvTitle) as TextView
        val btnDismiss = dialog.findViewById(R.id.btnDismiss) as TextView
        if (!itemName.equals("", ignoreCase = true)) {
            txtTitle.text = "Congratulation you are won $itemName!"
        } else {
            txtTitle.text = "Sorry You are not eligable for any promotion. Better luck Next Time!"
        }

        btnDismiss.setOnClickListener {
            dialog.dismiss()
            if (!itemName.equals("", ignoreCase = true)) {
                countPlus++
                binding.stepView.go(countPlus, true)
                updateData(countPlus)
            } else {
                finish()
            }
        }

        dialog.show()
    }

}