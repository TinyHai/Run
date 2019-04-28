package com.mdzz.run

import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.mdzz.hook") {
            check(lpparam.classLoader)
            XposedBridge.log("Run begin")
            XposedBridge.log(XSharedPrefUtil.getBoolean(HOOK_START).toString())
            XposedBridge.log(XSharedPrefUtil.getBoolean(NEED_HIDE_TAB).toString())
            XposedBridge.log(XSharedPrefUtil.getBoolean(LOG_SWICH).toString())
            return
        }
        if (lpparam.packageName == HOOK_PACKAGE) {
            XposedBridge.log(lpparam.packageName + " " + lpparam.processName)
            if (XSharedPrefUtil.getBoolean(HOOK_START)) {
                HKInstrumentation().getClassLoaderAndStartHook(lpparam)
            }
        }
    }

    private fun check(classLoader: ClassLoader) {
        XposedHelpers.findAndHookMethod("com.mdzz.activity.MainActivity",
                classLoader, "isActive", XC_MethodReplacement.returnConstant(true))
    }
}