package com.mdzz.run

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.mdzz.run.base.BaseHook
import com.mdzz.run.filter.ApplicationInfoFilter
import com.mdzz.run.filter.Filter
import com.mdzz.run.filter.PackageInfoFilter
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

@Suppress("UNCHECKED_CAST")
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
            XposedHelpers.findAndHookMethod(it, "getInstalledApplications",
                    Int::class.javaPrimitiveType, MyIAppMethodHook)
            XposedHelpers.findAndHookMethod(it, "getInstalledPackages",
                    Int::class.javaPrimitiveType, MyIPkgMethodHook)
        }

        log(TAG, "run: 模块2工作正常")
    }

    object MyIAppMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val applicationInfos = param.result as List<ApplicationInfo>
            param.result = ApplicationInfoFilter.get(applicationInfos)
        }
    }

    object MyIPkgMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val packageInfos = param.result as List<PackageInfo>
            param.result = PackageInfoFilter.get(packageInfos)
        }
    }

    object MyMethodHook : XC_MethodHook() {

        private val stringSet = XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")

        override fun afterHookedMethod(param: MethodHookParam) {
            log(TAG, "stringSet.size = ${stringSet.size}")
            stringSet.forEach {
                log(TAG, it)
            }
            log(TAG, param.args[0].toString())
            if (param.args[0] == HOOK_PACKAGE) {
                return
            }
            if (param.args[0] in stringSet || param.args[0] == "de.robv.android.xposed.installer") {
                param.throwable = PackageManager.NameNotFoundException("nmsl")
                return
            }
            when (param.result::class.java.simpleName) {
                "ApplicationInfo" -> {
                    val applicationInfo = param.result as ApplicationInfo
                    if (Filter.isSystemApp(applicationInfo.flags)) {
                        return
                    } else {
                        param.throwable = PackageManager.NameNotFoundException("nmsl")
                    }
                }
                "PackageInfo" -> {
                    val packageInfo = param.result as PackageInfo
                    if (Filter.isSystemApp(packageInfo.applicationInfo.flags)) {
                        return
                    } else {
                        param.throwable = PackageManager.NameNotFoundException("nmsl")
                    }
                }
                else -> {}
            }
        }
    }
}