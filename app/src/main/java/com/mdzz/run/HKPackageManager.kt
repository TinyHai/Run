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
        val appPackageManagerClass = try {
            XposedHelpers.findClass("android.app.ApplicationPackageManager",
                    classLoader)
        } catch (th: Throwable) {
            log(TAG, th)
            null
        }
        appPackageManagerClass?.let {
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

        private val protectedPackageNames = XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")

        override fun afterHookedMethod(param: MethodHookParam) {
            protectedPackageNames.forEach {
                log(TAG, it)
            }
            if (param.args[0] == HOOK_PACKAGE) {
                return
            }
            if (needProtected(param.args[0].toString())) {
                param.throwable = PackageManager.NameNotFoundException("nmsl")
                return
            }
            if (hasResult(param)) {
                when (param.result::class.java.simpleName) {
                    "ApplicationInfo" -> {
                        val applicationInfo = param.result as ApplicationInfo
                        if (Filter.isSystemApp(applicationInfo.flags)) {
                            logSystemAppPackageName(param.args[0].toString())
                            return
                        } else {
                            param.throwable = PackageManager.NameNotFoundException("nmsl")
                        }
                    }
                    "PackageInfo" -> {
                        val packageInfo = param.result as PackageInfo
                        if (Filter.isSystemApp(packageInfo.applicationInfo.flags)) {
                            logSystemAppPackageName(param.args[0].toString())
                            return
                        } else {
                            param.throwable = PackageManager.NameNotFoundException("nmsl")
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        private fun logSystemAppPackageName(packageName: String) {
            log(TAG, packageName)
        }

        private fun hasResult(param: MethodHookParam): Boolean {
            return param.throwable == null
        }

        private fun needProtected(packageName: String) = packageName in protectedPackageNames
                || packageName == "de.robv.android.xposed.installer"
    }
}