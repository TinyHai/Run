package com.mdzz.run

import android.view.View
import android.view.ViewGroup
import com.mdzz.run.util.FileUtil.saveInfo
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

// 未使用到的类
class HKWindowManager {

    fun handleLoadPackage() {
        val clazz = XposedHelpers.findClass("android.view.WindowManagerImpl",
                XposedBridge.BOOTCLASSLOADER)
        XposedHelpers.findAndHookMethod(clazz, "addView", View::class.java,
                ViewGroup.LayoutParams::class.java, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                saveInfo("view: ", param.args[0]::class.java.name)
            }
        })
        XposedBridge.log("run: 模块6工作正常")
    }
}