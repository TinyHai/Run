package com.mdzz.run.util

import java.io.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object LogUtil {

    private val threadExecutor: ExecutorService by lazy {
        Executors.newFixedThreadPool(1)
    }

    private val file = File("/data/data/com.zjwh.android_wh_physicalfitness/infos_1")
    private val infoFile = File(file, "infos.txt")
    private val fileWriter = FileWriter(infoFile, true)

    fun saveInfo(tag: String, info: String?) {
        synchronized(threadExecutor) {
            if (threadExecutor.isTerminated) {
                return
            }
            threadExecutor.submit {
                fileWriter.use { writer ->
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
                    writer.append("$tag: ")
                            .append(info)
                            .append("\n")
                            .flush()
                }
            }
        }
    }

    fun saveInfo(tag: String, th: Throwable) {
        val stringWriter = StringWriter()
        th.printStackTrace(PrintWriter(stringWriter))
        this.saveInfo(tag, stringWriter.buffer.toString())
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