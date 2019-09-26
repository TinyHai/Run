package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKList : BaseHook() {
    companion object {
        private const val TAG = "HKList"
    }

    override fun beginHook() {
        log(TAG, "run: 模块6工作正常")
        val mockAppEvtClass = try {
            classLoader.loadClass(MOCKAPPEVT_CLASS)
        } catch (e: ClassNotFoundException) {
            log(TAG, e)
            null
        }
        XposedHelpers.findAndHookConstructor(mockAppEvtClass, List::class.java, MyMethodHook)
    }

    object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let {
                try {
                    val mockList = it.args[0] as MutableList<*>
                    mockList.forEach { any ->
                        printIt(any!!)
                    }
                    param.result = null
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }

        private fun printIt(any: Any) {
            val anyClass = any::class.java
            val fields = anyClass.declaredFields
            log(TAG, "begin any")
            fields.forEach {
                if (!it.isAccessible) {
                    it.isAccessible = true
                }
                log(TAG, "${it.name} = ${it.get(any)}")
            }
            log(TAG, "end any")
        }
    }
}