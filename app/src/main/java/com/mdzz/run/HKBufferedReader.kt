package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.BufferedReader

// 已弃用
class HKBufferedReader : BaseHook() {

    companion object {
        private const val TAG = "HKBufferedReader"
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod(BufferedReader::class.java, "readLine",
                MyMethodHook)
        log(TAG, "run: 模块2工作正常")
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            if (param.result == null) {
                return
            }
            if (param.result.toString().trim().contains("XposedBridge")) {
                param.result = "run"
            }
        }
    }
}