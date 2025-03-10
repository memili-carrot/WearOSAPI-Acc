package com.example.wearosacc.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accX by mutableStateOf("Fetching...")
    private var accY by mutableStateOf("Fetching...")
    private var accZ by mutableStateOf("Fetching...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SensorManager 초기화
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        setContent {
            AccelWearOSApp(accX, accY, accZ)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            accX = "%.2f".format(it.values[0])
            accY = "%.2f".format(it.values[1])
            accZ = "%.2f".format(it.values[2])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 이벤트 필요 없음
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun AccelWearOSApp(accX: String, accY: String, accZ: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Accelerometer Data", style = MaterialTheme.typography.body1, modifier = Modifier.padding(16.dp))
        Text("X: $accX", modifier = Modifier.padding(8.dp))
        Text("Y: $accY", modifier = Modifier.padding(8.dp))
        Text("Z: $accZ", modifier = Modifier.padding(8.dp))
    }
}