package com.mdzz.run

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.io.BufferedReader

class HKBufferedReader {
    fun handleLoadPackage() {
        XposedHelpers.findAndHookMethod(BufferedReader::class.java, "readLine",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (param.result == null) {
                            return
                        }
                        if (param.result.toString().trim().contains("XposedBridge")
                            || param.result.toString().trim().startsWith("package:")) {
                            param.result = null
                        }
                    }
                })
        XposedBridge.log("run: 模块2工作正常")
    }
}