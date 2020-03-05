package com.mdzz.hook

import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.LogUtil
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.HotXposed

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
            HotXposed.hook(XposedMain::class.java, BuildConfig.APPLICATION_ID, lpparam)
        }
    }

    private fun hookSelf(classLoader: ClassLoader) {
        try {
            val mainActivityClass = classLoader.loadClass("com.mdzz.activity.MainActivity")
            XposedHelpers.findAndHookMethod(mainActivityClass, "isActive",
                    XC_MethodReplacement.returnConstant(true))
            XposedHelpers.setStaticObjectField(mainActivityClass,"XPTAG", LogUtil.XPTAG)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }
}