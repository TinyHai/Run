package com.mdzz.toast

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object {

        var toast: Toast? = null

        fun makeToast(context: Context, msg: String, time: Int = Toast.LENGTH_SHORT) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, time)
                toast?.show()
            } else {
                toast?.let {
                    it.setText(msg)
                    it.show()
                }
            }
        }
    }
}