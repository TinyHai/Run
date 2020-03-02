package com.mdzz.fragment

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceScreen
import com.mdzz.activity.MainActivity.Companion.XPTAG
import com.mdzz.run.EDXPOSED_TAG
import com.mdzz.run.R
import com.mdzz.run.XPOSED_TAG
import com.mdzz.toast.ToastUtil
import com.mdzz.util.PackageUtil

class MainFragment : BasePreferenceFragment() {

    private var isActive: Boolean = false

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
            summary = if (isActive) {
                when (XPTAG) {
                    XPOSED_TAG -> {
                        getString(R.string.is_active, "Xposed")
                    }
                    EDXPOSED_TAG -> {
                        getString(R.string.is_active, "EdXposed")
                    }
                    else -> {
                        getString(R.string.is_active, "未知环境")
                    }
                }
            } else {
                getString(R.string.no_active)
            }
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

        val xpManagerPackageNames = arrayOf("com.solohsu.android.edxp.manager",
                "de.robv.android.xposed.installer", "org.meowcat.edxposed.manager")

        var installedPackageName = ""

        xpManagerPackageNames.forEach {
            if (PackageUtil.hasInstalledIt(activity, it)) {
                installedPackageName = it
                return@forEach
            }
        }

        if (installedPackageName != "" && installedPackageName != "org.meowcat.edxposed.manager") {
            val intent = Intent().apply {
                component = ComponentName(installedPackageName,
                        "de.robv.android.xposed.installer.WelcomeActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("fragment", 1)
            }
            startActivity(intent)
        } else if (installedPackageName == "org.meowcat.edxposed.manager") {
            val intent = Intent().apply {
                component = ComponentName(installedPackageName,
                        "org.meowcat.edxposed.manager.WelcomeActivityMlgmXyysd")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("fragment", 1)
            }
            startActivity(intent)
        } else {
            ToastUtil.makeToast("请先安装XposedInstaller后重试")
        }
    }

    companion object {
        private const val TAG = "MainFragment"

        const val IS_ACTIVE = "is_active"

        fun newInstance(args: Bundle? = null)
                = MainFragment().apply {
            args?.let {
                this.arguments = it
            }
        }
    }
}