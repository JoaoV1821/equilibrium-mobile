package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ufpr.equilibrium.feature_login.LoginActivity
import com.ufpr.equilibrium.feature_paciente.HomePaciente
import com.ufpr.equilibrium.feature_professional.CadastroPacienteActivity
import com.ufpr.equilibrium.feature_professional.CadastroProfissional
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.utils.RoleHelpers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        SessionManager.init(this)

        if (SessionManager.isLoggedIn()) {
            if (RoleHelpers.isHealthProfessional()) {
                startActivity(Intent(this@MainActivity, HomeProfissional::class.java))
            } else if (RoleHelpers.isPatient()) {
                startActivity(Intent(this@MainActivity, HomePaciente::class.java))
            }
        }

        val btnLogin = findViewById<Button>(R.id.profissional)
        val btnCadastro = findViewById<Button>(R.id.profissional2)


        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        btnCadastro.setOnClickListener {
            startActivity(Intent(this@MainActivity, CadastroPacienteActivity::class.java))
        }

    }
}
