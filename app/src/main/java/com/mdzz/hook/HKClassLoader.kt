package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Method

/**
 * author: tiny
 * created on: 20-5-5 上午11:43
 */
class HKClassLoader : BaseHook() {

    companion object {
        private const val TAG = "HKClassLoader"

        private const val XPOSED_PACKAGE_PREFIX = "de.robv.android.xposed"
    }

    override fun beginHook() {
        try {
            val classLoaderClass = classLoader::class.java
            val loadClassMethod = findMethod(classLoaderClass, "loadClass", String::class.java)
            XposedBridge.hookMethod(loadClassMethod, MyMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }

    private fun findMethod(clazz: Class<*>?, methodName: String, vararg parameterTypes: Class<*>): Method? {
        if (clazz == null) {
            return null
        }
        return try {
            clazz.getDeclaredMethod(methodName, *parameterTypes)
        } catch (ignore: NoSuchMethodException) {
            findMethod(clazz.superclass, methodName, *parameterTypes)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                if (it.hasThrowable()) {
                    return
                }
                val result = it.result as? Class<*>
                result?.let { clazz ->
                    if (clazz.name.startsWith(XPOSED_PACKAGE_PREFIX)) {
                        it.throwable = ClassNotFoundException(it.args!![0].toString())
                    }
                }
            }
        }
    }
}