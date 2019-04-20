package com.mdzz.run

import android.os.Environment
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.io.File

class HKFile {

    fun handleLoadPackage() {
        XposedBridge.hookAllMethods(File::class.java, "list", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val file = param.thisObject as File
                if (file.absolutePath.startsWith("/data/data/com.zjwh.android_wh_physicalfitness")) {
                    return
                }
                val obj = param.thisObject
                when (obj) {
                    is File -> {
                        with(obj) {
                            if (parent == Environment.getExternalStorageDirectory().path
                                    + "/Android") {
                                param.result = arrayOf("com.zjwh.android_wh_physicalfitness")
                            }
                        }
                    }
                }
            }
        })

        XposedBridge.log("run: 模块4工作正常")
    }
}