package com.ufpr.equilibrium.feature_teste

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.HomePaciente
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.domain.risk.RiskClassifier
import com.ufpr.equilibrium.domain.risk.RiskLevel


class TestResult : AppCompatActivity() {
    private lateinit var result: String
    private  lateinit var timeResult : TextView;
    private lateinit var textResult: TextView;
    private lateinit var backButton: Button;


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test_result)

        timeResult = findViewById(R.id.time);
        textResult = findViewById(R.id.textResult);
        timeResult.text = intent.getStringExtra("time");
        backButton = findViewById(R.id.back)

        classifyTest(intent.getStringExtra("teste"), timeResult.text.toString())

        backButton.setOnClickListener {

            val intent = if (SessionManager.user?.role == "PATIENT")
                Intent(this@TestResult, HomePaciente::class.java) else
                Intent(this@TestResult, HomeProfissional::class.java)

            startActivity(intent)
        }
    }

    private fun classifyTest(type: String?, time: String?) {
        if (time.isNullOrEmpty()) return

        try {
            val parts = time.split(":")
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()
            val timeInSeconds = minutes * 60 + seconds

            result = when (RiskClassifier.classify(type, timeInSeconds)) {
                RiskLevel.LOW -> "Baixo risco de queda"
                RiskLevel.MODERATE -> "Risco Moderado de queda"
                RiskLevel.HIGH -> "Alto risco de quedas"
                RiskLevel.UNKNOWN -> "Resultado não classificado"
            }

            textResult.text = result

        } catch (e: Exception) {
            e.printStackTrace()
            textResult.text = "Erro ao interpretar o tempo"
        }
    }


}