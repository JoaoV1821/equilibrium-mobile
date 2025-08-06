package com.ufpr.equilibrium.feature_teste

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import com.ufpr.equilibrium.utils.PacienteManager
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.network.RetrofitClient
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.feature_tug.TugInstruction
import com.ufpr.equilibrium.network.Teste
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import org.tensorflow.lite.flex.FlexDelegate
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
    private var linearAceleration: Sensor? = null

    private val running = AtomicBoolean(false)
    private var startTime: Long = 0
    private val frequency = SensorManager.SENSOR_DELAY_GAME

    private val accelQueue = ConcurrentLinkedQueue<JSONObject>()
    private val gyroQueue = ConcurrentLinkedQueue<JSONObject>()
    private val result = Collections.synchronizedList(mutableListOf<JSONObject>())

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var tugPhase = TugPhase.INIT
    private var lastGyroY = 0.0
    private var lastGyroZ: Double = 0.0
    private var lastGyroX: Double = 0.0;
    private var lastGyroTimestamp: Long = 0
    private var walkBackStartTime = 0L
    private var sitStartTime = 0L

    private var lastAccelX: Double = 0.0
    private var lastAccelY: Double = 0.0
    private var lastAccelZ: Double = 0.0
    private var lastAccelTimestamp: Long = 0

    private var state = "WAITING_TO_STAND"
    private var cycleCount = 0

    private var lastPredictedClass = -1
    private var stableClassCount = 0
    private val STABLE_THRESHOLD = 3

    val maxCycles = 5

    private var time = "";

    private lateinit var typeTeste:String

    private val LABELS = arrayOf("Downstairs",

        "Jogging", "Sitting", "Standing", "Upstairs", "Walking"
    )

    private val gyroWindow = mutableListOf<Double>()
    private val accelWindow = mutableListOf<Double>()

    private val WINDOW_SIZE = 5
    private val MIN_TRANSITION_INTERVAL = 750 // 1 segundo em milissegundos
    private var lastTransitionTime = System.currentTimeMillis()

    // Fora do método: definições necessárias
    private val sensorWindow = mutableListOf<FloatArray>()  // Cada item = [ax, ay, az]
    private val WIDOW_SIZE = 200
    private lateinit var interpreter: Interpreter
    private val tfInput = Array(1) { Array(WIDOW_SIZE) { FloatArray(3) } }
    private val tfOutput = Array(1) { FloatArray(6) } // supondo 6 classes de saída

    // Inicialize o interpreter no onCreate ou similar

    private fun initInterpreter() {
        val model = LoadModelFile(assets, "har_model.tflite")
        val options = Interpreter.Options()
        options.addDelegate(FlexDelegate())
        interpreter = Interpreter(model, options)

    }

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
        linearAceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

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
        initInterpreter()
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

        val teste = intent.getStringExtra("teste")
        val unidade = intent.getStringExtra("id_unidade")

        val newIntent = Intent(this@Timer, Contagem::class.java);

        newIntent.putExtra("teste", teste);
        newIntent.putExtra("id_unidade", unidade)

        startActivity(newIntent)
        finish()

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

        val timestampStr = formatTimestamp()
        val timestampLong = System.currentTimeMillis()

        val sensorData = floatArrayOf(
            event.values[0],
            event.values[1],
            event.values[2]
        )

        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {

                accelQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                })

                lastAccelX = sensorData[0].toDouble()
                lastAccelY = sensorData[1].toDouble()
                lastAccelZ = sensorData[2].toDouble()
                lastAccelTimestamp = timestampLong

                sensorWindow.add(sensorData)

            }

            Sensor.TYPE_GYROSCOPE -> {

                gyroQueue.add(JSONObject().apply {
                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())
                });

                lastGyroZ = sensorData[2].toDouble()
                lastGyroY = sensorData[1].toDouble();
                lastGyroX = sensorData[0].toDouble()

                lastAccelTimestamp = timestampLong

                val testType = intent.getStringExtra("teste")?.lowercase()

                if (testType == "5tsts") {
                    detectCycle5tsts(lastGyroX, lastGyroY, lastGyroZ)

                } else if (testType == "tug") {
                    detectCycleTug()
                }
            }

            Sensor.TYPE_LINEAR_ACCELERATION -> {

            }
        }

        tryMergeSensorData()
    }


    private fun tryMergeSensorData() {
        val acc = accelQueue.peek() ?: return
        val gyro = gyroQueue.peek() ?: return

        val accTime = acc.getString("time")
        val gyroTime = gyro.getString("time")

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

                put("time", acc.getString("time"))
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

    private fun smooth(values: MutableList<Double>, newValue: Double): Double {
        values.add(newValue)

        if (values.size > WINDOW_SIZE) {
            values.removeAt(0)
        }
        return values.average()
    }

    private fun detectCycle5tsts(accelXRaw: Double, accelYRaw:Double, accelZRaw: Double) {
        val now = System.currentTimeMillis()
        if (now - lastTransitionTime < MIN_TRANSITION_INTERVAL) return

        val inputVector = floatArrayOf(accelXRaw.toFloat(), accelYRaw.toFloat(), accelZRaw.toFloat())

        sensorWindow.add(inputVector)

        if (sensorWindow.size >= WIDOW_SIZE) {
            for (i in 0 until WIDOW_SIZE) {
                tfInput[0][i] = sensorWindow[i]
            }

            interpreter.run(tfInput, tfOutput)

            val predictedClass = tfOutput[0].indices.maxByOrNull { tfOutput[0][it] } ?: -1

            Log.d("TFLITE", "Classe prevista: $predictedClass - Confiança: ${tfOutput[0][predictedClass]}, ${LABELS[predictedClass]}",

            )

            // Slide da janela
            sensorWindow.subList(0, 50).clear()
            // Atualizar lógica baseada na predição

            when (state) {

                "WAITING_TO_STAND" -> {
                    if (predictedClass == 3) { // 1 = Levantar, de acordo com o modelo
                        Log.d("5TSTS", "Levantou")
                        state = "STANDING"
                        lastTransitionTime = now
                    }
                }

                "STANDING" -> {
                    if (predictedClass == 2) { // 0 = Sentar, de acordo com o modelo
                        Log.d("5TSTS", "Sentou")
                        cycleCount++

                        Log.d("5TSTS", "Ciclo completo: $cycleCount")

                        if (cycleCount >= maxCycles) {
                            stopTimerAndSensors()

                            sensorWindow.clear()

                            for (i in tfInput[0].indices) {
                                tfInput[0][i].fill(0f)
                            }

                            pauseButton.text = "Enviar"
                        }

                        state = "WAITING_TO_STAND"
                        lastTransitionTime = now
                    }
                }
            }
        }
    }


    private fun detectCycleTug() {
        val now = System.currentTimeMillis()
        val latest = result.lastOrNull() ?: return

        val accelZ = latest.optDouble("accel_z", 0.0).toFloat()
        val gyroY = latest.optDouble("gyro_y", 0.0).toFloat()
        val inputVector = floatArrayOf(accelZ, gyroY, 0f) // pode usar gyroZ se disponível
        sensorWindow.add(inputVector)

        if (sensorWindow.size >= WIDOW_SIZE) {
            for (i in 0 until WIDOW_SIZE) {
                tfInput[0][i] = sensorWindow[i]
            }

            interpreter.run(tfInput, tfOutput)

            val predictedClass = tfOutput[0].indices.maxByOrNull { tfOutput[0][it] } ?: -1
            Log.d("TFLITE", "Classe prevista: $predictedClass - Confiança: ${tfOutput[0][predictedClass]}")

            sensorWindow.subList(0, 50).clear()

            when (tugPhase) {
                TugPhase.INIT -> {
                    if (predictedClass == 1) { // 1 = Levantando
                        tugPhase = TugPhase.STAND_UP
                        Log.d("TUG", "Fase: Levantando-se")
                    }
                }

                TugPhase.STAND_UP -> {
                    if (predictedClass == 2) { // 2 = Caminhando
                        tugPhase = TugPhase.WALK_FORWARD
                        Log.d("TUG", "Fase: Caminhando para frente")
                    }
                }

                TugPhase.WALK_FORWARD -> {
                    if (predictedClass == 3) { // 3 = Virando
                        tugPhase = TugPhase.TURN
                        Log.d("TUG", "Fase: Virando")
                    }
                }

                TugPhase.TURN -> {
                    if (predictedClass == 2) { // Caminhando de volta
                        tugPhase = TugPhase.WALK_BACK
                        walkBackStartTime = now
                        Log.d("TUG", "Fase: Caminhando de volta")
                    }
                }

                TugPhase.WALK_BACK -> {
                    if (predictedClass == 0) { // 0 = Sentando
                        tugPhase = TugPhase.SIT_DOWN
                        sitStartTime = now
                        Log.d("TUG", "Fase: Sentando")
                    }
                }

                TugPhase.SIT_DOWN -> {
                    if (predictedClass == 4) { // 4 = Sentado
                        tugPhase = TugPhase.DONE
                        stopTimerAndSensors()

                        sensorWindow.clear()
                        tfInput.forEach { it.fill(FloatArray(3)) }

                        pauseButton.text = "Enviar"
                        Log.d("TUG", "Fase: Finalizado")
                    }
                }

                TugPhase.DONE -> {
                    // Nada a fazer
                }
            }
        }

        lastAccelZ = accelZ.toDouble()
        lastGyroY = gyroY.toDouble()
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
            sensorData = sensorList
        )

        if (SessionManager.user?.profile== "healthProfessional") {
            val call = api.postTestes(teste);

            call.enqueue(object : retrofit2.Callback<Teste> {
                override fun onResponse(call: Call<Teste>, response: Response<Teste>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Teste enviado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
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

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    /* private fun sendData() {
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
    //private fun convertJsonToCsv(jsonObject: JSONObject): String {
        return "\n${jsonObject.optString("timestamp")},${jsonObject.optDouble("acc_x", 0.0)},${jsonObject.optDouble("acc_y", 0.0)},${jsonObject.optDouble("acc_z", 0.0)},${jsonObject.optDouble("gyro_x", 0.0)},${jsonObject.optDouble("gyro_y", 0.0)},${jsonObject.optDouble("gyro_z", 0.0)}"
    }
*/
}