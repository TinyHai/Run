package com.mdzz.run.util

import de.robv.android.xposed.XposedBridge
import java.io.*
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

object FileUtil {

    private val threadExecutor: ExecutorService by lazy {
        Executors.newFixedThreadPool(1)
    }

    private val file = File("/data/data/com.zjwh.android_wh_physicalfitness/infos_1")
    private val infoFile = File(file, "infos.txt")
    private val fileWriter = FileWriter(infoFile, true)

    fun saveInfo(tag: String, info: String?) {
        threadExecutor.submit {
            try {
                if (file.exists() && !file.isDirectory) {
                    file.delete()
                } else if (!file.exists()) {
                    file.mkdir()
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
                fileWriter.flush()
            } catch (e: IOException) {
                try {
                    fileWriter.close()
                } catch (ignore: Exception) {}
            }
        }
    }

    fun saveInfo(tag: String, th: Throwable) {
        val stringWriter = StringWriter()
        th.printStackTrace(PrintWriter(stringWriter))
        this.saveInfo(tag, stringWriter.buffer.toString())
    }
}