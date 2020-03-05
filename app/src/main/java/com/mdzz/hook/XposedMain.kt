package com.mdzz.hook

import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
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
        if (lpparam.packageName == "org.meowcat.edxposed.manager") {
            val notificationUtilClass = try {
                lpparam.classLoader.loadClass(
                        "de.robv.android.xposed.installer.util.NotificationUtil")
            } catch (th: Throwable) {
                BaseHook.log(TAG, th)
                null
            } ?: return

            XposedHelpers.findAndHookMethod(
                    notificationUtilClass,
                    "showModulesUpdatedNotification",
                    object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            BaseHook.log(TAG, "hook edxposed manager notification success")
                            param.result = null
                        }

                        @Throws(Throwable::class)
                        override fun afterHookedMethod(param: MethodHookParam) {
                            super.afterHookedMethod(param)
                        }
                    })
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
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }
}