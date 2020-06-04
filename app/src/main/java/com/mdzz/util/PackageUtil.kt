package com.mdzz.util

import android.content.Context
import android.content.pm.PackageManager

object PackageUtil {
    fun hasInstalledIt(context: Context, pkgName: String) = try {
        context.packageManager.getPackageInfo(pkgName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}