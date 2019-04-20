package com.mdzz.run

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (lpparam.packageName == "com.mdzz.hook") {
            check(lpparam)
            return
        }

        if (lpparam.packageName == "com.zjwh.android_wh_physicalfitness"
                && lpparam.processName == "com.zjwh.android_wh_physicalfitness") {
            XposedBridge.log("run: begin")
            HKThrowable().handleLoadPackage()
            HKBufferedReader().handleLoadPackage()
            HKPackageManager().handleLoadPackage(lpparam)
            HKFile().handleLoadPackage()
            HKClass().handleLoadPackage()
        }
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