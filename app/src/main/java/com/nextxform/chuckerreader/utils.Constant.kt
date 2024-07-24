package com.nextxform.chuckerreader

val urlRegex = Regex("^URL: .+")
val methodRegex = Regex("^Method: .+")
val responseRegex = Regex("^Response: .+")
val requestTimeRegex = Regex("^Request time: .+")
val durationRegex = Regex("^Duration: .+")
val totalTimeRegex = Regex("^Total size: .+")
val requestStartRegex = Regex("---------- Request ----------")
val responseStartRegex = Regex("---------- Response ----------")
val exportRegex = Regex("^/\\*.+")
val transactionSeparatorRegex = Regex("=+")