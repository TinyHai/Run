package com.mdzz.run

import android.content.pm.PackageManager
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKPackageManager : BaseHook() {

    companion object {
        private const val TAG = "HKPackageManager"
    }

    override fun beginHook() {
        val clazz = try {
            XposedHelpers.findClass("android.app.ApplicationPackageManager",
                    classLoader)
        } catch (th: Throwable) {
            log(TAG, th)
            null
        }
        clazz?.let {
            XposedHelpers.findAndHookMethod(it, "getPackageInfo",
                    String::class.java, Int::class.javaPrimitiveType, MyMethodHook)

            XposedHelpers.findAndHookMethod(it, "getApplicationInfo",
                    String::class.java, Int::class.javaPrimitiveType, MyMethodHook)
        }

        log(TAG, "run: 模块2工作正常")
    }

    object MyMethodHook : XC_MethodHook() {

        private val stringSet = XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")

        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, "stringSet.size = ${stringSet.size}")
            stringSet.forEach {
                log(TAG, it)
            }
            if (param.args[0] in stringSet || param.args[0] == "de.robv.android.xposed.installer") {
                param.throwable = PackageManager.NameNotFoundException("nmsl")
            }
        }
    }
}