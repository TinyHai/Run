package com.mdzz.hook.proxy

import android.content.SharedPreferences
import com.mdzz.hook.base.BaseHook

class SPProxy(private val sp: SharedPreferences) : SharedPreferences by sp {

    companion object {
        private const val TAG = "SPProxy"
    }

    private val keys = arrayOf("sp_hook_and_virtual")

    override fun getString(key: String, defValue: String?): String? {
        if (key in keys) {
            BaseHook.log(TAG, "$key : ${sp.getString(key, defValue)}")
            return defValue;
        }
        return sp.getString(key, defValue)
    }
}