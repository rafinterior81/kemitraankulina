package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.MitraRepository

class KulinaMitraApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { MitraRepository(database) }

    override fun onCreate() {
        super.onCreate()
    }
}
