package com.mdzz.run

import android.content.Context
import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class HKTool : BaseHook() {
    companion object {
        private const val TAG = "HKTool"
    }

    override fun beginHook() {
        val nativeToolClass = try {
            classLoader.loadClass(NATIVETOOL_CLASS)
        } catch (e: ClassNotFoundException) {
            log(TAG, e)
            null
        }
        XposedHelpers.findAndHookMethod(nativeToolClass, "startCheckMockLocation",
                "android.content.Context", "com.ijm.drisk.mockinspect.MockProcess",
                Int::class.javaPrimitiveType, MyMethodHook)
        log(TAG, "run: 模块6工作正常")
    }

    object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let {
                val mockProcess = it.args[1]
                if (mockProcess != null) {
                    hookMockProcess(mockProcess)
                    return
                }
                log(TAG, "mockProcess == null")
            }
        }

        private fun hookMockProcess(mockProcess: Any) {
            val mockProcessClass = mockProcess::class.java
            val processMethod = try {
                mockProcessClass.getDeclaredMethod("process", List::class.java)
            } catch (e: NoSuchMethodException) {
                log(TAG, e)
                null
            }
            log(TAG, "method: ${processMethod?.name ?: "null"}")
            XposedBridge.hookMethod(processMethod, ProcessMethodHook)
        }
    }

    object ProcessMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let {
                try {
                    val list = it.args[0] as List<*>
                    log(TAG, "begin")
                    list.forEach {
                        log(TAG, it.toString())
                    }
                    log(TAG, "end")
                    it.result = null
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }
    }
}