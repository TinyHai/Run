package com.mdzz

import android.app.Application

class MyApplication : Application() {

    companion object {
        lateinit var application: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}