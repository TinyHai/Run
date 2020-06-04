package com.mdzz.hook

import android.content.SharedPreferences
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.proxy.SPProxy
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKContextWrapper : BaseHook() {

    companion object {
        private const val TAG = "HKContextWrapper"

        private const val SP_NAME = "share_date"
    }

    override fun beginHook() {
        try {
            val contextWrapperClass = XposedHelpers.findClass(CONTEXT_WRAPPER_CLASS, classLoader)
            XposedHelpers.findAndHookMethod(contextWrapperClass, "getSharedPreferences",
                    String::class.java, Int::class.javaPrimitiveType, MyMethodHook)
            log(TAG, "run: 模块10运行正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块10出错")
            log(TAG, th)
        }
    }

    object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                if (SP_NAME == it.args[0]) {
                    it.result = SPProxy(it.result as SharedPreferences)
                }
            }
        }
    }
}