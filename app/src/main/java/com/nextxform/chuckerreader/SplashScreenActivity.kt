package com.nextxform.chuckerreader

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextxform.chuckerreader.ui.theme.ChuckerReaderTheme
import java.util.concurrent.TimeUnit

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChuckerReaderTheme {
                SplashScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Handler(Looper.getMainLooper()).postDelayed(
            { startActivity(Intent(this, MainActivity::class.java)) },
            2000
        )
    }

    @Composable
    private fun SplashScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Image(
                    painter = painterResource(id = R.drawable.launcher),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            Color(0xFF00A4FD),
                            RoundedCornerShape(20.dp)
                        )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Made with ❤️ in India by ")
                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun PreviewSplashScreen() {
        ChuckerReaderTheme {
            SplashScreen()
        }
    }
}
