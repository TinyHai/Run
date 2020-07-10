package com.mdzz.hook

import android.os.Build
import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class HKMockLocation : BaseHook() {

    companion object {
        private const val TAG = "HKMockLocation"
        private val mockMethodNames = arrayOf(
                "getMockGpsStrategy",
                "isMockGps"
        )
    }

    override fun beginHook() {
        try {
            val locationClass = classLoader.loadClass(LOCATION_CLASS)
            var success = false
            val thList = ArrayList<Throwable>(mockMethodNames.size)
            mockMethodNames.forEach {
                try {
                    XposedHelpers.findAndHookMethod(locationClass, it, XC_MethodReplacement.returnConstant(0))
                    success = true
                } catch (th: Throwable) {
                    thList += th
                }
            }
            if (success.not()) {
                throw Throwable().apply {
                    thList.forEach {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            addSuppressed(it)
                        } else {
                            log(TAG, it)
                        }
                    }
                }
            }
            log(TAG, "run: 模块${number}工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块${number}出错")
            log(TAG, th)
        }
    }
}