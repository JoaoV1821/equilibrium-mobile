package com.ufpr.equilibrium.feature_paciente

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import com.ufpr.equilibrium.utils.SessionManager
import java.util.Locale

class HomePaciente: AppCompatActivity() {

    private lateinit var tvTotalTestes: TextView
    private lateinit var tvTestesMes: TextView
    private lateinit var tvDataUltimo: TextView
    private lateinit var tvMelhor: TextView
    private lateinit var tvDesempenho: TextView
    private lateinit var tvName: TextView



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

        tvTotalTestes = findViewById(R.id.tvTotalTestes)
        tvTestesMes = findViewById(R.id.tvTestesMes)
        tvDataUltimo = findViewById(R.id.tvDataUltimo)
        tvMelhor = findViewById(R.id.tvMelhor)
        tvDesempenho = findViewById(R.id.tvBest)
        tvName = findViewById(R.id.tvGreeting)

        val firstName = SessionManager.user?.fullName
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.split(Regex("\\s+"))
            ?.firstOrNull()
            ?.replaceFirstChar { ch ->
                // Garante capitalização correta em pt-BR
                if (ch.isLowerCase()) ch.titlecase(Locale("pt", "BR")) else ch.toString()
            }

        tvName.text = firstName ?: "Olá"

        tvTotalTestes.text = "15"
        tvTestesMes.text = "15"
        tvDataUltimo.text = "13/12"
        tvMelhor.text = "15s"
        tvDesempenho.text = "Ótimo"


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stats -> {
                    startActivity(Intent(this@HomePaciente, MetricasActivity::class.java))
                }
                R.id.nav_history -> {
                    startActivity(Intent(this@HomePaciente, HistoricoActivity::class.java))
                }

                R.id.nav_add -> {
                    startActivity(Intent(this@HomePaciente, FtstsInstruction::class.java))
                }
                R.id.nav_edit -> { /* abrir edição */ }
            }

            true
        }


        //newEvaluation = findViewById(R.id.newEvaluation)
        //updateCard = findViewById(R.id.updateCard)
        //history = findViewById(R.id.history)
        //metricas = findViewById(R.id.metricas)
        //userName = findViewById(R.id.userName)
       // dataNasc = findViewById(R.id.userNasc)

        //userName.text = SessionManager.user?.name

        //dataNasc.text = SessionManager.user?.data_nascimento

       // newEvaluation.setOnClickListener {
        //    startActivity(Intent(this@HomePaciente, FtstsInstruction::class.java))
       // }

       // updateCard.setOnClickListener {
          //  startActivity(Intent(this@HomePaciente, UpdatePaciente::class.java))
        //}

    }
}