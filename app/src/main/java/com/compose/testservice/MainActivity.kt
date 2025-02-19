package com.compose.testservice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.compose.testservice.service.MyForegroundService
import com.compose.testservice.service.isServiceRunning
import com.compose.testservice.ui.theme.TestServiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }
    }
}


@Composable
fun ServiceController(context: Context) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            if (!isServiceRunning(context, MyForegroundService::class.java)) {
                startService(context)
            }
        }) {
            Text("Iniciar servicio")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { stopService(context) }) {
            Text("Detener Servicio")
        }
    }
}

private fun startService(context: Context) {
    val serviceIntent = Intent(context, MyForegroundService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)
}

private fun stopService(context: Context) {
    val serviceIntent = Intent(context, MyForegroundService::class.java)
    context.stopService(serviceIntent)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, context: Context) {
    ServiceController(context)
}
