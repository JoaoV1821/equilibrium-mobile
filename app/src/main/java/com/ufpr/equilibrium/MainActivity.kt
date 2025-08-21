package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ufpr.equilibrium.feature_login.LoginActivity
import com.ufpr.equilibrium.feature_paciente.HomePaciente
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
            if (SessionManager.user?.profile == "healthProfissional") {
                startActivity(Intent(this@MainActivity, HomeProfissional::class.java))

            } else if (SessionManager.user?.profile == "patient") {
                startActivity(Intent(this@MainActivity, HomePaciente::class.java))
            }

        }

        val btnLogin = findViewById<Button>(R.id.profissional)


        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

    }
}
