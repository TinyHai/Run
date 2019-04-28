package com.mdzz.run.filter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


sealed class Filter<T> {
    protected val result = ArrayList<T>()

    abstract fun filter(list: List<T>): List<T>

    fun get(list: List<T>) = if (result.isEmpty()) filter(list) else result
}

object PackageInfoFilter : Filter<PackageInfo>() {

    override fun filter(list: List<PackageInfo>) = result.apply {
        if (isEmpty()) {
            for (info in list) {
                if ((info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0) {
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
                if ((info.flags and ApplicationInfo.FLAG_SYSTEM) > 0) {
                    if (info.packageName == "de.robv.android.xposed.installer") {
                        continue
                    }
                    add(info)
                }
            }
        }
    }
}