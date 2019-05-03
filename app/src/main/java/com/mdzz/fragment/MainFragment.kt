package com.mdzz.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceScreen
import android.text.TextUtils
import com.mdzz.activity.MainActivity
import com.mdzz.run.R
import com.mdzz.toast.ToastUtil
import com.mdzz.util.PackageUtil

@SuppressLint("ValidFragment")
class MainFragment(activity: MainActivity?) : BasePreferenceFragment(activity) {

    private var isActive: Boolean = false

    constructor(): this(null)

    override fun getXmlId() = R.xml.main_frag_preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isActive = it.getBoolean(IS_ACTIVE, false)
        }
        initPreference()
    }

    private fun initPreference() {
        findPreference("status").apply {
            summary = if (isActive) getString(R.string.is_active) else getString(R.string.no_active)
        }
    }

    override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen, preference: Preference): Boolean {
        when (preference.key) {
            "status" -> {
                openXposedInstaller()
                return true
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }

    private fun openXposedInstaller() {

        val xpManagerPackageNames = arrayOf("com.solohsu.android.edxp.manager", "de.robv.android.xposed.installer")

        var installedPackageName = ""

        xpManagerPackageNames.forEach {
            if (PackageUtil.hasInstalledIt(activity, it)) {
                installedPackageName = it
                return@forEach
            }
        }

        if (installedPackageName != "") {
            val intent = Intent()
            intent.let {
                it.component = ComponentName(installedPackageName,
                        "de.robv.android.xposed.installer.WelcomeActivity")
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("fragment", 1)
                startActivity(it)
            }
        } else {
            ToastUtil.makeToast(activity, "请先安装XposedInstaller后重试")
        }
    }

    companion object {
        private const val TAG = "MainFragment"

        const val IS_ACTIVE = "is_active"

        fun newInstance(activity: MainActivity? = null, args: Bundle? = null)
                = MainFragment(activity).apply {
            args?.let {
                this.arguments = it
            }
        }
    }
}