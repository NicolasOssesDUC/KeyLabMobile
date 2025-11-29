package com.keylab.mobile

import android.app.Application
import com.keylab.mobile.data.remote.RetrofitClient

class KeyLabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
