package com.mdzz.run

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.hotxposed.HotXposed

class XposedInit : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "XposedInit"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        HotXposed.hook(XposedMain::class.java, lpparam)
    }
}