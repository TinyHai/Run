package com.mdzz.hook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.filter.ApplicationInfoFilter
import com.mdzz.hook.filter.Filter
import com.mdzz.hook.filter.PackageInfoFilter
import com.mdzz.hook.filter.ResolveInfoFilter
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

@Suppress("UNCHECKED_CAST")
class HKPackageManager : BaseHook() {

    companion object {
        private const val TAG = "HKPackageManager"
    }

    @SuppressLint("PrivateApi")
    override fun beginHook() {
        try {
            val appPackageManagerClass = classLoader.loadClass(PACKAGEMANAGER_CLASS)
            XposedHelpers.findAndHookMethod(appPackageManagerClass, "getPackageInfo",
                    String::class.java, Int::class.javaPrimitiveType, MyMethodHook)
            XposedHelpers.findAndHookMethod(appPackageManagerClass, "getApplicationInfo",
                    String::class.java, Int::class.javaPrimitiveType, MyMethodHook)
            XposedHelpers.findAndHookMethod(appPackageManagerClass, "queryIntentActivities",
                    Intent::class.java, Int::class.javaPrimitiveType, MyRInfoMethodHook)
            XposedHelpers.findAndHookMethod(appPackageManagerClass, "getInstalledApplications",
                    Int::class.javaPrimitiveType, MyIAppMethodHook)
            XposedHelpers.findAndHookMethod(appPackageManagerClass, "getInstalledPackages",
                    Int::class.javaPrimitiveType, MyIPkgMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }

    private object MyIAppMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val applicationInfos = param.result as List<ApplicationInfo>
            param.result = ApplicationInfoFilter.get(applicationInfos)
        }
    }

    private object MyIPkgMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val packageInfos = param.result as List<PackageInfo>
            param.result = PackageInfoFilter.get(packageInfos)
        }
    }

    private object MyRInfoMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val resolveInfo = param.result as List<ResolveInfo>
            param.result = ResolveInfoFilter.get(resolveInfo)
        }
    }

    private object MyMethodHook : XC_MethodHook() {

        private val protectedPackageNames
                get() = XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")

        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                try {
                    val packageName = it.args[0] as String
                    if (packageName.startsWith("android.") || packageName.contains(HOOK_PACKAGE)) {
                        return
                    }

                    var shouldThrow = false
                    if (hasResult(it)) {
                        when (it.result::class.java.simpleName) {
                            "ApplicationInfo" -> {
                                val applicationInfo = param.result as ApplicationInfo
                                if (Filter.isSystemApp(applicationInfo.flags)) {
                                    return
                                } else if (needProtected(packageName)) {
                                    shouldThrow = true
                                }
                            }
                            "PackageInfo" -> {
                                val packageInfo = it.result as PackageInfo
                                if (Filter.isSystemApp(packageInfo.applicationInfo.flags)) {
                                    return
                                } else if (needProtected(packageName)) {
                                    shouldThrow = true
                                }
                            }
                        }
                    }
                    if (shouldThrow) {
                        log(TAG, packageName)
                        it.throwable = PackageManager.NameNotFoundException(packageName)
                    }
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }

        private fun hasResult(param: MethodHookParam): Boolean {
            return param.throwable == null
        }

        private fun needProtected(packageName: String) = packageName in protectedPackageNames
                || packageName == "de.robv.android.xposed.installer"
    }
}