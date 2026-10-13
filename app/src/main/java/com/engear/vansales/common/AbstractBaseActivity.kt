package com.engear.vansales.common

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.engear.vansales.R
import com.engear.vansales.databinding.CustomAlertDialogBinding
import com.engear.vansales.databinding.DialogSimpleBinding
import com.engear.vansales.interfaces.OnConfirmationDialogClick
import com.engear.vansales.interfaces.OnDialog
import com.engear.vansales.model.Label
import com.engear.vansales.utils.Utils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

abstract class AbstractBaseActivity : AppCompatActivity() {
    private var redirectClass: Class<*>? = null
    private var mDialog: Dialog? = null

    /**
     *
     * <item name="android:windowContentTransitions" tools:targetApi="lollipop">true</item>
     */
    fun startActivity(viewStart: View, transactionName: String, intent: Intent) {
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(this, viewStart, transactionName)
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    override fun onResume() {
        super.onResume()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(base))
    }

    fun getRedirectClass(): Class<*>? {
        return redirectClass
    }

    fun setRedirectClass(redirectClass: Class<*>) {
        this.redirectClass = redirectClass
    }

    fun showSnackBar(context: Context, msg: String) {
        Utils.hideSoftKeyboard(this)
        val snackbar =
            Snackbar.make(
                window.decorView.rootView.findViewById(R.id.frame_container),
                msg,
                Snackbar.LENGTH_LONG
            )
        val tv =
            snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed))
        snackbar.show()
    }

    fun showOnlyProgressDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                return
            }
            mDialog = Dialog(this)
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setCancelable(false)
            mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            mDialog!!.setContentView(R.layout.custom_only_progress_dialog)
            mDialog!!.window!!.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dismissDialog() {
        try {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showConfirmAlertDialog(
        message: String,
        OnConfirmationDialogClick: OnConfirmationDialogClick
    ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            CustomAlertDialogBinding.inflate(LayoutInflater.from(baseContext))
        dialog.setContentView(dialogBinding.root)
        val displayMetrics = DisplayMetrics()

        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val layoutParams = WindowManager.LayoutParams()
        dialogBinding.message = Label().getLabel(message)
        dialogBinding.executePendingBindings()

        dialogBinding.btnOk.setOnClickListener {
            OnConfirmationDialogClick.onOk()
            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            OnConfirmationDialogClick.onCancel()
            dialog.dismiss()
        }
        layoutParams.copyFrom(dialog.window!!.attributes)
        val dialogWindowWidth = (height * 0.5f).toInt()
        val dialogWindowHeight = (width * 0.5f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.gravity = Gravity.CENTER
        dialog.window!!.attributes = layoutParams
        dialog.show()
    }

    fun showSimpleDialog(msg: String, title: String, onDialog: OnDialog?) {
        val dialog = Dialog(this@AbstractBaseActivity)//, R.style.customDialog
        val dialogSimpleBinding = DialogSimpleBinding.inflate(layoutInflater)
        dialogSimpleBinding.msg = Label().getLabel(msg)
        dialogSimpleBinding.title = Label().getLabel(title)
        dialogSimpleBinding.executePendingBindings()
        dialogSimpleBinding.btnOk.setOnClickListener {
            dialog.dismiss()
            onDialog?.onOk()
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogSimpleBinding.root)
        dialog.setCancelable(false)
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val windowManager = getWindow().attributes
        windowManager.dimAmount = 0.2f
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.show()
    }

}
