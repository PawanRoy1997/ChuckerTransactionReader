package com.nextxform.chuckerreader.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextxform.chuckerreader.db.TransactionDatabase
import com.nextxform.chuckerreader.db.model.Transaction
import com.nextxform.chuckerreader.durationRegex
import com.nextxform.chuckerreader.exportRegex
import com.nextxform.chuckerreader.methodRegex
import com.nextxform.chuckerreader.requestStartRegex
import com.nextxform.chuckerreader.requestTimeRegex
import com.nextxform.chuckerreader.responseRegex
import com.nextxform.chuckerreader.responseStartRegex
import com.nextxform.chuckerreader.totalTimeRegex
import com.nextxform.chuckerreader.transactionSeparatorRegex
import com.nextxform.chuckerreader.urlRegex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class MainViewModel : ViewModel() {
    val transactions = ArrayList<Transaction>()

    fun parseTransactions(uri: Uri, context: Context, setLoading: (Boolean) -> Unit) {
        setLoading.invoke(true)
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("Trans", "Copy File Start")
            val file = File.createTempFile("Transactions", ".txt")
            file.deleteOnExit()
            runBlocking(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri).use { fileInputStream ->
                        FileOutputStream(file).use { fileOutputStream ->
                            fileInputStream?.copyTo(fileOutputStream)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("File", "Something went wrong!! ${e.message}")
                }
            }
            Log.d("Trans", "Copy File End")
            parseFile(file)
            getAllTransactions(setLoading)
            setLoading.invoke(false)
        }
    }

    private suspend fun parseFile(file: File) {
        Log.d("Trans", "Parsing Transaction Start")
        var transaction = Transaction(0)
        var sb = StringBuilder()
        file.forEachLine { line ->
            if (line.matches(exportRegex) || line.trim().matches(transactionSeparatorRegex)) {

                transaction.response = sb.toString()

                if (!transaction.status.isNullOrEmpty()) {
                    runBlocking(Dispatchers.Default) {
                        TransactionDatabase.database.transactionsDao().add(transaction)
                    }
                }

                transaction = Transaction(0)
                sb = StringBuilder()
                return@forEachLine
            }
            if (line.matches(urlRegex)) {
                transaction.endPointName = line.substring(startIndex = 4).trim()
            }
            if (line.matches(methodRegex)) {
                transaction.transactionName = line.substringAfter("Method: ").trim()
            }
            if (line.matches(responseRegex)) {
                transaction.status = line.substringAfter("Response: ").trim()
            }
            if (line.matches(requestTimeRegex)) {
                transaction.time = line.substringAfter("Request time: ").trim()
            }
            if (line.matches(durationRegex)) {
                transaction.duration = line.substringAfter("Duration: ").trim()
            }
            if (line.matches(totalTimeRegex)) {
                transaction.size = line.substringAfter("Total size: ").trim()
            }
            if (line.matches(requestStartRegex)) {
                return@forEachLine
            }
            if (line.matches(responseStartRegex)) {
                transaction.request = sb.toString()
                sb = StringBuilder()
            } else {
                if (line.trim().isNotEmpty()) sb.append(line).append("\n")
            }
        }

        file.delete()
        Log.d("Trans", "Parsing Transaction End")
    }

    fun getAllTransactions(setLoading: (Boolean) -> Unit) {
        viewModelScope.launch {
            setLoading.invoke(true)
            val elements = TransactionDatabase.database.transactionsDao().getAllTransactions()
            transactions.addAll(elements)
            elements.forEach {
                Log.d("Trans", it.uid.toString())
            }
            setLoading.invoke(false)
        }
    }

    fun delete(setLoading: (Boolean) -> Unit) {
        viewModelScope.launch {
            setLoading.invoke(true)
            TransactionDatabase.database.transactionsDao().deleteAll()
            transactions.clear()
            setLoading.invoke(false)
        }
    }
}