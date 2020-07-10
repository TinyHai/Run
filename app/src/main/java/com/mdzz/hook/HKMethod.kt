package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method

class HKMethod : BaseHook() {
    companion object {
        private const val TAG = "HKMethod"
    }

    override fun beginHook() {
        try {
            val methodClass = classLoader.loadClass(METHOD_CLASS)
            XposedHelpers.findAndHookMethod(methodClass, "getModifiers", MyMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }


    private object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            if ((param.thisObject as Method).name == "getDeviceId") {
                param.result = 0x0
                log(TAG, "native method modifier be replace")
            }
        }
    }
}