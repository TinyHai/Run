package com.mdzz.run

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
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
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (param.args[0].toString() != "com.zjwh.android_wh_physicalfitness") {
                                param.throwable = PackageManager.NameNotFoundException()
                            }
                        }
                    })

            XposedHelpers.findAndHookMethod(it, "getApplicationInfo",
                    String::class.java,
                    Int::class.javaPrimitiveType,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
//                            XposedBridge.log("run: applicationinfo ${param.args[0]}")
                            if (param.args[0].toString() != "com.zjwh.android_wh_physicalfitness") {
                                param.throwable = PackageManager.NameNotFoundException()
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
}