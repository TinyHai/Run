package com.mdzz.run.proxy

import android.content.SharedPreferences
import com.mdzz.run.base.BaseHook
import org.json.JSONObject

class EditorProxy(private val editor: SharedPreferences.Editor) : SharedPreferences.Editor by editor{

    companion object {
        private const val TAG = "EditorProxy"
    }

    private val keys = arrayOf("sp_key_mock_data")

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        if (keys.contains(key)) {
            val jsonObject = JSONObject(value)
            BaseHook.log(TAG, "virtualType = ${jsonObject["virtualType"]}")
            jsonObject.put("virtualType", 1)
            BaseHook.log(TAG, "hookType = ${jsonObject["hookType"]}")
            jsonObject.put("hookType", 1)
            BaseHook.log(TAG, "mockType = ${jsonObject["mockType"]}")
            jsonObject.put("mockType", 1)
            editor.putString(key, jsonObject.toString())
        }
        editor.putString(key, value)
        return this
    }
}