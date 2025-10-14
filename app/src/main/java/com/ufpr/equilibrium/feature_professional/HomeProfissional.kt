package com.ufpr.equilibrium.feature_professional

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.utils.SessionManager

class HomeProfissional : AppCompatActivity() {

    private lateinit var  tvName: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_profissional)

        val builder = AlertDialog.Builder(this)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        builder.setTitle("Deseja sair do aplicativo ?")

        builder.setPositiveButton("Sim") { dialog, which ->
            SessionManager.clearSession()
            startActivity(Intent(this@HomeProfissional, MainActivity::class.java))
        }

        builder.setNegativeButton("Não") { dialog, which ->

        }

        tvName = findViewById(R.id.tvGreeting);

        tvName = findViewById(R.id.tvGreeting)

        SessionManager.user?.let { user ->
            val firstName = user.fullName
                .trim()
                .split(Regex("\\s+"))
                .firstOrNull()
                ?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString()
                }
                ?: "Usuário"

            tvName.text = firstName

        } ?: run {
            tvName.text = "Usuário"
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                builder.show()
            }
        })

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_add -> {
                    startActivity(Intent(this@HomeProfissional, ListagemPacientes::class.java))
                }

                R.id.nav_edit -> {
                    startActivity(Intent(this@HomeProfissional, AtualizarProfissionalActivity::class.java))
                }

                R.id.nav_add_user -> {
                    startActivity(Intent(this@HomeProfissional, CadastroPacienteActivity::class.java))
                }
            }

            true
        }
    }
}
