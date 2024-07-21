package com.nextxform.chuckerreader

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
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
import com.nextxform.chuckerreader.viewModels.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var fileLauncher: ActivityResultLauncher<String>
    private val viewModel: MainViewModel by viewModels()

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
                viewModel.parseTransactions(fileUri, this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.createDatabase(this)
        viewModel.getAllTransactions()
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
                        IconButton(onClick = { viewModel.delete() }) {
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
            LazyColumn(
                Modifier
                    .fillMaxSize(1f)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.showLoading) {
                    item { CircularProgressIndicator(Modifier.size(50.dp)) }
                } else {
                    viewModel.transactions.forEach { trans ->
                        item { TransactionFile(transaction = trans) }
                    }
                }
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
                transactionName = "POST",
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
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val statusColor = when {
                transaction.status.matches(Regex("1..")) -> Color.Gray
                transaction.status.matches(Regex("2..")) -> Color(0xFF4CAF50)
                transaction.status.matches(Regex("3..")) -> Color(0xFFFFC107)
                transaction.status.matches(Regex("4..")) -> Color(0xFFE91E63)
                transaction.status.matches(Regex("5..")) -> Color(0xFF673AB7)
                else -> Color.DarkGray
            }

            val requestColor = when {
                transaction.transactionName.matches(Regex("PUT")) -> Color.Gray
                transaction.transactionName.matches(Regex("GET")) -> Color.White
                transaction.transactionName.matches(Regex("POST")) -> Color(0xFFFFC107)
                transaction.transactionName.matches(Regex("DELETE")) -> Color(0xFFE91E63)
                transaction.transactionName.matches(Regex("PATCH")) -> Color(0xFF673AB7)
                else -> Color.DarkGray
            }



            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        shape = CircleShape, color = statusColor
                    ), contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = transaction.status,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = transaction.transactionName,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        color = requestColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "End Point",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = transaction.endPointName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = transaction.time, style = MaterialTheme.typography.bodySmall)
                    Text(text = transaction.duration, style = MaterialTheme.typography.bodySmall)
                    Text(text = transaction.size, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}