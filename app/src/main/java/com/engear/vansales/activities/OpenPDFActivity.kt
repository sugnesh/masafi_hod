package com.engear.vansales.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOpenPDFBinding
import com.engear.vansales.utils.Utils

class OpenPDFActivity : BaseActivity() {

    lateinit var binding: ActivityOpenPDFBinding
    private var mUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenPDFBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@OpenPDFActivity,
            "On View manual Screen "
        )
        setToolbarTitle("View Manual")

        showOnlyProgressDialog()

        binding.webView.clearCache(true);
        binding.webView.clearHistory();
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.javaScriptEnabled = true
        mUrl = "https://docs.google.com/gview?embedded=true&url=http://139.162.41.99/Manual.pdf"
        binding.webView.loadUrl(mUrl)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                dismissDialog()
            }
        }

    }
}