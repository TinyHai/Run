package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import kotlin.collections.ArrayList

class HKLBClass : BaseHook() {

    companion object {
        private const val TAG = "HKLBClass"
    }

    override fun beginHook() {
        val lbClass = try {
            XposedHelpers.findClass(LB_CLASS, classLoader)
        } catch (th: Throwable) {
            log(TAG, th)
            null
        }
        if (lbClass == null) {
            log(TAG, "no Class name is $LB_CLASS")
            return
        }
        hookAllStaticMethodReturnZ(lbClass)
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