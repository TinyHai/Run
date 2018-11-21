package com.mdzz.log

import de.robv.android.xposed.XposedBridge

const val status = false

fun log(arg: Any) {

    if (status) {
        when (arg) {
            is String -> {
                XposedBridge.log(arg)
            }

            is Throwable -> {
                XposedBridge.log(arg)
            }

            else -> {
                throw ClassFormatError("${arg.javaClass.name} is not String or Throwable")
            }
        }
    }
}