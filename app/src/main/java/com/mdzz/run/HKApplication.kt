package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HKApplication {

    companion object {
        private const val TAG = "HKApplication"
    }

    fun getClassLoaderAndStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = try {
                lpparam.classLoader.loadClass("s.h.e.l.l.S")
            } catch (th: Throwable) {
                lpparam.classLoader.loadClass("com.zjwh.android_wh_physicalfitness.application.MyApplication")
            }
            XposedHelpers.findAndHookMethod(clazz, "onCreate", MyMethodHook)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }

    object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val context = param.thisObject
            val getClassLoader = context::class.java.getMethod("getClassLoader")
            val classLoader = getClassLoader.invoke(context) as ClassLoader
            startHookByClassLoader(classLoader)
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
                forEach {
                    it.beginHook()
                }
            }.clear()
        }
    }
}