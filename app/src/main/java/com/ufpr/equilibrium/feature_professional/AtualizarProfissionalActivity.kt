package com.ufpr.equilibrium.feature_professional

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.HistoricoActivity
import com.ufpr.equilibrium.feature_paciente.MetricasActivity

class AtualizarProfissionalActivity:  AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var senha: EditText
    private lateinit var telefone: EditText
    private lateinit var email: EditText
    private  lateinit var especialidade: EditText
    private lateinit var sexo: RadioGroup
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_atualizar_profissional)

        nome = findViewById(R.id.nome)
        senha = findViewById(R.id.senha)
        telefone = findViewById(R.id.phone)
        email =  findViewById(R.id.email)
        especialidade = findViewById(R.id.especialidade)
        sexo = findViewById(R.id.radioGroupSexo)

        submit = findViewById(R.id.enviar)

        submit.setOnClickListener {
            //TODO
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_stats -> {
                    startActivity(Intent(this@AtualizarProfissionalActivity, MetricasActivity::class.java))
                }

                R.id.nav_history -> {
                    startActivity(Intent(this@AtualizarProfissionalActivity, HistoricoActivity::class.java))
                }

                R.id.nav_add -> {
                    startActivity(Intent(this@AtualizarProfissionalActivity, ListagemPacientes::class.java))
                }

                R.id.nav_home -> {
                    startActivity(Intent(this@AtualizarProfissionalActivity, HomeProfissional::class.java))
                }

                R.id.nav_evaluation -> {
                    startActivity(Intent(this@AtualizarProfissionalActivity, ListagemPacientes::class.java))
                }
            }

            true
        }

    }
}