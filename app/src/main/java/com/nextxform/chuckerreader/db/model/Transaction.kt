package com.nextxform.chuckerreader.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("status") var status: String? = null,
    @ColumnInfo("transactionName") var transactionName: String? = null,
    @ColumnInfo("endPointName") var endPointName: String? = null,
    @ColumnInfo("time") var time: String? = null,
    @ColumnInfo("duration") var duration: String? = null,
    @ColumnInfo("size") var size: String? = null,
    @ColumnInfo("request") var request: String? = null,
    @ColumnInfo("response") var response: String? = null
)