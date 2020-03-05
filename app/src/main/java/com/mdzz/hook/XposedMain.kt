package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.IHookerDispatcher

class XposedMain : IHookerDispatcher {

    companion object {
        private const val TAG = "XposedMain"
    }

    override fun dispatch(lpparam: XC_LoadPackage.LoadPackageParam) {
        BaseHook.log(TAG,"Run 已加载")
        if (XSharedPrefUtil.getBoolean(HOOK_START)) {
            HKInstrumentation().getClassLoaderAndStartHook(lpparam)
        }
    }
}