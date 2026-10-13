package com.engear.vansales.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.common.AppConstants
import com.engear.vansales.common.MyApplication
import com.engear.vansales.databinding.ActivityInvoiceStampBinding
import com.engear.vansales.model.*
import com.engear.vansales.restAPI.ApiClient
import com.engear.vansales.utils.Utils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream


class StampUploadActivity : BaseActivity() {

    lateinit var binding: ActivityInvoiceStampBinding
    var orderNo: String? = null
    var itemType: String? = null

    private val pickImage = 100
    private val CAMERA = 200
    private var imageUri: Uri? = null
    var encodedImage: String? = null


    companion object {
        var isInvoiceDone = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceStampBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        Utils.logData(
            this@StampUploadActivity,
            "On Invoice Screen "
        )
        init()
    }

    private fun init() {
        setToolbarTitle("Stamp Upload")

        itemType = intent.getStringExtra("type")
        orderNo = intent.getStringExtra("order_no")

        binding.tvInvoiceNumber.text = "Invoice Number: " + orderNo

        binding.btnStampImage.setOnClickListener {
            showPictureDialog()
        }

        binding.btnUpload.setOnClickListener {
            getStampUpload()
        }


    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.imgStamp.setImageURI(imageUri)
            val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            encodedImage = encodeImage(selectedImage)

            //data:image/jpeg;base64,
            Log.d("check string", encodedImage.toString())
            // val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            binding.imgStamp.setImageBitmap(thumbnail)
            val encodedImage = encodeImage(thumbnail)
            Log.d("check string", encodedImage.toString())
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    private fun getStampUpload() {

        showOnlyProgressDialog()
        encodedImage = "data:image/jpeg;base64," + encodedImage

        val jObj = JsonObject()
        jObj.addProperty("invoice_id", orderNo!!.toInt())
        jObj.addProperty("stamp", 1)
        jObj.addProperty("stamp_image",encodedImage)
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
                                this@StampUploadActivity,
                                "Stamp Response  ${response.body()!!.toString()}"
                            )
                            Log.e("Stamp Body", response.body()!!.toString())

                            if (response.body()!!["status"].asBoolean) {

//                                var jData = response.body()!!["data"].asJsonObject
                                finish()

                            }
                        }
                    } else {
                        // Fail to Post
                        Utils.logData(
                            this@StampUploadActivity,
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
                            this@StampUploadActivity,
                            "PosStamp Response Fail${error.message.toString()}"
                        )
                    } else {
                        Utils.logData(
                            this@StampUploadActivity,
                            "Stamp Response Fail"
                        )

                    }
                }
            })
    }


}