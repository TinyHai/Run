package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKApplication : BaseHook() {

    companion object {
        private const val TAG = "HKApplication"
    }

    override fun beginHook() {
        try {
            val myApplicationClass = XposedHelpers.findClass(HOOK_APPLICATION, classLoader)
            XposedHelpers.findAndHookMethod(myApplicationClass, "onCreate", OnCreateMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (e: XposedHelpers.ClassNotFoundError) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }

    private object OnCreateMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            Thread.currentThread().setUncaughtExceptionHandler { th, ex ->
                log(TAG, th.name)
                log(TAG, ex)
            }
            log(TAG, "replace exception handler")
        }
    }
}