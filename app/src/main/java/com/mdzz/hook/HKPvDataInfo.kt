package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * author: tiny
 * created on: 20-5-5 下午3:50
 */
class HKPvDataInfo : BaseHook() {

    companion object {
        private const val TAG = "HKPvDataInfo"

        private const val FAKE_INFO = "run_credit_click"

        private const val FAKE_VALUE = 200_000

        private const val REAL_VALUE = 90_000
    }

    override fun beginHook() {
        try {
            val pvDataInfoClass = classLoader.loadClass(PV_DATAINFO_CLASS)
            XposedHelpers.findAndHookConstructor(pvDataInfoClass, Int::class.javaPrimitiveType,
                    String::class.java, MyMethodHook)
            log(TAG, "run: 模块9工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块9出错")
            log(TAG, th)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let {
                log(TAG, "value = ${it.args[0]}, info = ${it.args[1]}")
                val value = it.args[0] as Int
                if (value == REAL_VALUE) {
                    it.args[0] = FAKE_VALUE
                    it.args[1] = FAKE_INFO
                }
            }
        }
    }
}