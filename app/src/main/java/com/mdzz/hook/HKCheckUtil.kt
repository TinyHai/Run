package com.mdzz.hook

import android.text.TextUtils
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import kotlin.collections.ArrayList

class HKCheckUtil : BaseHook() {

    companion object {
        private const val TAG = "HKCheckUtil"

        private val checkUtilClassName: String
            get() = XSharedPrefUtil.getString(CHECK_UTIL_CLASS, "")!!.trim('\n', '\r', ' ', '\t')
    }

    override fun beginHook() {
        val lbClass = try {
            if (!TextUtils.isEmpty(checkUtilClassName)) {
                XposedHelpers.findClass(checkUtilClassName, classLoader)
            } else {
                log(TAG, "no class name be added")
                return
            }
        } catch (th: Throwable) {
            log(TAG, th)
            log(TAG, "run: 模块8出错")
            null
        }
        if (lbClass == null) {
            log(TAG, "no Class name is $checkUtilClassName")
            return
        }
        hookAllStaticMethodReturnZ(lbClass)
        log(TAG, "run: 模块8工作正常")
    }

    private fun hookAllStaticMethodReturnZ(lbClass: Class<*>) {
        val methods = getAllMethodReturnZ(lbClass)
        methods.forEach {
            XposedBridge.hookMethod(it, XC_MethodReplacement.returnConstant(false))
        }
    }

    private fun getAllMethodReturnZ(lbClass: Class<*>): List<Method> {
        val methods = ArrayList<Method>()
        val allDeclareStaticMethod = lbClass.declaredMethods
        allDeclareStaticMethod.forEach {
            log(TAG, it.toString())
            val isStatic = Modifier.isStatic(it.modifiers)
            if (isStatic && isMethodReturnZ(it)) {
                methods.add(it)
            }
        }
        return if (methods.isNotEmpty()) {
            methods
        } else {
            Collections.emptyList()
        }
    }

    private fun isMethodReturnZ(method: Method): Boolean {
        val resultType = method.returnType
        return resultType == Boolean::class.javaPrimitiveType
                || resultType == Boolean::class.java
    }
}