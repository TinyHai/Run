package com.mdzz.hook

import android.os.Build
import com.mdzz.BuildConfig
import com.mdzz.hook.base.BaseHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * author: tiny
 * created on: 20-4-21 下午12:39
 */
class HKDexFile : BaseHook() {

    companion object {
        private const val TAG = "HKDexFile"
    }

    override fun beginHook() {
        if (BuildConfig.DEBUG.not()) {
            return
        }
        val dexFileClass = try {
            classLoader.loadClass(DEX_FILE_CLASS)
        } catch (e: Exception) {
            log(TAG, e)
            null
        } ?: return
        XposedHelpers.findAndHookConstructor(dexFileClass, ByteBuffer::class.java, MyMethodHook)
    }

    private object MyMethodHook : XC_MethodHook() {
        @ExperimentalUnsignedTypes
        override fun beforeHookedMethod(param: MethodHookParam?) {
            param?.let { p ->
                val byteBuffer = p.args[0] as? ByteBuffer ?: return
                val magicBytes = ByteArray(8)
                byteBuffer.get(magicBytes, 0, 8)
                log(TAG, magicBytes.toHexString())
            }
        }

        @ExperimentalUnsignedTypes
        private fun ByteArray.toHexString() = buildString {
            reversedArray().forEach {
                append(it.toUByte().toString(16))
            }
        }
    }
}