package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class FtstsInstruction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ftsts_instruction)

        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val ftsts = findViewById<Button>(R.id.next_button);

        arrowBtn.setOnClickListener {
            val intent = Intent(this, Testes::class.java)

            startActivity(intent);
        }

        ftsts.setOnClickListener {
            val intent = Intent(this, Contagem::class.java)

            startActivity(intent);
        }

    }


}