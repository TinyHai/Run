package com.mdzz.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity

object UriUtil {
    fun openUri(activity: Activity, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(activity, intent, null)
    }
}