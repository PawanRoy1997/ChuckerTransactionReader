package com.nextxform.chuckerreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextxform.chuckerreader.ui.theme.ChuckerReaderTheme
import com.nextxform.chuckerreader.viewModels.ViewPermitViewModel
import kotlinx.coroutines.launch

class ViewTransactionActivity : ComponentActivity() {
    private val viewModel: ViewPermitViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChuckerReaderTheme {
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
                    }
                ) { innerPadding ->
                    Greeting(
                        name = viewModel.transaction.endPointName.orEmpty(),
                        request = viewModel.transaction.request.orEmpty(),
                        response = viewModel.transaction.response.orEmpty(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        viewModel.transactionId = intent.getIntExtra("transactionId", 0)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTransaction()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Greeting(
        modifier: Modifier = Modifier,
        name: String = "",
        request: String = "",
        response: String = ""
    ) {
        Column(modifier) {
            val coroutine = rememberCoroutineScope()
            val pagerState = rememberPagerState { 2 }
            val tabs = listOf("REQUEST", "RESPONSE")
            TabRow(
                selectedTabIndex = pagerState.currentPage, modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Gray)
            ) {
                tabs.forEachIndexed { index, value ->

                    Tab(
                        selected = index == pagerState.currentPage,
                        onClick = { coroutine.launch { pagerState.scrollToPage(index, 0f) } }) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
            HorizontalPager(state = pagerState) { page: Int ->
                LazyColumn(
                    Modifier.fillMaxSize()
                ) {
                    if (page == 0) {
                        item {
                            Text(
                                text = request,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = response,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ChuckerReaderTheme {
            Greeting(name = "Android")
        }
    }
}
