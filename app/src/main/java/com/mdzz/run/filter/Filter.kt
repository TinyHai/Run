package com.mdzz.run.filter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.mdzz.run.HOOK_PACKAGE


sealed class Filter<T> {
    protected val result = ArrayList<T>()

    abstract fun filter(list: List<T>): List<T>

    fun get(list: List<T>) = if (result.isEmpty()) filter(list) else result

    companion object {
        fun isSystemApp(flags: Int) = (flags and ApplicationInfo.FLAG_SYSTEM) > 0
    }
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
                }
            }
        }
    }
}