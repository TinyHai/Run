package com.mdzz.run

import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKMockConfig : BaseHook() {

    companion object {
        private const val TAG = "HKMockConfig"
    }

    override fun beginHook() {
        val mockConfigClass = try {
            classLoader.loadClass(MOCKCONFIG_CLASS)
        } catch (e: ClassNotFoundException) {
            log(TAG, e)
            null
        }
        XposedHelpers.findAndHookMethod(mockConfigClass, "getMockList", MyMethodHook)
        log(TAG, "run: 模块8工作正常")
    }

    object MyMethodHook : XC_MethodHook() {

        private val protectedPkg: Set<String> by lazy {
            XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")
        }

        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                try {
                    val mockList = it.result as List<*>
                    val resultList = ArrayList<String>()
                    log(TAG, "mockList")
                    mockList.forEach { pkg ->
                        val name = pkg.toString()
                        log(TAG, name)
                        if (name !in protectedPkg) {
                            resultList.add(name)
                        }
                    }
                    param.result = resultList
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }
    }
}