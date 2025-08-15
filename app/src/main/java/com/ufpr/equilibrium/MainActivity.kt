package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ufpr.equilibrium.feature_login.LoginActivity
import com.ufpr.equilibrium.feature_paciente.CadastroActivity
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.utils.SessionManager

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

        val btnCadastro = findViewById<Button>(R.id.paciente)
        val btnLogin = findViewById<Button>(R.id.profissional)

        btnCadastro.setOnClickListener {

            startActivity(Intent(this@MainActivity, CadastroActivity::class.java))
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
}
