package com.mdzz.run

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.mdzz.hook") {
            check(lpparam)
            return
        }
        if (lpparam.packageName != "com.zjwh.android_wh_physicalfitness") {
            return
        }
        XposedBridge.log("run: begin")
        HKThrowable().handleLoadPackage(lpparam)
        HKBufferedReader().handleLoadPackage(lpparam)
        HKPackageManager().handleLoadPackage(lpparam)
        HKActivityManager().handleLoadPackage(lpparam)
        HKFile().handleLoadPackage(lpparam)
    }

    fun check(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod("com.mdzz.activity.MainActivity", lpparam.classLoader,
                "isActive", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.result = true
            }
        })
    }
}