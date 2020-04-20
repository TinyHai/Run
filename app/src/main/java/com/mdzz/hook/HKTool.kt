package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

class HKTool : BaseHook() {
    companion object {
        private const val TAG = "HKTool"
    }

    override fun beginHook() {
        try {
            val nativeToolClass = classLoader.loadClass(NATIVETOOL_CLASS)
            XposedHelpers.findAndHookMethod(nativeToolClass, "startCheckMockLocation",
                    "android.content.Context", "com.ijm.drisk.mockinspect.MockProcess",
                    Int::class.javaPrimitiveType, MyMethodHook)
            log(TAG, "run: 模块6工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块5出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块5出错")
            log(TAG, th)
        }
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
            log(TAG, "mockProcessClass = ${mockProcessClass.name}")
            val processMethod = try {
                mockProcessClass.getDeclaredMethod("process", List::class.java)
            } catch (e: NoSuchMethodException) {
                log(TAG, e)
                null
            }
            XposedBridge.hookMethod(processMethod, ProcessMethodHook)
        }
    }

    object ProcessMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let { p ->
                try {
                    val list = p.args[0] as List<*>
                    log(TAG, "begin")
                    list.forEach {
                        log(TAG, it.toString())
                    }
                    log(TAG, "end")
                    p.result = null
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }
    }
}