package com.mdzz.run

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class HKClass {
    fun handleLoadPackage() {
        XposedHelpers.findAndHookMethod(ClassLoader::class.java, "loadClass",
                String::class.java, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        if (param?.args!![0].toString().startsWith("de.robv.android.xposed")) {
                            param.args[0] = "de.robv.android.xposed.MDZZ"
                        }
                    }
                })
        XposedBridge.log("run: 模块5工作正常")
    }
}