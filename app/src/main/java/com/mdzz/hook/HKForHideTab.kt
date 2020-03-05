package com.mdzz.hook

import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import com.mdzz.hook.base.BaseHook
import com.mdzz.hook.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.lang.reflect.Method

class HKForHideTab : BaseHook() {

    companion object {
        private const val TAG = "HKForHideTab"
    }

    override fun beginHook() {
        if (!XSharedPrefUtil.getBoolean(NEED_HIDE_TAB)) {
            return
        }
        try {
            val mainActivityClass =
                    classLoader.loadClass("com.zjwh.android_wh_physicalfitness.activity.MainActivity")
            XposedHelpers.findAndHookMethod(mainActivityClass, "onResume", MyMethodHook)
            log(TAG, "run: 模块3工作正常")
        } catch (e: ClassNotFoundException) {
            log(TAG, "run: 模块3出错")
            log(TAG, e)
        }
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            try {
                val activity = param.thisObject as Activity
                val resources = activity.resources
                hideAdImageView(resources, activity)
            } catch (th: Throwable) {
                log(TAG, th)
            }
        }

        private fun hideAdImageView(resources: Resources, activity: Activity) {
            val adImageId = resources.getIdentifier(AD_IMAGE_ID_NAME, "id", HOOK_PACKAGE)
            val adImage = activity.findViewById<View>(adImageId)
            if (adImage != null) {
                val adImageParent = adImage.parent as ViewGroup
                hideCenterTab(adImageParent)
                adImageParent.removeView(adImage)
            }
        }

        private fun hideCenterTab(parent: ViewGroup) {
            val tabLayout = parent.getChildAt(2)
            val tabLayoutClass = tabLayout::class.java
            val mTabs = getMTabsField(tabLayoutClass)
            val removeTabAt = getRemoveTabAt(tabLayoutClass)
            val tabs = mTabs.get(tabLayout) as ArrayList<*>
            if (tabs.size == 5) {
                removeTabAt.invoke(tabLayout, 2)
            }
        }

        private fun getRemoveTabAt(tabLayoutClass: Class<*>): Method {
            val removeTabAt = tabLayoutClass.getDeclaredMethod("removeTabAt", Int::class.javaPrimitiveType)
            if (!removeTabAt.isAccessible) {
                removeTabAt.isAccessible = true
            }
            return removeTabAt
        }

        private fun getMTabsField(tabLayoutClass: Class<*>): Field {
            val mTabs = tabLayoutClass.getDeclaredField("mTabs")
            if (!mTabs.isAccessible) {
                mTabs.isAccessible = true
            }
            return mTabs
        }
    }
}