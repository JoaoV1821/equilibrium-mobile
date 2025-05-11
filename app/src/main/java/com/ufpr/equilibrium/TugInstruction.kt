package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class TugInstruction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tug_instruction)

        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val tug = findViewById<Button>(R.id.next_button);
        var intent: Intent;

        println(SessionManager.user?.profile);

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (SessionManager.user?.profile == "healthProfessional") {
                    intent = Intent(this@TugInstruction, HomeProfissional::class.java)

                    startActivity(intent);

                } else {
                    intent = Intent(this@TugInstruction, Testes::class.java)

                    startActivity(intent)
                }

            }
        })

        arrowBtn.setOnClickListener {
            if (SessionManager.user?.profile == "healthProfessional") {
                intent = Intent(this@TugInstruction, HomeProfissional::class.java)

                startActivity(intent);

            } else {
                intent = Intent(this@TugInstruction, Testes::class.java)

                startActivity(intent)
            }
        }

        tug.setOnClickListener {

            intent = Intent(this, HealthUnitActivity::class.java)

            val cpf = Intent().getStringExtra("cpf")

            intent.putExtra("cpf",cpf)
            intent.putExtra("teste", "TUG")

            startActivity(intent);
        }
    }
}