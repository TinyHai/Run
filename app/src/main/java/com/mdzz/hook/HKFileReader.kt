package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader

class HKFileReader : BaseHook() {
    companion object {
        private const val TAG = "HKFileReader"
    }

    override fun beginHook() {
        try {
            XposedHelpers.findAndHookConstructor(FileReader::class.java, String::class.java, MyMethodHook)
            log(TAG, "run: 模块6工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块6出错")
            log(TAG, th)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let {
                val fileName = it.args[0].toString()
                if (fileName.matches(Regex("/proc/[0-9]+/maps"))) {
                    log(TAG, fileName)
                    it.throwable = FileNotFoundException("Invalid file path")
                }
            }
        }
    }
}