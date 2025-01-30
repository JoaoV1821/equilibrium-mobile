package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Testes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testes)

        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val tugBtn = findViewById<FrameLayout>(R.id.tug);
        val ftsts = findViewById<FrameLayout>(R.id.ftsts);


        arrowBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent);
        }

        tugBtn.setOnClickListener {
            val intent = Intent(this, TugInstruction::class.java);

            startActivity(intent);
        }

        ftsts.setOnClickListener {
            val intent = Intent(this, FtstsInstruction::class.java)

            startActivity(intent);
        }

    }
}