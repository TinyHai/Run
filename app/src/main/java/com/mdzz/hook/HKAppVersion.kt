package com.mdzz.hook

import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.StringBuilder

class HKAppVersion : BaseHook() {

    companion object {
        private const val TAG = "HKAppVersion"

        private val needBanForceUpdate: Boolean by lazy {
            XSharedPrefUtil.getBoolean(BAN_FORCE_UPDATE, default = false)
        }

    }

    override fun beginHook() {
        if (!needBanForceUpdate) {
            return
        }
        try {
            val appVersionClass = classLoader.loadClass(APPVERSION_CLASS)
            XposedHelpers.findAndHookMethod(appVersionClass, "getIsForse", IsForceMethodHook)
            log(TAG, "run: 模块7工作正常")
        } catch (th: Throwable) {
            log(TAG, "run: 模块7出错")
            log(TAG, th)
        }
    }

    private object IsForceMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            param?.let {
                try {
                    val appVersion = param.thisObject ?: return
                    log(TAG, "appVersion: ${toString(appVersion)}")
                    it.result = 0
                } catch (th: Throwable) {
                    log(TAG, th)
                }
            }
        }

        private fun toString(appVersion: Any): String {
            val appVersionClass = appVersion::class.java
            val declaredFields = appVersionClass.declaredFields
            val resultStringBuilder = StringBuilder()
            declaredFields.forEach {
                if (!it.isAccessible) {
                    it.isAccessible = true
                }
                resultStringBuilder.append(it.name)
                        .append(" = ")
                        .append(it.get(appVersion))
                        .append("; ")
            }
            return resultStringBuilder.toString()
        }
    }
}