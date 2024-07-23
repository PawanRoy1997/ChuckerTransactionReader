package com.nextxform.chuckerreader

import android.app.Application
import com.nextxform.chuckerreader.db.TransactionDatabase

class TransactionReader: Application() {
    override fun onCreate() {
        super.onCreate()
        TransactionDatabase.init(this)
    }
}