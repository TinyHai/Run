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
            val clazz = lpparam.classLoader.loadClass("android.app.Instrumentation")
            XposedHelpers.findAndHookMethod(clazz, "newApplication",
                    "java.lang.ClassLoader", "java.lang.String",
                    "android.content.Context", MyMethodHook)
        } catch (th: Throwable) {
            BaseHook.log(TAG, th)
        }
    }

    object MyMethodHook : XC_MethodHook() {

        override fun afterHookedMethod(param: MethodHookParam) {
            if (param.args[1] == HOOK_APPLICATION) {
                val classLoader = param.args[0] as ClassLoader
                startHookByClassLoader(classLoader)
            }
        }

        private fun startHookByClassLoader(classLoader: ClassLoader) {
            BaseHook.log(TAG, "run: working!")
            BaseHook.classLoader = classLoader
            ArrayList<BaseHook>().apply {
                add(HKApplication())
                add(HKThrowable())
                add(HKPackageManager())
                add(HKForHideTab())
                add(HKDialog())
                add(HKMethod())
                add(HKTool())
                add(HKFileReader())
                add(HKAppVersion())
                add(HKSPEditor())
                forEach {
                    it.beginHook()
                }
            }.clear()
        }
    }
}