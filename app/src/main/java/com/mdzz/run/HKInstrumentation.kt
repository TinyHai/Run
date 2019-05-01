package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HKInstrumentation {

    companion object {
        private const val TAG = "HKInstrumentation"
    }

    fun getClassLoaderAndStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = try {
                lpparam.classLoader.loadClass("android.app.Instrumentation")
            } catch (th: Throwable) {
                BaseHook.log(TAG, th)
                null
            }
            XposedHelpers.findAndHookMethod(clazz, "newApplication",
                    "java.lang.ClassLoader", "java.lang.String",
                    "android.content.Context", MyMethodHook)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }

    object MyMethodHook : XC_MethodHook() {

        override fun afterHookedMethod(param: MethodHookParam) {
            if (param.args[1] == "com.zjwh.android_wh_physicalfitness.application.MyApplication") {
                val classLoader = param.args[0] as ClassLoader
                startHookByClassLoader(classLoader)
            }
        }

        private fun startHookByClassLoader(classLoader: ClassLoader) {
            BaseHook.log(TAG, "run: working!")
            BaseHook.classLoader = classLoader
            ArrayList<BaseHook>().apply {
                add(HKStackTraceElement())
                add(HKPackageManager())
                add(HKFile())
                add(HKForHideTab())
                add(HKDialog())
                add(HKMethod())
                forEach {
                    it.beginHook()
                }
            }.clear()
        }
    }
}