package com.mdzz.log

import de.robv.android.xposed.XposedBridge
import java.lang.IllegalArgumentException

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
                throw IllegalArgumentException("${arg.javaClass.name} is not String or Throwable")
            }
        }
    }
}