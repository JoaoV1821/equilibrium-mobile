package com.ufpr.equilibrium

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.ConcurrentHashMap

class Timer : AppCompatActivity(), SensorEventListener {
    private var timerTextView: TextView? = null
    private var pauseButton: Button? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private val running = AtomicBoolean(false)
    private var startTime: Long = 0
    private val frequency = (1_000_000 / 60)
    private var lastMergedTimestamp: String? = null
    private val sensorData = ConcurrentHashMap<String, JSONObject>()
    private val result = Collections.synchronizedList(mutableListOf<JSONObject>())

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var cycleCount = 0
    private val peakThreshold = 1.0
    private var peakDetected = false
    private val maxCycles = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Timer, FtstsInstruction::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        })

        timerTextView = findViewById(R.id.timerTextView)
        pauseButton = findViewById(R.id.pauseButton)
        val refreshBtn = findViewById<ImageView>(R.id.refresh)
        val arrowBack = findViewById<ImageView>(R.id.arrow_back)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        pauseButton?.setOnClickListener {
            if (pauseButton!!.text == "Enviar") {
                println(cycleCount);
                sendData()

            }  else {
                toggleTimer()
            }
        }

        refreshBtn.setOnClickListener { resetTimer() }

        arrowBack.setOnClickListener {
            val intent = Intent(this, FtstsInstruction::class.java)
            startActivity(intent)
            finish()
        }

        startTimerAndSensors()
    }

    private fun startTimerAndSensors() {
        startTime = System.currentTimeMillis()
        running.set(true)
        startTimerTask()
        startSensorCollection()
    }

    private fun stopTimerAndSensors() {
        running.set(false)
        sensorManager!!.unregisterListener(this)
    }

    private fun toggleTimer() {
        if (running.get()) {
            stopTimerAndSensors()
        }
        pauseButton!!.text = if (running.get()) "Pausar" else "Enviar"
    }

    private fun resetTimer() {
        stopTimerAndSensors()
        startTimerAndSensors()
        pauseButton!!.text = "Pausar"
    }

    private fun startTimerTask() {
        coroutineScope.launch(Dispatchers.Main) {
            while (running.get()) {
                val elapsed = System.currentTimeMillis() - startTime
                timerTextView?.text = formatTime(elapsed)
                delay(1000)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60

        return String.format("%02d:%02d", minutes, seconds, )
    }

    private fun startSensorCollection() {
        coroutineScope.launch {
            sensorManager!!.registerListener(this@Timer, accelerometer, frequency)
            sensorManager!!.registerListener(this@Timer, gyroscope, frequency)
        }
    }

    private fun detectCycle(gyroX: Double) {
        if (gyroX > peakThreshold && !peakDetected) {
            peakDetected = true
            cycleCount++

            if (cycleCount / 2 >= maxCycles) {
                stopTimerAndSensors()
                sendData()
            }
        } else if (gyroX < 0.2) {
            peakDetected = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!running.get()) return

        val sensorTimestamp = SimpleDateFormat("HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())

        val sensorType = if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) "accelerometer" else "gyroscope"

        sensorData[sensorType] = JSONObject().apply {
            put("timestamp", sensorTimestamp)
            put("x", event.values[0].toDouble())
            put("y", event.values[1].toDouble())
            put("z", event.values[2].toDouble())
        }

        if (sensorType == "gyroscope") {
            detectCycle(event.values[0].toDouble())
        }

        if (sensorType == "accelerometer") {
            mergeSensorData()
        }
    }

    private fun mergeSensorData() {
        val acc = sensorData["accelerometer"]
        val gyro = sensorData["gyroscope"]

        if (acc != null && gyro != null) {

            val currentTimestamp = acc.getString("timestamp")

            if (currentTimestamp == lastMergedTimestamp) return

            lastMergedTimestamp = currentTimestamp

            synchronized(result) {

                val mergedJson = JSONObject().apply {

                    put("timestamp", currentTimestamp)
                    put("acc_x", acc.getDouble("x"))
                    put("acc_y", acc.getDouble("y"))
                    put("acc_z", acc.getDouble("z"))
                    put("gyro_x", gyro.getDouble("x"))
                    put("gyro_y", gyro.getDouble("y"))
                    put("gyro_z", gyro.getDouble("z"))
                    
                }

                result.add(mergedJson)
                sensorData.clear()
            }
        }
    }


    private fun sendData() {
        try {

            val folder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (folder != null && !folder.exists()) folder.mkdirs()

            val csvFile = File(folder, "sensor_data.csv")
            val writer = FileWriter(csvFile, )

            writer.append("timestamp,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z\n")

            synchronized(result) {
                for (json in result) {
                    writer.append(convertJsonToCsv(json))
                }
                result.clear()
            }

            writer.flush()
            writer.close()

            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", csvFile)

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }

            startActivity(Intent.createChooser(sendIntent, "Enviar CSV via WhatsApp"))

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun convertJsonToCsv(jsonObject: JSONObject): String {
        return "\n${jsonObject.optString("timestamp")},${jsonObject.optDouble("acc_x", 0.0)},${jsonObject.optDouble("acc_y", 0.0)},${jsonObject.optDouble("acc_z", 0.0)},${jsonObject.optDouble("gyro_x", 0.0)},${jsonObject.optDouble("gyro_y", 0.0)},${jsonObject.optDouble("gyro_z", 0.0)}"
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
}
