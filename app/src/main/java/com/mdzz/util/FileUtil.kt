package com.mdzz.util

import android.content.Context
import android.support.v4.content.ContextCompat
import java.io.File

object FileUtil {

    fun getDataFile(context: Context) = ContextCompat.getDataDir(context)!!

    fun getPrefDirFile(context: Context) = File(getDataFile(context), "shared_prefs")

    fun getPrefFile(context: Context) = File(getPrefDirFile(context), context.packageName + "_preferences.xml")
}