package com.ufpr.equilibrium
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class Timer : AppCompatActivity(), SensorEventListener {
    private var timerTextView: TextView? = null
    private var pauseButton: Button? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private val running = AtomicBoolean(false)
    private var startTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())

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

        pauseButton?.setOnClickListener { toggleTimer() }
        refreshBtn.setOnClickListener { resetTimer() }

        arrowBack.setOnClickListener {
            val intent = Intent(this, FtstsInstruction::class.java)
            startActivity(intent);
            finish();
        }

        startTimerAndSensors()
    }

    private val timerRunnable = object : Runnable {

        override fun run() {
            if (running.get()) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / (1000 * 60)) % 60
                timerTextView?.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startTimerAndSensors() {
        startTime = System.currentTimeMillis()
        running.set(true)
        handler.post(timerRunnable)
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager!!.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
    }

    private fun stopTimerAndSensors() {
        running.set(false)
        handler.removeCallbacks(timerRunnable)
        sensorManager!!.unregisterListener(this)
    }

    private fun toggleTimer() {
        if (running.get()) {
            stopTimerAndSensors()

        } else {
            startTimerAndSensors()
        }

        pauseButton!!.text = if (running.get()) "Pausar" else "Enviar"
    }

    private fun resetTimer() {
        stopTimerAndSensors()
        startTimerAndSensors()

        pauseButton!!.text = "Pausar"
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (running.get()) {
            val dataJson = JSONObject()

            try {
                val timestamp = SimpleDateFormat("HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
                dataJson.put("timestamp", timestamp)

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    dataJson.put("acc_x", event.values[0].toDouble())
                    dataJson.put("acc_y", event.values[1].toDouble())
                    dataJson.put("acc_z", event.values[2].toDouble())

                } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
                    dataJson.put("gyro_x", event.values[0].toDouble())
                    dataJson.put("gyro_y", event.values[1].toDouble())
                    dataJson.put("gyro_z", event.values[2].toDouble())
                }

                println(dataJson)

            } catch (ignored: Exception) {}
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit



}