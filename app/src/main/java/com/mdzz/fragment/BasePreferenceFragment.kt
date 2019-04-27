package com.mdzz.fragment

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import com.mdzz.activity.MainActivity
import java.lang.ref.WeakReference

abstract class BasePreferenceFragment(activity: MainActivity?) : PreferenceFragment() {

    private val mActivity = WeakReference(activity)

    constructor(): this(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(getXmlId())
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        when (preference.key) {
            "about_run" -> {
                mActivity.get()?.setupFragment(MainActivity.ABOUT_FRAGMENT)
                return true
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    abstract fun getXmlId(): Int
}