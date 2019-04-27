package com.mdzz.run.util

import com.mdzz.run.BuildConfig
import de.robv.android.xposed.XSharedPreferences
import java.lang.ref.WeakReference

object XSharedPrefUtil {

    private var mXSharedPref: WeakReference<XSharedPreferences>
            = WeakReference(XSharedPreferences(BuildConfig.APPLICATION_ID))

    private fun getXSharedPref(): XSharedPreferences {
        if (mXSharedPref.get() == null) {
            val xsf = XSharedPreferences(BuildConfig.APPLICATION_ID)
            mXSharedPref = WeakReference(xsf)
        }
        val pref = mXSharedPref.get()
        pref?.reload()
        return pref!!
    }

    fun getStringSet(key: String, default: Set<String> = setOf(), delimiter: String = " ")
            = getString(key, "").split(delimiter).toSet()

    fun getString(key: String, default: String = "") = getXSharedPref().getString(key, default)

    fun getBoolean(key: String, default: Boolean = false) = getXSharedPref().getBoolean(key, default)
}