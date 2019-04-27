package com.mdzz.run.util

import de.robv.android.xposed.XposedBridge
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

object FileUtil {

    private val threadExecutor: ExecutorService by lazy {
        Executors.newFixedThreadPool(1)
    }

    fun saveInfo(tag: String, info: String?) {
        threadExecutor.submit {
            var fileWriter: FileWriter? = null
            try {
                val file = File("/data/data/com.zjwh.android_wh_physicalfitness/infos_1")
                if (file.exists() && !file.isDirectory) {
                    file.delete()
                } else if (!file.exists()) {
                    file.mkdir()
                }
                val infoFile = File(file, "infos.txt")
                if (!infoFile.exists()) {
                    infoFile.createNewFile()
                }
                if (file.length() > 10 * (2 shr 20)) {
                    file.delete()
                    file.createNewFile()
                }
                fileWriter = FileWriter(infoFile, true)
                fileWriter.append("$tag: ")
                        .append(info)
                        .append("\n")
            } catch (e: IOException) {
                XposedBridge.log(e)
            } finally {
                try {
                    fileWriter?.close()
                } catch (e: IOException) {}
            }
        }
    }

    fun saveInfo(tag: String, th: Throwable) {
        this.saveInfo(tag, th.message)
    }
}