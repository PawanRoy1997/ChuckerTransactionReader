package com.nextxform.chuckerreader.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextxform.chuckerreader.db.TransactionDatabase
import com.nextxform.chuckerreader.db.model.Transaction
import kotlinx.coroutines.launch

class ViewPermitViewModel: ViewModel() {
    var transactionId: Int = 0
    var transaction by mutableStateOf(Transaction())

    fun getTransaction() {
        viewModelScope.launch {
            transaction = TransactionDatabase.database.transactionsDao().getTransaction(transactionId)
        }
    }

    fun delete() {
        viewModelScope.launch {
            TransactionDatabase.database.transactionsDao().delete(transaction)
        }
    }
}