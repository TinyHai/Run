package com.mdzz.run

import com.mdzz.run.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class HKList : BaseHook() {
    companion object {
        const val TAG = "HKList"
        const val PACKAGE_REGEX = "^[a-zA-Z][a-zA-Z_]*\\.([a-zA-Z_]*\\.)*[a-zA-Z_]+$"
        private val weakList = LinkedHashSet<List<*>>()
    }

    override fun beginHook() {
        XposedHelpers.findAndHookMethod(ArrayList::class.java, "get",
                Int::class.javaPrimitiveType, MyMethodHook)
//        XposedHelpers.findAndHookMethod(ArrayList::class.java, "contains",
//                Object::class.java, MyMethodHook)
        log(TAG, "run: 模块6工作正常")
    }

    object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                val result = it.result
                if (result !is String) {
                    return
                }
                val thisObject = it.thisObject as MutableList<*>

                if (!existList(thisObject)) {
                    weakList.add(thisObject)
                }

                var needClear = false
                for (packageName in thisObject) {
                    val name = packageName.toString()
                    log(TAG, name)
                    if (name.startsWith("android.") || name.contains(HOOK_PACKAGE)) {
                        continue
                    }
                    if (name.matches(Regex(PACKAGE_REGEX))) {
                        needClear = true
                    }
                }
                if (needClear) {
                    val iterator = thisObject.iterator()
                    while (iterator.hasNext()) {
                        val packageName = iterator.next().toString()
                        if (packageName.startsWith("android.") || packageName.contains(HOOK_PACKAGE)) {
                            continue
                        }
                        if (!packageName.endsWith("com") && packageName.matches(Regex(PACKAGE_REGEX))) {
                            log(TAG, "remove: $packageName")
                            iterator.remove()
                        }
                    }
                }
            }
        }

        private fun existList(list: List<*>): Boolean {
            weakList.forEach {
                if (list === it) {
                    return true
                }
            }
            return false
        }
    }
}