package com.mdzz.filter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import de.robv.android.xposed.XposedBridge


sealed class Filter<T>() {
    protected val result = ArrayList<T>()

    abstract fun filter(list: List<T>): List<T>
}

object PackageInfoFilter : Filter<PackageInfo>() {

    override fun filter(list: List<PackageInfo>) = with(result) {
        if (isEmpty()) {
            for (info in list) {
                if ((info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) > 0) {
                    add(info)
                }
            }
            this
        } else {
            this
        }
    }
}

object ApplicationInfoFilter : Filter<ApplicationInfo>() {

    override fun filter(list: List<ApplicationInfo>) = with(result) {
        if (isEmpty()) {
            for (info in list) {
                if ((info.flags and ApplicationInfo.FLAG_SYSTEM) > 0) {
                    add(info)
                }
            }
            this
        } else {
            this
        }
    }
}