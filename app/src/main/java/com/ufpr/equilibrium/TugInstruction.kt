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




        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = if (SessionManager.usuario?.perfil == "paciente") {
                    Intent(this@TugInstruction, Testes::class.java)

                } else {
                    Intent(this@TugInstruction, HomeProfissional::class.java)
                }

                startActivity(intent);
            }
        })

        arrowBtn.setOnClickListener {
            intent = if (SessionManager.usuario?.perfil == "paciente") {
                Intent(this@TugInstruction, Testes::class.java)

            } else {
                Intent(this@TugInstruction, HomeProfissional::class.java)
            }

            startActivity(intent);
        }

        tug.setOnClickListener {


            intent = Intent(this, Contagem::class.java)

            val cpf = Intent().getStringExtra("cpf")

            intent.putExtra("cpf",cpf)
            intent.putExtra("teste", "TUG")



            startActivity(intent);
        }
    }
}