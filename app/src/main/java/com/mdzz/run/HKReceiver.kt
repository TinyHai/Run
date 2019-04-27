package com.mdzz.run

import android.content.Context
import android.content.Intent
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.FileUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class HKReceiver : BaseHook() {

    companion object {
        private const val TAG = "HKReceiver"
    }

    override fun beginHook() {
        val clazz = classLoader.loadClass("com.zjwh.android_wh_physicalfitness.receiver.JPushReceiver")
        XposedHelpers.findAndHookMethod(clazz, "onReceive", Context::class.java,
                Intent::class.java, MyMethodHook)

        log(TAG, "run: 模块6工作正常")
    }

    private object MyMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            val intent = param.args[1] as Intent
            val extras = intent.extras
            var info: String? = null
            when (intent.action) {
                "cn.jpush.android.intent.REGISTRATION" -> {
                    info = extras.getString("cn.jpush.android.REGISTRATION_ID")
                }
                "cn.jpush.android.intent.MESSAGE_RECEIVED" -> {
                    info = extras.getString("cn.jpush.android.MESSAGE")
                }
                "cn.jpush.android.intent.NOTIFICATION_RECEIVED" -> {
                    info = extras.getString("cn.jpush.android.NOTIFICATION_ID")
                }
                "cn.jpush.android.intent.NOTIFICATION_OPENED" -> {
                    info = extras.getString("cn.jpush.android.EXTRA")
                }
                "cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK" -> {
                    info = extras.getString("cn.jpush.android.EXTRA")
                }
                "cn.jpush.android.intent.CONNECTION" -> {
                    info = extras.getString("cn.jpush.android.CONNECTION_CHANGE")
                }
            }
            FileUtil.saveInfo(intent.action, info)
        }
    }
}