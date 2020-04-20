package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKMethod : BaseHook() {
    companion object {
        private const val TAG = "HKMethod"
    }

    override fun beginHook() {
        try {
            val modifierClass = classLoader.loadClass(MODIFIER_CLASS)
            XposedHelpers.findAndHookMethod(modifierClass, "isNative",
                    Int::class.javaPrimitiveType, MyMethodHook)
            log(TAG, "run: 模块4工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块4出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块4出错")
            log(TAG, th)
        }
    }


    object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, "isNative be false")
            param.result = false
        }
    }
}