package com.mdzz.hook

import android.content.Context
import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class HKTool : BaseHook() {
    companion object {
        private const val TAG = "HKTool"

        private const val SPECIAL_XP_CLASS = "de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam"
    }

    override fun beginHook() {
        try {
            val nativeToolClass = classLoader.loadClass(NATIVETOOL_CLASS)
            nativeToolClass.declaredMethods.filter { it.name.startsWith("set").not() }.forEach {
                XposedBridge.hookMethod(it, CheckMethodHook)
            }
            XposedHelpers.findAndHookMethod(nativeToolClass, "startCheckMockLocation",
                    "android.content.Context", "com.ijm.drisk.mockinspect.MockProcess",
                    Int::class.javaPrimitiveType, MyMethodHook)
            XposedHelpers.findAndHookMethod(nativeToolClass, "hasXposedExist",
                    Context::class.java, XpCheckMethodHook)
            log(TAG, "run: 模块5工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块5出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块5出错")
            log(TAG, th)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
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

    private object ProcessMethodHook : XC_MethodHook() {
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

    private object XpCheckMethodHook : XC_MethodHook() {

        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                val result = it.result as? Boolean ?: return
                val context = it.args[0] as? Context
                context?.let { c ->
                    val classLoader = c.classLoader
                    val isSame = classLoader == BaseHook.classLoader
                    log(TAG, "isSame = $isSame")
                    log(TAG, "classLoader load XpClass ${tryGet(false) { classLoader.loadClass(SPECIAL_XP_CLASS) != null }}")
                }
                if (result) {
                    log(TAG, "XpCheck return true")
                    it.result = false
                }
            }
        }

        @Suppress("SameParameterValue")
        private fun <T> tryGet(default: T, block: () -> T): T {
            return try {
                block()
            } catch (th: Throwable) {
                log(TAG, th)
                default
            }
        }
    }

    private object CheckMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                val method = it.method as Method
                log(TAG, method.toString())
                log(TAG, "parameterValues = ${it.args?.contentToString()}")
                log(TAG, "returnValue = ${it.resultOrThrowable}")
                if (isReturnZ(method)) {
                    it.result = false
                }
                if (it.args[0] == 1 && isReturnI(method)) {
                    it.result = 0
                }
            }
        }

        private fun isReturnZ(method: Method): Boolean {
            return when (method.returnType) {
                Boolean::class.java, Boolean::class.javaPrimitiveType -> {
                    true
                }
                else -> false
            }
        }

        private fun isReturnI(method: Method): Boolean {
            return when (method.returnType) {
                Int::class.java, Int::class.javaPrimitiveType -> true
                else -> false
            }
        }
    }
}