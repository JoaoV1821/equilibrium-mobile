package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class FtstsInstruction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ftsts_instruction)

        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val ftsts = findViewById<Button>(R.id.next_button);



        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = if (SessionManager.usuario?.perfil == "paciente") {
                    Intent(this@FtstsInstruction, Testes::class.java)

                } else {
                    Intent(this@FtstsInstruction, HomeProfissional::class.java)
                }

                startActivity(intent);
            }
        })


        arrowBtn.setOnClickListener {
            val intent = if (SessionManager.usuario?.perfil == "paciente") {

                Intent(this@FtstsInstruction, Testes::class.java)

            } else {
                Intent(this@FtstsInstruction, HomeProfissional::class.java)
            }

            startActivity(intent);
        }

        ftsts.setOnClickListener {

            val cpf = intent.getStringExtra("cpf") // <-- pegar da intent atual ✅

            println("$cpf - instruction")

            val intent = Intent(this, Contagem::class.java)



            intent.putExtra("teste", "5TSTS")

            startActivity(intent)
        }

    }


}