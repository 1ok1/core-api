package com.elite.api

import android.app.Application
import io.elite.core.Elite
import io.elite.core.Environment

class ApiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Elite.instance.context(applicationContext)
            .environment(Environment.PROD).authUrl("").useRole("users")
            .appName("HipBar-Drinks").timeOut(10).build()
    }
}