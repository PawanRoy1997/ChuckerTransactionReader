package com.nextxform.chuckerreader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nextxform.chuckerreader.db.dao.TransactionsDao
import com.nextxform.chuckerreader.db.model.Transaction

@Database(entities = [Transaction::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
}

object TransactionDatabase{
    lateinit var database: com.nextxform.chuckerreader.db.Database

    fun init(context: Context){
        database = Room.databaseBuilder(
            context.applicationContext,
            com.nextxform.chuckerreader.db.Database::class.java,
            "transaction-database"
        ).build()
    }
}