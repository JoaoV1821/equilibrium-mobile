package com.ufpr.equilibrium.feature_teste

import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.network.RetrofitClient
import com.ufpr.equilibrium.network.Teste
import com.ufpr.equilibrium.utils.PacienteManager
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.utils.RoleHelpers
import com.ufpr.equilibrium.utils.ErrorMessages
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class Timer : AppCompatActivity(), SensorEventListener, TextToSpeech.OnInitListener {

    private lateinit var timerTextView: TextView
    private lateinit var title: TextView
    private lateinit var pauseButton: Button
    private lateinit var sensorManager: SensorManager

    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var linearAceleration: Sensor? = null

    private val running = AtomicBoolean(false)
    private var startTime: Long = 0L

    // 25 Hz
    private val frequency = 1_000_000 / 25

    // filas de leitura brutas
    private val accelQueue = ConcurrentLinkedQueue<JSONObject>()
    private val gyroQueue = ConcurrentLinkedQueue<JSONObject>()
    private val linearQueue = ConcurrentLinkedQueue<JSONObject>()

    // resultado mesclado (apenas ACC + GYRO)
    private val result = Collections.synchronizedList(mutableListOf<JSONObject>())

    private val coroutineScope = CoroutineScope(Dispatchers.Default)



    // ======= buffers antigos mantidos (se forem usados pela sua classificação) =======
    private val ax = mutableListOf<Float>()
    private val ay = mutableListOf<Float>()
    private val az = mutableListOf<Float>()
    private val lx = mutableListOf<Float>()
    private val ly = mutableListOf<Float>()
    private val lz = mutableListOf<Float>()
    private val gx = mutableListOf<Float>()
    private val gy = mutableListOf<Float>()
    private val gz = mutableListOf<Float>()
    private val ma = mutableListOf<Float>()
    private val ml = mutableListOf<Float>()
    private val mg = mutableListOf<Float>()
    private var results: FloatArray? = null
    private val N_SAMPLES = 100


    private var elapsedMs = 0L
    private var timerJob: Job? = null
    private var paused = false

    private var repetitions = 0
    private var lastRepTimestamp = 0L
    private var lastStandPeakTs = 0L
    private var sittingLikely = true

    private var lastGyroY = 0.0
    private var lastLinearZ = 0.0

    private var timeDisplay = ""
    private lateinit var typeTeste: String

    // ========= TTS =========
    private var textToSpeech: TextToSpeech? = null
    private var ttsReady = false
    private var lastSpokenSecond: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@Timer, FtstsInstruction::class.java))
                finish()
            }
        })

        timerTextView = findViewById(R.id.timerTextView)
        title = findViewById(R.id.title)
        pauseButton = findViewById(R.id.pauseButton)
        timerTextView.text = "00:30"

        typeTeste = "30CST"
        title.text = "30CST"

        val refreshBtn = findViewById<ImageView>(R.id.refresh)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        linearAceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)




        //   - enquanto rodando: "Pausar" -> pausa o teste
        //   - pausado: "Enviar" -> envia ou navega
        pauseButton.text = "Pausar"
        pauseButton.isEnabled = true


        pauseButton.setOnClickListener {

            // Se está rodando → PAUSAR
            if (!paused && running.get()) {
                paused = true
                stopTimerAndSensors()
                speak("Teste pausado")
                pauseButton.text = "Enviar"
                return@setOnClickListener
            }

            // Se está pausado → ENVIAR
            if (paused) {
                if (RoleHelpers.isHealthProfessional()) {
                    // profissional → enviar para a API
                    postData()

                } else {
                    // paciente → vai direto para resultado
                    val intent = Intent(this@Timer, TestResult::class.java)
                    intent.putExtra("time", timeDisplay.safeTime())
                    intent.putExtra("repetitions", repetitions)
                    intent.putExtra("teste", typeTeste)
                    startActivity(intent)
                    finish()
                }
            }
        }


        val refreshAction = {
            resetTimer()
        }

        refreshBtn.setOnClickListener { refreshAction() }


        textToSpeech = TextToSpeech(this, this)

        startTimerAndSensors() // inicia contando de 00:00 para cima
    }

    // ====== TTS ======
    override fun onInit(status: Int) {
        ttsReady = status == TextToSpeech.SUCCESS
        if (ttsReady) textToSpeech?.language = Locale("pt", "BR")
    }

    private fun speak(text: String) {
        if (ttsReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.nanoTime().toString())
        }
    }

    // ====== Timer/Sensores ======
    private fun startTimerAndSensors() {
        startTime = System.currentTimeMillis()
        elapsedMs = 0L
        lastSpokenSecond = -1
        paused = false
        running.set(true)

        startCountdown()
        startSensorCollection()
    }

    private fun stopTimerAndSensors() {
        running.set(false)
        sensorManager.unregisterListener(this)
        timerJob?.cancel()
    }

    private fun resetTimer() {
        stopTimerAndSensors()

        result.clear()
        accelQueue.clear()
        gyroQueue.clear()
        linearQueue.clear()

        repetitions = 0
        lastRepTimestamp = 0L
        lastStandPeakTs = 0L
        sittingLikely = true
        lastSpokenSecond = -1
        elapsedMs = 0L
        paused = false
        timerTextView.text = "00:30"
        pauseButton.text = "Pausar"

        finish()
        startActivity(Intent(this@Timer, FtstsInstruction::class.java))
    }

    private fun startCountdown() {
        elapsedMs = 30_000L

        timerJob = coroutineScope.launch(Dispatchers.Main) {
            while (running.get() && elapsedMs >= 0) {

                timerTextView.text = formatTime(elapsedMs)
                timeDisplay = formatTime(elapsedMs)

                delay(200)
                elapsedMs -= 200

                if (elapsedMs <= 0) {
                    // zera a contagem
                    timerTextView.text = "00:00"
                    timeDisplay = "00:00"

                    running.set(false)
                    stopTimerAndSensors()
                    speak("Teste concluído")

                    // muda para modo ENVIAR
                    paused = true
                    pauseButton.text = "Enviar"
                    pauseButton.isEnabled = true

                    // NÃO enviar automaticamente
                    return@launch
                }
            }
        }
    }


    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Long): String {
        val clamped = if (ms < 0) 0 else ms
        val seconds = (clamped / 1000) % 60
        val minutes = (clamped / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun startSensorCollection() {
        sensorManager.registerListener(this, accelerometer, frequency)
        sensorManager.registerListener(this, gyroscope, frequency)
        sensorManager.registerListener(this, linearAceleration, frequency)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!running.get()) return

        val timestampStr = formatTimestamp()
        val sensorData = floatArrayOf(event.values[0], event.values[1], event.values[2])

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
                lastGyroY = sensorData[1].toDouble()
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                linearQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })
                lastLinearZ = sensorData[2].toDouble()
            }
        }

        tryMergeSensorData()

    }



    private fun tryMergeSensorData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        while (true) {
            val acc = accelQueue.peek() ?: return
            val gyr = gyroQueue.peek() ?: return

            val accDateStr = acc.optString("time", "")
            val accDate = sdf.parse(accDateStr)
            if (accDate == null) { accelQueue.poll(); continue }

            val gyrDateStr = gyr.optString("time", "")
            val gyrDate = sdf.parse(gyrDateStr)
            if (gyrDate == null) { gyroQueue.poll(); continue }

            val diff = kotlin.math.abs(accDate.time - gyrDate.time)

            when {
                diff <= 25 -> {
                    val merged = JSONObject().apply {
                        put("timestamp", accDateStr)                // campo que o backend valida
                        put("accel_x", acc.optDouble("x"))
                        put("accel_y", acc.optDouble("y"))
                        put("accel_z", acc.optDouble("z"))
                        put("gyro_x",  gyr.optDouble("x"))
                        put("gyro_y",  gyr.optDouble("y"))
                        put("gyro_z",  gyr.optDouble("z"))
                    }

                    accelQueue.poll()
                    gyroQueue.poll()
                    result.add(merged)
                }
                accDate.before(gyrDate) -> accelQueue.poll()
                else -> gyroQueue.poll()
            }
        }
    }

    private fun formatTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    private fun isoUtc(dateMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(dateMillis))
    }

    private fun postData() {
        val api = RetrofitClient.instancePessoasAPI

        // 🔹 Monta sensorData com pontos válidos
        val sensorList: List<Map<String, Any>> = result.map { json ->
            mapOf(
                "timestamp" to json.optString("timestamp", ""),
                "accel_x"   to json.optDouble("accel_x", 0.0),
                "accel_y"   to json.optDouble("accel_y", 0.0),
                "accel_z"   to json.optDouble("accel_z", 0.0),
                "gyro_x"    to json.optDouble("gyro_x", 0.0),
                "gyro_y"    to json.optDouble("gyro_y", 0.0),
                "gyro_z"    to json.optDouble("gyro_z", 0.0)
            )
        }

        // ★ totalTime agora é o tempo decorrido do 5TSTS
        val total = timeDisplay.safeTime()

        val teste = Teste(
            type = "FTSTS",
            patientId = PacienteManager.uuid.toString(),
            healthProfessionalId = SessionManager.user?.id.toString(),
            healthcareUnitId = intent.getStringExtra("id_unidade"),
            date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
            totalTime = total,                    // ★ usa o tempo real
            sensorData = sensorList,
            time_init = isoUtc(startTime),
            time_end = isoUtc(System.currentTimeMillis())

        )

        println(teste.sensorData)

        if (RoleHelpers.isHealthProfessional()) {
            val call = api.postTestes(teste, "Bearer ${SessionManager.token}")
            call.enqueue(object : retrofit2.Callback<Teste> {
                override fun onResponse(call: Call<Teste>, response: Response<Teste>) {
                    if (response.isSuccessful) {
                        speak(getString(R.string.success_test_sent))

                        val intent = Intent(this@Timer, TestResult::class.java)
                        intent.putExtra("time", total)
                        intent.putExtra("repetitions", repetitions)
                        intent.putExtra("teste", typeTeste)
                        startActivity(intent)
                        finish()
                    } else {
                        val msg = ErrorMessages.forHttpStatus(this@Timer, response.code())
                        speak(msg)
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Teste>, t: Throwable) {
                    Log.e("Erro", "Falha ao enviar o teste", t)
                    val msg = getString(R.string.error_network)
                    speak(msg)
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Para pacientes, também navega para TestResult
            val intent = Intent(this@Timer, TestResult::class.java)
            intent.putExtra("time", total)
            intent.putExtra("repetitions", repetitions)
            intent.putExtra("teste", typeTeste)
            startActivity(intent)
            finish()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    // útil para debug/export
    private fun convertJsonToCsv(json: JSONObject): String {
        fun getS(vararg keys: String): String {
            for (k in keys) if (json.has(k) && !json.isNull(k)) return json.optString(k)
            return ""
        }
        fun getD(vararg keys: String): Double {
            for (k in keys) if (json.has(k) && !json.isNull(k)) return json.optDouble(k, 0.0)
            return 0.0
        }

        val ts  = getS("timestamp", "time")
        val ax  = getD("accel_x", "x")
        val ay  = getD("accel_y", "y")
        val az  = getD("accel_z", "z")
        val gx  = getD("gyro_x", "x")
        val gy  = getD("gyro_y", "y")
        val gz  = getD("gyro_z", "z")

        return "\n$ts,$ax,$ay,$az,$gx,$gy,$gz"
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    // ===== Helpers =====
    private fun String.safeTime(): String {
        // garante "MM:SS" mesmo se timeDisplay estiver vazio
        return if (this.matches(Regex("\\d{2}:\\d{2}"))) this else "00:00"
    }
}
