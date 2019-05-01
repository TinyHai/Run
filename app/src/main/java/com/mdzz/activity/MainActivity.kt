package com.mdzz.activity

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.ComponentName
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.mdzz.fragment.AboutFragment
import com.mdzz.fragment.MainFragment
import com.mdzz.run.BuildConfig
import com.mdzz.run.R
import com.mdzz.util.FileUtil
import de.robv.android.xposed.XposedBridge
import java.io.File
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_main_layout)
        setupFragment(MAIN_FRAGMENT)
        makePrefReadable()
    }

    override fun onResume() {
        super.onResume()
        makePrefReadable()
    }

    override fun onDestroy() {
        super.onDestroy()
        makePrefReadable()
    }

    fun setupFragment(flag: Int) {
        val fragment = when (flag) {
            MAIN_FRAGMENT -> {
                val bundle = Bundle()
                bundle.putBoolean(MainFragment.IS_ACTIVE, isActive())
                MainFragment.newInstance(this, bundle)
            }
            ABOUT_FRAGMENT -> AboutFragment.newInstance(this)
            else -> null
        }
        if (fragment != null) {
            val fm = fragmentManager.beginTransaction()
                    .replace(R.id.pref_fragment, fragment)
                    .setCustomAnimations(FragmentTransaction.TRANSIT_FRAGMENT_FADE,
                            FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            if (flag == ABOUT_FRAGMENT) {
                fm.addToBackStack(null)
            }
            fm.commit()
        }
    }

    private fun isActive(): Boolean {
        Log.v("Run: ", "running")
        return false
    }

    private fun setAliasStatus() {
        if (isAliasHide()) {
            packageManager.setComponentEnabledSetting(getAliasComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
        } else {
            packageManager.setComponentEnabledSetting(getAliasComponentName(),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP)
        }
    }


    @SuppressLint("SwitchIntDef")
    private fun isAliasHide() = when (packageManager.getComponentEnabledSetting(
            getAliasComponentName())) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> false
        else -> true
    }

    private fun getAliasComponentName() = ComponentName(this,
            "com.mdzz.activity.MainActivityAlias")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.icon_hide_mene).apply {
            title = if (isAliasHide()) {
                getString(R.string.view_icon)
            } else {
                getString(R.string.hide_icon)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.icon_hide_mene -> {
                if (isAliasHide()) {
                    item.title = getString(R.string.hide_icon)
                } else {
                    item.title = getString(R.string.view_icon)
                }
                setAliasStatus()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetWorldReadable")
    private fun makePrefReadable() {
        arrayOf(FileUtil.getDataFile(this), FileUtil.getPrefDirFile(this),
                FileUtil.getPrefFile(this)).forEach {
            if (it.exists()) {
                it.setReadable(true, false)
                it.setExecutable(true, false)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        const val MAIN_FRAGMENT = 1 shl 3

        const val ABOUT_FRAGMENT = 1 shl 4
    }
}