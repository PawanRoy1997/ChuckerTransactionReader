package com.nextxform.chuckerreader

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextxform.chuckerreader.models.Transaction
import com.nextxform.chuckerreader.ui.theme.ChuckerReaderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private lateinit var fileLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChuckerReaderTheme {

                MainScreen {
                    fileLauncher.launch("text/plain")
                }
            }
        }

        fileLauncher = registerForActivityResult(FilePickerContract()) { fileUri ->
            if (fileUri == null) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    val file = File.createTempFile("Transactions", ".txt")
                    file.deleteOnExit()
                    try {
                        contentResolver.openInputStream(fileUri).use { fileInputStream ->
                            FileOutputStream(file).use { fileOutputStream ->
                                fileInputStream?.copyTo(fileOutputStream)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("File", "Something went wrong!! ${e.message}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(getFile: () -> Unit = {}) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.background(Color.Gray),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Records"
                        )
                    }
                }
            )

        },
        floatingActionButton = {
            FloatingActionButton(onClick = { getFile.invoke() }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Import")
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize(1f)
                .padding(innerPadding)
        ) {

            val transaction = Transaction(
                status = "200",
                transactionName = "Transaction Name",
                endPointName = "www.google.com",
                time = "2 PM",
                duration = "400ms",
                size = "2mb"
            )
            TransactionFile(transaction.copy(status = "100"))
            TransactionFile(transaction.copy(status = "201"))
            TransactionFile(transaction.copy(status = "300"))
            TransactionFile(transaction.copy(status = "400"))
            TransactionFile(transaction.copy(status = "500"))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ChuckerReaderTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenNightPreview() {
    ChuckerReaderTheme {
        MainScreen()
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTransactionFile() {
    ChuckerReaderTheme {
        val transaction = Transaction(
            status = "200",
            transactionName = "Transaction Name",
            endPointName = "www.google.com",
            time = "2 PM",
            duration = "400ms",
            size = "2mb"
        )
        TransactionFile(transaction)
    }
}

@Composable
fun TransactionFile(transaction: Transaction) {
    Row(
        Modifier
            .padding(all = 8.dp)
            .fillMaxWidth(1f)
    ) {
        val statusColor = when {
            transaction.status.matches(Regex("1..")) -> Color.Gray
            transaction.status.matches(Regex("2..")) -> Color(0xFF4CAF50)
            transaction.status.matches(Regex("3..")) -> Color(0xFFFFC107)
            transaction.status.matches(Regex("4..")) -> Color(0xFFE91E63)
            transaction.status.matches(Regex("5..")) -> Color(0xFF673AB7)
            else -> Color.DarkGray
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    shape = CircleShape, color = statusColor
                ), contentAlignment = Alignment.Center
        ) {
            Text(
                text = transaction.status,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = transaction.transactionName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "End Point",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = transaction.endPointName, style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.width(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = transaction.time, style = MaterialTheme.typography.bodySmall)
                Text(text = transaction.duration, style = MaterialTheme.typography.bodySmall)
                Text(text = transaction.size, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}