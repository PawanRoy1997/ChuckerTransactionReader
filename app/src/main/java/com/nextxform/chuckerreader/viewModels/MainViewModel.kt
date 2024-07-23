package com.nextxform.chuckerreader.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextxform.chuckerreader.db.TransactionDatabase
import com.nextxform.chuckerreader.db.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainViewModel : ViewModel() {
    val transactions = ArrayList<Transaction>()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun parseTransactions(uri: Uri, context: Context) {
        _isLoading.value = true
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
            transactions.clear()
            file.forEachLine { line ->
                if (line.matches(Regex("^/\\*.+")) || line.trim().matches(Regex("=+"))) {

                    transaction.response = sb.toString()

                    if (!transaction.status.isNullOrEmpty()) {
                        transactions.add(transaction)
                    }

                    transaction = Transaction(0)
                    sb = StringBuilder()
                    return@forEachLine
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
                if (line.matches(Regex("---------- Request ----------"))) {
                    return@forEachLine
                }
                if (line.matches(Regex("---------- Response ----------"))) {
                    transaction.request = sb.toString()
                    sb = StringBuilder()
                } else {
                    if (line.trim().isNotEmpty()) sb.append(line).append("\n")
                }
            }

            transactions.forEach {
                Log.d("Trans", it.uid.toString())
            }
            TransactionDatabase.database.transactionsDao().addAll(transactions)
            _isLoading.value = false
        }
    }

    fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val elements = TransactionDatabase.database.transactionsDao().getAllTransactions()
            transactions.addAll(elements)
            elements.forEach {
                Log.d("Trans", it.uid.toString())
            }
            _isLoading.value = false
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isLoading.value = true
            TransactionDatabase.database.transactionsDao().deleteAll()
            transactions.clear()
            _isLoading.value = false
        }
    }
}