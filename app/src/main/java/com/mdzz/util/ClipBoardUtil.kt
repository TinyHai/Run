package com.mdzz.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipBoardUtil {
    fun setClipBoardContent(context: Context, data: ClipData) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = data
    }
}