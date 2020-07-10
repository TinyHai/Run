package com.mdzz.hook.filter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.ResolveInfo
import com.mdzz.hook.HOOK_PACKAGE
import com.mdzz.hook.NEED_PROTECT_PACKAGE
import com.mdzz.hook.util.XSharedPrefUtil


sealed class Filter<T> {

    companion object {
        val PROTECT_PACKAGES = XSharedPrefUtil.getStringSet(NEED_PROTECT_PACKAGE, delimiter = "\n")

        fun isSystemApp(flags: Int) = (flags and ApplicationInfo.FLAG_SYSTEM) > 0
    }

    protected val result = ArrayList<T>()

    abstract fun filter(list: List<T>): List<T>

    fun get(list: List<T>) = if (result.isEmpty()) filter(list) else result
}

object PackageInfoFilter : Filter<PackageInfo>() {

    override fun filter(list: List<PackageInfo>) = result.apply {
        if (isEmpty()) {
            for (info in list) {
                if (info.packageName == HOOK_PACKAGE) {
                    add(info)
                }
                if (isSystemApp(info.applicationInfo.flags)) {
                    if (info.packageName == "de.robv.android.xposed.installer") {
                        continue
                    }
                    add(info)
                } else if (info.packageName !in PROTECT_PACKAGES) {
                    add(info)
                }
            }
        }
    }
}

object ApplicationInfoFilter : Filter<ApplicationInfo>() {

    override fun filter(list: List<ApplicationInfo>) = result.apply {
        if (isEmpty()) {
            for (info in list) {
                if (info.packageName == HOOK_PACKAGE) {
                    add(info)
                }
                if (isSystemApp(info.flags)) {
                    if (info.packageName == "de.robv.android.xposed.installer") {
                        continue
                    }
                    add(info)
                } else if (info.packageName !in PROTECT_PACKAGES) {
                    add(info)
                }
            }
        }
    }
}

object ResolveInfoFilter : Filter<ResolveInfo>() {
    override fun filter(list: List<ResolveInfo>) = result.apply {
        if (isEmpty()) {
            for (info in list) {
                val activityInfo = info.activityInfo
                if (activityInfo.packageName == HOOK_PACKAGE) {
                    add(info)
                }
                if (isSystemApp(activityInfo.applicationInfo.flags)) {
                    if (activityInfo.packageName == "de.robv.android.xposed.installer") {
                        continue
                    }
                    add(info)
                } else if (activityInfo.packageName !in PROTECT_PACKAGES) {
                    add(info)
                }
            }
        }
    }
}