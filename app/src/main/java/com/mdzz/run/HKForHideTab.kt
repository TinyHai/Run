package com.mdzz.run

import android.view.ViewGroup
import android.widget.ImageView
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field

class HKForHideTab : BaseHook() {

    companion object {
        private const val TAG = "HKForHideTab"
    }

    override fun beginHook() {
        if (!XSharedPrefUtil.getBoolean(NEED_HIDE_TAB)) {
            return
        }
        var clazz: Class<*>? = null
        try {
            clazz = classLoader.loadClass("com.zjwh.android_wh_physicalfitness.activity.MainActivity")
        } catch (e: ClassNotFoundException) {
            log(TAG, e)
        }
        XposedHelpers.findAndHookMethod(clazz, "onResume", MyMethodHook)

        log(TAG, "run: 模块4工作正常")
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                var field: Field? = null
                try {
                    field = param.thisObject::class.java.getDeclaredField("O0000oO")
                } catch (e: NoSuchFieldException) {
                    param.thisObject::class.java.declaredFields.forEach {
                        if (it.type.simpleName == "TabLayout") {
                            field = it
                            return@forEach
                        }
                    }
                }
                field?.let {
                    it.isAccessible = true
                    log(TAG, it.get(param.thisObject)::class.java.name)
                    val tabLayout = it.get(param.thisObject)
                    val getTabCount = tabLayout::class.java.getMethod("getTabCount")
                    val count = getTabCount.invoke(tabLayout) as Int
                    if (count < 5) {
                        return
                    }
                    val removeTabAt = tabLayout::class.java.getMethod("removeTabAt", Int::class.javaPrimitiveType);
                    removeTabAt.invoke(tabLayout, 2)
                    val getParent = tabLayout::class.java.getMethod("getParent")
                    val parentView = getParent.invoke(tabLayout) as ViewGroup
                    val view = parentView.getChildAt(3)
                    if (view is ImageView) {
                        parentView.removeView(view)
                    }
                }
            } catch (th: Throwable) {
                log(TAG, th)
            }
        }
    }
}