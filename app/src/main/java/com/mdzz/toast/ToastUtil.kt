package com.mdzz.toast

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object {

        private var toast: Toast? = null

        fun makeToast(context: Context, msg: CharSequence, time: Int = Toast.LENGTH_SHORT) {
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