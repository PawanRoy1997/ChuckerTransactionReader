package com.nextxform.chuckerreader.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nextxform.chuckerreader.db.dao.TransactionsDao
import com.nextxform.chuckerreader.db.model.Transaction

@Database(entities = [Transaction::class], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao
}