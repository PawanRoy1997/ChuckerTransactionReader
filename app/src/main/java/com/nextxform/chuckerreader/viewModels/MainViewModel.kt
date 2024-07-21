package com.nextxform.chuckerreader.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.nextxform.chuckerreader.db.Database
import com.nextxform.chuckerreader.db.model.Transaction
import com.nextxform.chuckerreader.models.Transaction as Trans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainViewModel : ViewModel() {
    val transactions = ArrayList<Trans>()
    private lateinit var database: Database

    var showLoading by mutableStateOf(false)

    fun parseTransactions(uri: Uri, context: Context) {
        showLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            val file = File.createTempFile("Transactions", ".txt")
            file.deleteOnExit()
            try {
                context.contentResolver.openInputStream(uri).use { fileInputStream ->
                    FileOutputStream(file).use { fileOutputStream ->
                        fileInputStream?.copyTo(fileOutputStream)
                    }
                }
            } catch (e: Exception) {
                Log.d("File", "Something went wrong!! ${e.message}")
            }

            var transaction = Transaction(0)
            var sb = StringBuilder()

            val transactions = ArrayList<Transaction>()

            file.forEachLine { line ->
                if (line.matches(Regex("/* .+")) || line.matches(Regex("=+"))) {

                    transaction.response = sb.toString()

                    if (!transaction.status.isNullOrEmpty()) {
                        transactions.add(transaction)
                    }

                    transaction = Transaction(0)
                }
                if (line.matches(Regex("^URL: .+"))) {
                    transaction.endPointName = line.substring(startIndex = 4).trim()
                }
                if (line.matches(Regex("^Method: .+"))) {
                    transaction.transactionName = line.substringAfter("Method: ").trim()
                }
                if (line.matches(Regex("^Response: .+"))) {
                    transaction.status = line.substringAfter("Response: ").trim()
                }

                if (line.matches(Regex("^Request time: .+"))) {
                    transaction.time = line.substringAfter("Request time: ").trim()
                }
                if (line.matches(Regex("^Duration: .+"))) {
                    transaction.duration = line.substringAfter("Duration: ").trim()
                }

                if (line.matches(Regex("^Total size: .+"))) {
                    transaction.size = line.substringAfter("Total size: ").trim()
                }

                if (line.matches(Regex("---------- Response ----------"))) {
                    transaction.request = sb.toString()
                    sb = StringBuilder()
                } else {
                    sb.append(line)
                }
            }

            database.transactionsDao().addAll(transactions)
            showLoading = false
        }
    }

    fun getAllTransactions() {
        viewModelScope.launch {
            showLoading = true
            database.transactionsDao().getAllTransactions().forEach { t ->
                transactions.add(
                    Trans(
                        t.status.orEmpty(),
                        t.transactionName.orEmpty(),
                        t.endPointName.orEmpty(),
                        t.time.orEmpty(),
                        t.duration.orEmpty(),
                        t.size.orEmpty()
                    )
                )
            }
            showLoading = false
        }
    }

    fun createDatabase(context: Context) {
        database = Room.databaseBuilder(
            context.applicationContext,
            Database::class.java,
            "transaction-database"
        ).build()
    }

    fun delete() {
        viewModelScope.launch {
            showLoading = true
            database.transactionsDao().deleteAll()
            transactions.clear()
            showLoading = false
        }
    }
}