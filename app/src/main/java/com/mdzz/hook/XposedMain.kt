package com.mdzz.hook

import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.IHookerDispatcher

class XposedMain : IHookerDispatcher {

    companion object {
        private const val TAG = "XposedMain"
    }

    override fun dispatch(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            hookSelf(lpparam.classLoader)
            return
        }
        if (lpparam.packageName == HOOK_PACKAGE) {
            XposedBridge.log("Run 已重新加载")
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
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }
}