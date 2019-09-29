package com.mdzz.run

import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.LogUtil
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "XposedInit"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            hookSelf(lpparam.classLoader)
            return
        }
        if (lpparam.packageName == HOOK_PACKAGE) {
            XposedBridge.log("Run 已加载")
            if (XSharedPrefUtil.getBoolean(HOOK_START)) {
                HKInstrumentation().getClassLoaderAndStartHook(lpparam)
            }
        }
    }

    private fun hookSelf(classLoader: ClassLoader) {
        try {
            val mainActivityClass = classLoader.loadClass("com.mdzz.activity.MainActivity")
            XposedHelpers.findAndHookMethod(mainActivityClass, "isActive",
                    XC_MethodReplacement.returnConstant(true))
            val xpTag= LogUtil.XPTAG
            XposedHelpers.setStaticObjectField(mainActivityClass, "XPTAG", xpTag)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }
}