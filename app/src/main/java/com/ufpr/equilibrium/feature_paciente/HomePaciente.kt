package com.ufpr.equilibrium.feature_paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import com.ufpr.equilibrium.utils.SessionManager

class HomePaciente: AppCompatActivity() {

    private lateinit var newEvaluation: CardView
    private lateinit var updateCard: CardView
    private lateinit var history: CardView
    private lateinit var metricas: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_paciente)


        val builder = AlertDialog.Builder(this);

        builder.setTitle("Deseja sair do aplicativo ?")

        builder.setPositiveButton("Sim") { dialog, which ->
            SessionManager.clearSession();
            startActivity(Intent(this@HomePaciente, MainActivity::class.java))
        }

        builder.setNegativeButton("Não") { dialog, which ->

        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                builder.show();
            }
        })

        newEvaluation = findViewById(R.id.newEvaluation)
        updateCard = findViewById(R.id.updateCard)
        history = findViewById(R.id.history)
        metricas = findViewById(R.id.metricas)

        newEvaluation.setOnClickListener {
            startActivity(Intent(this@HomePaciente, FtstsInstruction::class.java))
        }

    }
}