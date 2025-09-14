package com.ufpr.equilibrium.feature_teste

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
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import com.ufpr.equilibrium.network.RetrofitClient
import com.ufpr.equilibrium.network.Teste
import com.ufpr.equilibrium.utils.PacienteManager
import com.ufpr.equilibrium.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

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
    private val frequency = 1_000_000 / 25

    private val accelQueue = ConcurrentLinkedQueue<JSONObject>()
    private val gyroQueue = ConcurrentLinkedQueue<JSONObject>()
    private val linearQueue = ConcurrentLinkedQueue<JSONObject>()

    private val result = Collections.synchronizedList(mutableListOf<JSONObject>())

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var state = "WAITING_TO_STAND"

    private var lastGyroY = 0.0
    private var lastGyroZ: Double = 0.0
    private var lastGyroX: Double = 0.0;
    private var lastGyroTimestamp: Long = 0

    private var lastAccelX: Double = 0.0
    private var lastAccelY: Double = 0.0
    private var lastAccelZ: Double = 0.0
    private var lastAccelTimestamp: Long = 0

    private var lastLinearX: Double = 0.0
    private var lastLinearY: Double = 0.0
    private var lastLinearZ: Double = 0.0
    private var lastLinearTimestamp: Long = 0

    private var ax = mutableListOf<Float>()
    private var ay = mutableListOf<Float>()
    private var az = mutableListOf<Float>()

    private var lx = mutableListOf<Float>()
    private var ly = mutableListOf<Float>()
    private var lz = mutableListOf<Float>()

    private var gx = mutableListOf<Float>()
    private var gy = mutableListOf<Float>()
    private var gz = mutableListOf<Float>()

    private var ma = mutableListOf<Float>()
    private var ml = mutableListOf<Float>()
    private var mg = mutableListOf<Float>()

    private var results: FloatArray? = null

    private lateinit var classifier: HARClassifier

    private var cycleCount = 0

    val maxCycles = 5

    val N_SAMPLES = 100

    private var time = "";

    private lateinit var typeTeste:String
    private val sensorWindow = mutableListOf<FloatArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (intent.getStringExtra("teste") == "FTSTS") {
                    startActivity(Intent(this@Timer, FtstsInstruction::class.java))
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

        classifier = HARClassifier(applicationContext)

        pauseButton.setOnClickListener {

            if (pauseButton.text == "Enviar") {

                if (SessionManager.user?.role == "HEALTH_PROFESSIONAL") {

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

            if (intent.getStringExtra("teste")?.uppercase() == "FTSTS") {

                startActivity(Intent(this@Timer, FtstsInstruction::class.java))
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
        sensorManager.registerListener(this, linearAceleration, frequency)
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
//
                lastAccelX = sensorData[0].toDouble()
                lastAccelY = sensorData[1].toDouble()
                lastAccelZ = sensorData[2].toDouble()
                lastAccelTimestamp = timestampLong

                ax.add(event.values[0]);
                ay.add(event.values[1]);
                az.add(event.values[2]);

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

               gx.add(event.values[0]);
                gy.add(event.values[1]);
               gz.add(event.values[2]);

                lastGyroTimestamp = timestampLong

                sensorWindow.add(sensorData)
            }

            Sensor.TYPE_LINEAR_ACCELERATION -> {
                linearQueue.add(JSONObject().apply {

                    put("time", timestampStr)
                    put("x", sensorData[0].toDouble())
                    put("y", sensorData[1].toDouble())
                    put("z", sensorData[2].toDouble())

                })

                lastLinearZ = sensorData[2].toDouble();
                lastLinearY = sensorData[1].toDouble();
                lastLinearX = sensorData[0].toDouble();

                lastLinearTimestamp = timestampLong

                //lx.add(event.values[0]);
                //ly.add(event.values[1]);
                //lz.add(event.values[2]);

            }
        }

        val testType = intent.getStringExtra("teste")?.lowercase()

       // activityPrediction()

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
            if (accDate == null) {
                accelQueue.poll()
                continue
            }

            val gyrDateStr = gyr.optString("time", "")
            val gyrDate = sdf.parse(gyrDateStr)

            if (gyrDate == null) {
                gyroQueue.poll()
                continue
            }

            val diff = abs(accDate.time - gyrDate.time)

            when {
                diff <= 25 -> {
                    val merged = JSONObject().apply {
                        put("timestamp", accDateStr)
                        put("accel_x", acc.optDouble("x"))
                        put("accel_y", acc.optDouble("y"))
                        put("accel_z", acc.optDouble("z"))
                        put("gyro_x", gyr.optDouble("x"))
                        put("gyro_y", gyr.optDouble("y"))
                        put("gyro_z", gyr.optDouble("z"))
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



    private fun activityPrediction() {

        val data = mutableListOf<Float>()

        if (listOf(ax, ay, az, lx, ly, lz, gx, gy, gz).all { it.size >= N_SAMPLES }
        ) {

            for (i in 0 until N_SAMPLES) {
                val maValue = sqrt(ax[i].toDouble().pow(2) + ay[i].toDouble().pow(2) + az[i].toDouble().pow(2))
                val mlValue = sqrt(lx[i].toDouble().pow(2) + ly[i].toDouble().pow(2) + lz[i].toDouble().pow(2))
                val mgValue = sqrt(gx[i].toDouble().pow(2) + gy[i].toDouble().pow(2) + gz[i].toDouble().pow(2))

                ma.add(maValue.toFloat())
                ml.add(mlValue.toFloat())
                mg.add(mgValue.toFloat())
            }

            data.addAll(ax.subList(0, N_SAMPLES))
            data.addAll(ay.subList(0, N_SAMPLES))
            data.addAll(az.subList(0, N_SAMPLES))

            data.addAll(lx.subList(0, N_SAMPLES))
            data.addAll(ly.subList(0, N_SAMPLES))
            data.addAll(lz.subList(0, N_SAMPLES))

            data.addAll(gx.subList(0, N_SAMPLES))
            data.addAll(gy.subList(0, N_SAMPLES))
            data.addAll(gz.subList(0, N_SAMPLES))

            data.addAll(ma.subList(0, N_SAMPLES))
            data.addAll(ml.subList(0, N_SAMPLES))
            data.addAll(mg.subList(0, N_SAMPLES))

            results = classifier.predictProbabilities(data.toFloatArray())

            results?.let { r ->
                when {
                    r[3] > r[4] -> println("sitting")
                    r[4] > r[3] -> println("standing")
                    r[6] > r[3] && r[6] > r[4] -> println("walking")
                }
            }

            var max = -1f

            for (i in results!!.indices) {
                if (results!![i] > max) {

                    max = results!![i]
                }
            }



            ax.clear()
            ay.clear()
            az.clear()
            lx.clear()
            ly.clear()
            lz.clear()
            gx.clear()
            gy.clear()
            gz.clear()
            ma.clear()
            ml.clear()
            mg.clear()
        }

    }

    private fun detectCycle5tsts() {

            when (state) {

                "WAITING_TO_STAND" -> {

                }

                "STANDING" -> {

                }
            }

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

        val teste = Teste (

            type = intent.getStringExtra("teste"),
            patientId = PacienteManager.uuid.toString(),
            healthProfessionalId = SessionManager.user?.id.toString(),
            healthcareUnitId = intent.getStringExtra("id_unidade"),
            date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
            totalTime = time,
            sensorData = sensorList,
            time_init = isoUtc(startTime),
            time_end = isoUtc(System.currentTimeMillis())
        )

        if (SessionManager.user?.role == "HEALTH_PROFESSIONAL") {
            val call = api.postTestes(teste, "Bearer " + SessionManager.token.toString());

            call.enqueue(object : retrofit2.Callback<Teste> {
                override fun onResponse(call: Call<Teste>, response: Response<Teste>) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            applicationContext,
                            "Teste enviado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        intent = Intent(this@Timer, TestResult::class.java)

                        intent.putExtra("time", time);
                        intent.putExtra("teste", typeTeste)

                        startActivity(intent)
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

    private fun sendData() {
        try {
            stopTimerAndSensors() // garante que nada mais entra em 'result' enquanto salva

            val folder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (folder != null && !folder.exists()) folder.mkdirs()

            val csvFile = File(folder, "sensor_data.csv")

            FileWriter(csvFile, /* append = */ false).use { writer ->
                writer.append("timestamp,accel_x,accel_y,accel_z,gyro_x,gyro_y,gyro_z\n")
                synchronized(result) {
                    for (json in result) writer.append(convertJsonToCsv(json))
                    result.clear()
                }
                writer.flush()
            }

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
        val ax  = getD("accel_x", "accel_x", "x")
        val ay  = getD("accel_y", "accel_y", "y")
        val az  = getD("accel_z", "accel_z", "z")
        val gx  = getD("gyro_x", "gx", "wx")
        val gy  = getD("gyro_y", "gy", "wy")
        val gz  = getD("gyro_z", "gz", "wz")

        return "\n$ts,$ax,$ay,$az,$gx,$gy,$gz"
    }

    private fun isoUtc(dateMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(dateMillis))
    }
}