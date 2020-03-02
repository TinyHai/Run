package com.mdzz.toast

import android.widget.Toast
import com.mdzz.MyApplication

object ToastUtil {

    private var toast: Toast? = null

    fun makeToast(msg: CharSequence, time: Int = Toast.LENGTH_SHORT) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.application, msg, time)
            toast?.show()
        } else {
            toast?.let {
                it.setText(msg)
                it.show()
            }
        }
    }
}