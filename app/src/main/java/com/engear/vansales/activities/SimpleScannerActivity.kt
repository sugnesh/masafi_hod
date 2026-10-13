package com.engear.vansales.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.engear.vansales.utils.Utils
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class SimpleScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var mScannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.logData(
            this@SimpleScannerActivity,
            "On scanner Screen "
        )
        mScannerView = ZXingScannerView(this) // Programmatically initialize the scanner view
        setContentView(mScannerView) // Set the scanner
    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        Log.e("Scan", rawResult.toString()) // Prints scan results
        Log.e(
            "Scan", rawResult.barcodeFormat.toString()
        ) // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:
        val resultIntent = Intent()
        resultIntent.putExtra("scanid", rawResult.text)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        mScannerView!!.resumeCameraPreview(this)
    }
}