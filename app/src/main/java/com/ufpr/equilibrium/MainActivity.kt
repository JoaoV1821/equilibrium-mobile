package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        SessionManager.init(this)

        if (SessionManager.isLoggedIn()) {
            startActivity(Intent(this@MainActivity, HomeProfissional::class.java))
        }

        val btnPaciente = findViewById<Button>(R.id.paciente)
        val btnProfissional = findViewById<Button>(R.id.profissional)

        btnPaciente.setOnClickListener {

            startActivity(Intent(this@MainActivity, Testes::class.java))
        }

        btnProfissional.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
}
