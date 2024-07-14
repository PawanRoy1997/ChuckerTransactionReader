package com.nextxform.chuckerreader.models

data class Transaction(
    val status: String,
    val transactionName: String,
    val endPointName: String,
    val time: String,
    val duration: String,
    val size: String
)