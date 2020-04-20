package com.mdzz.fragment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.FragmentTransaction
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import com.mdzz.activity.MainActivity
import java.lang.ref.WeakReference

abstract class BasePreferenceFragment : PreferenceFragment() {

    private lateinit var mActivity: WeakReference<MainActivity>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity
        if (activity is MainActivity) {
            mActivity = WeakReference(activity)
        }
    }

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



    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return when (transit) {
            FragmentTransaction.TRANSIT_FRAGMENT_OPEN -> {
                if (enter) {
                    AnimatorInflater.loadAnimator(activity, android.R.animator.fade_in)
                } else {
                    AnimatorInflater.loadAnimator(activity, android.R.animator.fade_out)
                }
            }
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE -> {
                if (enter) {
                    AnimatorInflater.loadAnimator(activity, android.R.animator.fade_in)
                } else {
                    AnimatorInflater.loadAnimator(activity, android.R.animator.fade_out)
                }
            }
            else -> {
                super.onCreateAnimator(transit, enter, nextAnim)
            }
        }
    }

    abstract fun getXmlId(): Int
}