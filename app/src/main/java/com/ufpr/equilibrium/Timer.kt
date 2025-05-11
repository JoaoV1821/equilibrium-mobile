package com.ufpr.equilibrium

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

private enum class TugPhase {
    INIT, STAND_UP, WALK_FORWARD, TURN, WALK_BACK, SIT_DOWN, DONE
}

class Timer : AppCompatActivity(), SensorEventListener {

    private lateinit var timerTextView: TextView
    private lateinit var title: TextView
    private lateinit var pauseButton: Button
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private val running = AtomicBoolean(false)
    private var startTime: Long = 0
    private val frequency = SensorManager.SENSOR_DELAY_GAME

    private val accelQueue = ConcurrentLinkedQueue<JSONObject>()
    private val gyroQueue = ConcurrentLinkedQueue<JSONObject>()
    private val result = Collections.synchronizedList(mutableListOf<JSONObject>())

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var cycleCount = 0
    private val peakThreshold = 1.0
    private var peakDetected = false
    private val maxCycles = 5

    private var tugPhase = TugPhase.INIT
    private var lastGyroY = 0.0
    private var lastAccelZ = 0.0
    private var turning = false
    private var walkBackStartTime = 0L
    private var sitStartTime = 0L

    private var time = "";

    private lateinit var typeTeste:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (intent.getStringExtra("teste") == "5TSTS") {
                    startActivity(Intent(this@Timer, FtstsInstruction::class.java))
                    finish()

                } else {
                    startActivity(Intent(this@Timer, TugInstruction::class.java))
                    finish()
                }

            }
        })

        timerTextView = findViewById(R.id.timerTextView)
        title = findViewById(R.id.title)
        pauseButton = findViewById(R.id.pauseButton)
        typeTeste = intent.getStringExtra("teste").toString();

        title.text = intent.getStringExtra("teste")?.uppercase();

        val refreshBtn = findViewById<ImageView>(R.id.refresh)
        val arrowBack = findViewById<ImageView>(R.id.arrow_back)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        pauseButton.setOnClickListener {

            if (pauseButton.text == "Enviar") {

                if (SessionManager.user?.profile == "healthProfessional") {
                    postData();

                } else {

                    intent = Intent(this@Timer, TestResult::class.java)

                    intent.putExtra("time", time);
                    intent.putExtra("teste", typeTeste)

                    startActivity(intent);
                }

            } else {
                toggleTimer()
            }
        }

        refreshBtn.setOnClickListener { resetTimer() }

        arrowBack.setOnClickListener {

            if (intent.getStringExtra("teste")?.uppercase() == "5TSTS") {
                startActivity(Intent(this@Timer, FtstsInstruction::class.java))
                finish()

            } else {
                startActivity(Intent(this@Timer, TugInstruction::class.java))
                finish()
            }
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
        sensorManager.unregisterListener(this)
    }

    private fun toggleTimer() {
        if (running.get()) stopTimerAndSensors()
        pauseButton.text = if (running.get()) "Pausar" else "Enviar"
    }

    private fun resetTimer() {

        stopTimerAndSensors()
        result.clear()
        accelQueue.clear()
        gyroQueue.clear()
        startTimerAndSensors()
        pauseButton.text = "Pausar"
    }

    private fun startTimerTask() {
        coroutineScope.launch(Dispatchers.Main) {

            while (running.get()) {
                val elapsed = System.currentTimeMillis() - startTime
                timerTextView.text = formatTime(elapsed)
                time = formatTime(elapsed)
                delay(1000)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun startSensorCollection() {
        sensorManager.registerListener(this, accelerometer, frequency)
        sensorManager.registerListener(this, gyroscope, frequency)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!running.get()) return

        val timestamp = formatTimestamp()

        val json = JSONObject().apply {

            put("tempo", timestamp)
            put("x", event.values[0].toDouble())
            put("y", event.values[1].toDouble())
            put("z", event.values[2].toDouble())
        }

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelQueue.add(json)

            Sensor.TYPE_GYROSCOPE -> {
                gyroQueue.add(json)

                if (intent.getStringExtra("teste")?.lowercase() == "5tsts") {
                    detectCycle5tsts(event.values[0].toDouble())

            } else {
                detectCycleTug()

                }
            }
        }

        tryMergeSensorData()
    }

    private fun tryMergeSensorData() {
        val acc = accelQueue.peek() ?: return
        val gyro = gyroQueue.peek() ?: return

        val accTime = acc.getString("tempo")
        val gyroTime = gyro.getString("tempo")

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val accDate = sdf.parse(accTime)
        val gyroDate = sdf.parse(gyroTime)

        val diff = abs(accDate.time - gyroDate.time)

        if (diff <= 20) {
            accelQueue.poll()
            gyroQueue.poll()

            val merged = JSONObject().apply {

                put("tempo", acc.getString("tempo"))
                put("accel_x", acc.getDouble("x"))
                put("accel_y", acc.getDouble("y"))
                put("accel_z", acc.getDouble("z"))
                put("gyro_x", gyro.getDouble("x"))
                put("gyro_y", gyro.getDouble("y"))
                put("gyro_z", gyro.getDouble("z"))
            }

            result.add(merged)

        } else if (accDate != null) {

            if (accDate.before(gyroDate)) {
                accelQueue.poll()

            } else {
                gyroQueue.poll()
            }
        }
    }

    private fun detectCycle5tsts(gyroX: Double) {
        if (gyroX > peakThreshold && !peakDetected) {

            peakDetected = true
            cycleCount++

            if (cycleCount / 2 >= maxCycles) {
                stopTimerAndSensors()
                pauseButton.text = "Enviar"
            }

        } else if (gyroX < 0.2) {
            peakDetected = false
        }
    }

    private fun detectCycleTug() {
        val latest = result.lastOrNull() ?: return
        val currentTime = System.currentTimeMillis()

        val accelZ = latest.optDouble("accel_z", 0.0)
        val gyroY = latest.optDouble("gyro_y", 0.0)

        when (tugPhase) {
            TugPhase.INIT -> {
                if (accelZ < -1.0) {
                    tugPhase = TugPhase.STAND_UP
                    Log.d("TUG", "Fase: Levantando-se")
                }
            }

            TugPhase.STAND_UP -> {
                if (abs(accelZ - lastAccelZ) > 0.5) {
                    tugPhase = TugPhase.WALK_FORWARD
                    Log.d("TUG", "Fase: Caminhando para frente")
                }
            }

            TugPhase.WALK_FORWARD -> {
                if (abs(gyroY) > 1.0 && !turning) {
                    turning = true
                    tugPhase = TugPhase.TURN
                    Log.d("TUG", "Fase: Virando")
                }
            }

            TugPhase.TURN -> {
                if (abs(gyroY) < 0.3 && turning) {
                    turning = false
                    tugPhase = TugPhase.WALK_BACK
                    walkBackStartTime = currentTime
                    Log.d("TUG", "Fase: Caminhando de volta")
                }
            }

            TugPhase.WALK_BACK -> {
                val walkDuration = currentTime - walkBackStartTime

                if (walkDuration > 2000 && accelZ > 1.0) { // mínimo 2 segundos de caminhada antes de aceitar "sentar"
                    tugPhase = TugPhase.SIT_DOWN
                    sitStartTime = currentTime


                    Log.d("TUG", "Fase: Sentando")
                }
            }

            TugPhase.SIT_DOWN
            -> {
                val sitDuration = currentTime - sitStartTime

                if (sitDuration > 1000 && (abs(accelZ) < 0.2 && (abs(gyroY) < 0.2) || abs(accelZ) < -0.2 && abs(gyroY) < -0.2))  {
                    tugPhase = TugPhase.DONE

                    stopTimerAndSensors()

                    pauseButton.text = "Enviar"
                    Log.d("TUG", "Fase: Finalizado")
                }
            }

            TugPhase.DONE -> {
                // Nada a fazer
            }
        }

        lastAccelZ = accelZ
        lastGyroY = gyroY
    }

    private fun formatTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    private  fun postData() {
        val api = RetrofitClient.instancePessoasAPI

        val sensorList = result.map { json ->
            val map = mutableMapOf<String, Any>()

            json.keys().forEach { key ->
                map[key] = json.get(key)
            }
            map
        }

        println(intent.getStringExtra("teste"))
        println(PacienteManager.cpf);
        println(SessionManager.user?.cpf)
        println(sensorList)

        println("teste unidade")
        println(intent.getStringExtra("id_unidade"))

        val teste = Teste(
            type = intent.getStringExtra("teste"),
            cpfPatient = PacienteManager.cpf,
            cpfHealthProfessional = SessionManager.user?.cpf,
            id_healthUnit = intent.getStringExtra("id_unidade")?.toInt(),
            date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
            totalTime = time,
            dadosSensor = sensorList
        )

        if (SessionManager.user?.profile== "healthProfessional") {
            val call = api.postTestes(teste);

            call.enqueue(object : retrofit2.Callback<Teste> {
                override fun onResponse(call: Call<Teste>, response: Response<Teste>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Teste enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Teste>, t: Throwable) {
                    Log.e("Erro", "Falha ao enviar o teste", t)
                }
            })

        } else {
            println("Teste realizado por paciente")
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



