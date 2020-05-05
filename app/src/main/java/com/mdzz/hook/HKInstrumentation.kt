package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HKInstrumentation {

    companion object {
        private const val TAG = "HKInstrumentation"
    }

    fun getClassLoaderAndStartHook(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val clazz = lpparam.classLoader.loadClass("android.app.Instrumentation")
            XposedHelpers.findAndHookMethod(clazz, "newApplication",
                    "java.lang.ClassLoader", "java.lang.String",
                    "android.content.Context", MyMethodHook)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }

    private object MyMethodHook : XC_MethodHook() {

        override fun beforeHookedMethod(param: MethodHookParam) {
            if (param.args[1] == HOOK_APPLICATION) {
                val classLoader = param.args[0] as ClassLoader
                startHookWithClassLoader(classLoader)
            }
        }

        private fun startHookWithClassLoader(classLoader: ClassLoader) {
            BaseHook.log(TAG, "run: working!")
            BaseHook.classLoader = classLoader
            ArrayList<BaseHook>().apply {
                add(HKApplication())
                add(HKThrowable())
                add(HKPackageManager())
                add(HKDialog())
                add(HKMethod())
                add(HKTool())
                add(HKFileReader())
                add(HKAppVersion())
                add(HKSPEditor())
                add(HKCheckUtil())
                add(HKPvDataInfo())
                add(HKClassLoader())
                forEach {
                    it.beginHook()
                }
            }.clear()
        }
    }
}