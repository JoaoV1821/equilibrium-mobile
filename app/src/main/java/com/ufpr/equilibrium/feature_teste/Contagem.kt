package com.ufpr.equilibrium.feature_teste

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ufpr.equilibrium.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Contagem : AppCompatActivity(), TextToSpeech.OnInitListener, SensorEventListener {

    private var textToSpeech: TextToSpeech? = null
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0

    // Contagem de 5 até 1 e depois "JÁ!"
    private val countdownList = listOf("5","4","3","2","1","JÁ!")
    private var countdownRunning = false
    
    // Gerenciamento de sensores
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var linearAcceleration: Sensor? = null
    private var sensorsStarted = false
    
    // Frequência de 60 Hz
    private val frequency = 1_000_000 / 60

    companion object {
        private const val INTRO_UTTERANCE_ID = "INTRO_TTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contagem)

        // Limpar qualquer dado residual do buffer
        SensorDataBuffer.clear()

        textToSpeech = TextToSpeech(this, this)
        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = ContagemAdapter(countdownList)
        
        // Inicializar SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        // Não inicia a contagem aqui; esperamos o TTS inicial terminar.
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale("pt", "BR")

            // Ouvir quando a fala introdutória terminar para então iniciar a contagem
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    if (utteranceId == INTRO_UTTERANCE_ID) {
                        runOnUiThread { startCountdown() }
                    }
                }
                override fun onError(utteranceId: String?) {
                    // Em caso de erro no TTS, ainda assim começamos a contagem
                    if (utteranceId == INTRO_UTTERANCE_ID) {
                        runOnUiThread { startCountdown() }
                    }
                }
            })

            // Fala introdutória antes da contagem
            speakText(
                "Cinco segundos para começar o teste!",
                INTRO_UTTERANCE_ID
            )
        }
    }

    private fun startCountdown() {
        if (countdownRunning) return
        countdownRunning = true
        currentPage = 0

        // Iniciar coleta de sensores no início da contagem
        if (!sensorsStarted) {
            startSensorCollection()
            sensorsStarted = true
            SensorDataBuffer.collectionStartTime = System.currentTimeMillis()
        }

        // Exibe e fala a cada 1 segundo
        handler.post(object : Runnable {
            override fun run() {
                if (currentPage < countdownList.size) {
                    viewPager.setCurrentItem(currentPage, true)

                    val text = countdownList[currentPage]
                    speakText(text)

                    currentPage++

                    // "JÁ!" fica menos tempo (300 ms) e já troca de tela
                    val delay = if (currentPage == countdownList.size) 300L else 1000L
                    handler.postDelayed(this, delay)
                } else {
                    startTimerActivity()
                }
            }
        })
    }

    private fun speakText(text: String, utteranceId: String? = null) {
        // Usamos QUEUE_FLUSH para garantir que cada fala substitua a anterior
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId ?: System.nanoTime().toString())
    }

    private fun startTimerActivity() {
        // Parar sensores antes de navegar — Timer.kt vai registrar os próprios listeners
        stopSensorCollection()

        val teste = intent.getStringExtra("teste")
        val selectedUnitId = intent.getStringExtra("id_unidade")

        val newIntent = Intent(this, Timer::class.java).apply {
            putExtra("teste", teste)
            putExtra("id_unidade", selectedUnitId)
        }

        startActivity(newIntent)
        finish()
    }

    private fun startSensorCollection() {
        sensorManager.registerListener(this, accelerometer, frequency)
        sensorManager.registerListener(this, gyroscope, frequency)
        sensorManager.registerListener(this, linearAcceleration, frequency)
    }
    
    private fun stopSensorCollection() {
        if (sensorsStarted) {
            try {
                sensorManager.unregisterListener(this)
            } catch (_: Exception) { }
            sensorsStarted = false
        }
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        // Coleta real de dados de sensores para o SensorDataBuffer
        val timestampStr = formatTimestamp()
        val sensorData = floatArrayOf(event.values[0], event.values[1], event.values[2])

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                SensorDataBuffer.accelQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
            }
            Sensor.TYPE_GYROSCOPE -> {
                SensorDataBuffer.gyroQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                SensorDataBuffer.linearQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
            }
        }
    }

    private fun formatTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
    
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Não é necessário processar mudanças de precisão nesta tela
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        stopSensorCollection()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}



