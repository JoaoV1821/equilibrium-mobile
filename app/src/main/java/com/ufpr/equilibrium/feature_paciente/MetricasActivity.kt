package com.ufpr.equilibrium.feature_paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction

class MetricasActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_metricas)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        
        // Ajusta a margem do BottomNavigationView para respeitar a safe area
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<androidx.constraintlayout.widget.ConstraintLayout.LayoutParams> {
                bottomMargin = systemBars.bottom
            }
            insets
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {


                R.id.nav_history -> { /* abrir histórico */ }

                R.id.nav_home -> {

                    startActivity(Intent(this@MetricasActivity, HomePaciente::class.java))
                }


                R.id.nav_add -> {
                    startActivity(Intent(this@MetricasActivity, FtstsInstruction::class.java))
                }

                R.id.nav_edit -> { /* abrir edição */ }
            }

            true
        }

    }
}