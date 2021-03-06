package com.mdzz.hook

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.proxy.EditorProxy
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKSPEditor : BaseHook() {

    companion object {
        private const val TAG = "HKSharedPref"
    }

    @SuppressLint("PrivateApi")
    override fun beginHook() {
        if (BuildConfig.DEBUG.not()) {
            return
        }
        try {
            val sharedPrefClass = XposedHelpers.findClass(SHARED_PREF_CLASS, classLoader)
            XposedHelpers.findAndHookMethod(sharedPrefClass, "edit", EditMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }

    private object EditMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                param.result = EditorProxy(param.result as SharedPreferences.Editor)
            }
        }
    }
}