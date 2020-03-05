package com.mdzz.hook.util

import android.util.Log
import de.robv.android.xposed.XposedBridge
import java.io.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object LogUtil {

    val XPTAG by lazy {
        try {
            val xposedBridgeClass = Class.forName("de.robv.android.xposed.XposedBridge")
            val tagField = xposedBridgeClass.getDeclaredField("TAG")
            tagField.isAccessible = tagField.isAccessible or true
            tagField.get(null).toString()
        } catch (e: ClassNotFoundException) {
            "Unknown"
        } catch (th: Throwable) {
            XposedBridge.log(th)
            "EdXposed-Bridge"
        }
    }

    private val threadExecutor: ExecutorService by lazy {
        Executors.newFixedThreadPool(1)
    }

    private val infoDir by lazy { File("/data/data/com.zjwh.android_wh_physicalfitness/infos_1") }
    private val infoFile by lazy { File(infoDir, "infos.txt") }
    private val fileWriter by lazy { FileWriter(infoFile, true) }

    fun saveInfo(tag: String, info: String?) {
        Log.e(XPTAG, info)
        synchronized(threadExecutor) {
            if (threadExecutor.isTerminated) {
                return
            }
            threadExecutor.submit {
                if (infoDir.exists() && !infoDir.isDirectory) {
                    infoDir.delete()
                } else if (!infoDir.exists()) {
                    infoDir.mkdir()
                }
                if (!infoFile.exists()) {
                    infoFile.createNewFile()
                }
                if (infoFile.length() > 2 shl 20) {
                    infoFile.delete()
                    infoFile.createNewFile()
                }
                fileWriter.append("$tag: ")
                        .append(info)
                        .append("\n")
                        .flush()
            }
        }
    }

    fun saveInfo(tag: String, th: Throwable) {
        this.saveInfo(tag, Log.getStackTraceString(th))
    }

    fun closeThreadPool() {
        synchronized(threadExecutor) {
            if (!threadExecutor.isTerminated) {
                threadExecutor.shutdownNow()
            }
            fileWriter.use {
                it.flush()
            }
        }
    }
}