package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Testes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testes)

        var intent: Intent;
        val perfil = SessionManager.user?.profile ?: ""
        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val tugBtn = findViewById<FrameLayout>(R.id.tug);
        val ftsts = findViewById<FrameLayout>(R.id.ftsts);
        val builder = AlertDialog.Builder(this);

        builder.setTitle("Deseja sair do aplicativo ?")

        builder.setPositiveButton("Sim") { dialog, which ->
            SessionManager.clearSession();
            startActivity(Intent(this@Testes, MainActivity::class.java))
        }

        builder.setNegativeButton("Não") { dialog, which ->

        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    builder.show();
            }
        })

        arrowBtn.setOnClickListener {
                builder.show();
        }


        tugBtn.setOnClickListener {
            intent = Intent(this, TugInstruction::class.java);
            startActivity(intent);
        }

        ftsts.setOnClickListener {
            intent = Intent(this, FtstsInstruction::class.java)

            startActivity(intent);
        }

    }

}