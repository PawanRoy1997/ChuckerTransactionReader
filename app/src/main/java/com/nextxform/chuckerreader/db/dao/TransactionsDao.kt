package com.nextxform.chuckerreader.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.nextxform.chuckerreader.db.model.Transaction

@Dao
interface TransactionsDao {
    @Query("SELECT * FROM `transaction`")
    suspend fun getAllTransactions(): List<Transaction>

    @Insert
    suspend fun addAll(transactions: List<Transaction>)

    @Insert
    suspend fun add(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("Delete from `transaction`")
    suspend fun deleteAll()
}