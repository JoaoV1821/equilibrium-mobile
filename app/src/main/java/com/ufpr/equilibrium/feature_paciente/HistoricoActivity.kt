package com.ufpr.equilibrium.feature_paciente

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction

class HistoricoActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historico)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_graph -> {
                    startActivity(Intent(this@HistoricoActivity, MetricasActivity::class.java))

                }

                R.id.nav_home -> {
                    startActivity(Intent(this@HistoricoActivity, HomePaciente::class.java))
                }


                R.id.nav_add -> {
                    startActivity(Intent(this@HistoricoActivity, FtstsInstruction::class.java))
                }

                R.id.nav_edit -> {

                }
            }

            true
        }
    }
}