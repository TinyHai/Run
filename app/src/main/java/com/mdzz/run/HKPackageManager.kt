package com.mdzz.run

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Binder
import android.support.annotation.BinderThread
import android.view.View
import com.mdzz.filter.ApplicationInfoFilter
import com.mdzz.filter.PackageInfoFilter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HKPackageManager {

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val clazz = XposedHelpers.findClass("android.app.ApplicationPackageManager",
                lpparam.classLoader)

        clazz?.let {

            XposedHelpers.findAndHookMethod(it, "getPackageInfo",
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            XposedBridge.log("GPI: " + param.args[0].toString())
                            if (param.throwable != null) {
                                return
                            }
                            if (isSystemApp(param.result as ApplicationInfo)
                                    || param.args[0] == "com.zjwh.android_wh_physicalfitness") {
                                return
                            } else {
                                param.throwable = PackageManager.NameNotFoundException(param.args[0].toString())
                            }
                        }
                    })

            XposedHelpers.findAndHookMethod(it, "getApplicationInfo",
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
//                            XposedBridge.log("run: applicationinfo ${param.args[0]}")
                            XposedBridge.log("GAI: " + param.args[0].toString())
                            if (param.throwable != null) {
                                return
                            }
                            if (isSystemApp(param.result as ApplicationInfo)
                                    || param.args[0] == "com.zjwh.android_wh_physicalfitness") {
                                return
                            } else {
                                param.throwable = PackageManager.NameNotFoundException(param.args[0].toString())
                            }
                        }
                    })

            XposedBridge.hookAllMethods(it, "getInstalledApplications",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val list = param.result as List<ApplicationInfo>
                            param.result = with(aInstance) {
                                if (this == null) {
                                    aInstance = ApplicationInfoFilter()
                                    aInstance?.filter(list)
                                } else {
                                    aInstance?.filter(list)
                                }
                            }
                        }
                    })

            XposedBridge.hookAllMethods(it, "getInstalledPackages",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val list = param.result as List<PackageInfo>
                            param.result = with(pInstance) {
                                if (this == null) {
                                    pInstance = PackageInfoFilter()
                                    pInstance?.filter(list)
                                } else {
                                    pInstance?.filter(list)
                                }
                            }
                        }
                    })

        }

        XposedBridge.log("run: 模块3工作正常")
    }

    private companion object {
        var pInstance: PackageInfoFilter? = null
        var aInstance: ApplicationInfoFilter? = null
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo)
        = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0
}