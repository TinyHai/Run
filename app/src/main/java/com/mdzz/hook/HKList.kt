package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKList : BaseHook() {
    companion object {
        private const val TAG = "HKList"
    }

    override fun beginHook() {
        try {
            val mockAppEvtClass = classLoader.loadClass(MOCKAPPEVT_CLASS)
            XposedHelpers.findAndHookConstructor(mockAppEvtClass, List::class.java, MyMethodHook)
            log(TAG, "run: 模块${number}工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, e)
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
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