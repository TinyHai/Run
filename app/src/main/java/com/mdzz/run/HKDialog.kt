package com.mdzz.run

import android.app.Dialog
import android.view.ViewGroup
import android.widget.TextView
import com.mdzz.run.base.BaseHook
import com.mdzz.run.util.XSharedPrefUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class HKDialog : BaseHook() {

    companion object {
        private const val TAG = "HKDialog"
    }

    override fun beginHook() {
        if (!XSharedPrefUtil.getBoolean(MAKE_DIALOG_CANCELABLE)) {
            return
        }
        XposedHelpers.findAndHookMethod("android.app.Dialog",
                classLoader, "setCancelable",
                Boolean::class.javaPrimitiveType, MySetTrueMethodHook)
        XposedHelpers.findAndHookMethod("android.app.Dialog", classLoader,
                "setCanceledOnTouchOutside", Boolean::class.javaPrimitiveType,
                MySetTrueMethodHook)
        XposedHelpers.findAndHookMethod("android.app.Dialog", classLoader,
                "setOnCancelListener",
                "android.content.DialogInterface\$OnCancelListener",
                MyCancelListenerMethodHook)
        if (XSharedPrefUtil.getBoolean(PREVENT_DIALOG)) {
            XposedHelpers.findAndHookMethod("android.app.Dialog", classLoader,
                    "show", MyShowMethodHook)
        }
        log(TAG, "run: 模块4工作正常")
    }

    private object MySetTrueMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, param.thisObject::class.java.name + ": "
                    + param.method.name + " ${param.args[0]}")
            param.args[0] = true
        }
    }

    private object MyCancelListenerMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, param.thisObject::class.java.name + ": "
                    + param.method.name)
            log(TAG, param.args[0]::class.java.name)
            param.args[0] = null
        }
    }

    private object MyShowMethodHook : XC_MethodHook() {

        private var mNeedPrevent = false

        private val dialogKeyWords
            get() = XSharedPrefUtil.getStringSet(KEY_WORDS)

        override fun beforeHookedMethod(param: MethodHookParam) {
            log(TAG, "${param.thisObject::class.java.name}: ${param.method.name}")
            log(TAG, "run: 成功拦截弹窗")
            if (!isNotBottomSheetDialog(param.thisObject)) {
                return
            }
            var mDecor: Any? = null
            try {
                val getWindow = param.thisObject::class.java.getMethod("getWindow")
                val mWindow = getWindow.invoke(param.thisObject)
                val getDecorView = mWindow::class.java.getMethod("getDecorView")
                mDecor = getDecorView.invoke(mWindow)
            } catch (th: Throwable) {
                log(TAG, th)
                mDecor = null
            }
            val setVisibility = try {
                mDecor!!::class.java.getMethod("setVisibility", Int::class.javaPrimitiveType)
            } catch (th: Throwable) {
                log(TAG, th)
                null
            }
            setVisibility?.invoke(mDecor, 8)
            mDecor?.let {
                mNeedPrevent = shouldPrevent(it as ViewGroup)
            }
            setVisibility?.invoke(mDecor, if (mNeedPrevent) 8 else 0)
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            if (mNeedPrevent) {
                val dismiss = param.thisObject::class.java.getMethod("dismiss")
                dismiss.invoke(param.thisObject)
            }
        }

        private fun shouldPrevent(decorView: ViewGroup): Boolean {
            var view = decorView
            val textViews = ArrayList<TextView>()
            val viewGroups = LinkedList<ViewGroup>()
            viewGroups.add(view)
            while (viewGroups.isNotEmpty()) {
                view = viewGroups.poll()
                for (index in 0 until view.childCount) {
                    val v = view.getChildAt(index)
                    if (v is ViewGroup) {
                        viewGroups.add(v)
                    } else if (v is TextView) {
                        textViews.add(v)
                    }
                }
            }
            textViews.forEach { tv ->
                dialogKeyWords.forEach {
                    if (tv.text.contains(it)) {
                        log(TAG, it::class.java.name + " text = ${tv.text}")
                        return true
                    }
                }
            }
            return false
        }

        private fun isNotBottomSheetDialog(dialog: Any)
                = dialog::class.java.name != "android.support.design.widget.BottomSheetDialog"
    }
}