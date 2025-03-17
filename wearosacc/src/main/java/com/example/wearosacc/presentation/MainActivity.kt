package com.example.wearosacc.presentation

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accX by mutableStateOf(0f)
    private var accY by mutableStateOf(0f)
    private var accZ by mutableStateOf(0f)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var sensorDataList: JSONArray // 🔥 `lateinit`으로 선언하고, `onCreate()`에서 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 onCreate에서 JSON 파일 로드 (Context가 초기화된 후)
        sensorDataList = loadJsonFromFile()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        setContent {
            AccelWearOSApp(accX.toString(), accY.toString(), accZ.toString())
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            accX = it.values[0]
            accY = it.values[1]
            accZ = it.values[2]

            val sensorData = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("sensor_name", "Accelerometer Sensor")
                put("x", accX)
                put("y", accY)
                put("z", accZ)
            }

            if (sensorDataList.length() >= 100) {
                sensorDataList.remove(0)
            }

            sensorDataList.put(sensorData)

            coroutineScope.launch {
                saveJsonToFile(sensorDataList)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun saveJsonToFile(jsonArray: JSONArray) {
        val fileName = "accel_data.json"
        val file = File(getExternalFilesDir(null), fileName)

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonArray.toString(4))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadJsonFromFile(): JSONArray {
        val fileName = "accel_data.json"
        val file = File(getExternalFilesDir(null), fileName)

        return try {
            if (file.exists()) {
                val content = file.readText()
                if (content.isNotEmpty()) JSONArray(content) else JSONArray()
            } else {
                JSONArray()
            }
        } catch (e: Exception) {
            JSONArray() // 파일이 없거나 손상된 경우 빈 배열 반환
        }
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