package com.mdzz.run

import android.app.ActivityManager
import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HKActivityManager {
    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod("android.app.ActivityManager", lpparam.classLoader,
                "getRunningAppProcesses",
                object : XC_MethodHook(){
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val list = param.result as List<ActivityManager.RunningAppProcessInfo>
                        val result = ArrayList<ActivityManager.RunningAppProcessInfo>()
                        for (info in list) {
                            if (info.processName != "com.zjwh.android_wh_physicalfitness") {
                                continue
                            }
                            result.add(info)
                        }
                        param.result = result
                    }
                })
        XposedBridge.log("run: 模块4工作正常")
    }
}