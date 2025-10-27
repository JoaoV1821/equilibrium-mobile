package com.ufpr.equilibrium.feature_ftsts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_healthUnit.HealthUnitActivity
import com.ufpr.equilibrium.feature_paciente.HomePaciente
import com.ufpr.equilibrium.feature_professional.HomeProfissional
import com.ufpr.equilibrium.feature_professional.ListagemPacientes
import com.ufpr.equilibrium.feature_teste.Contagem
import com.ufpr.equilibrium.utils.SessionManager
import com.ufpr.equilibrium.utils.RoleHelpers

class FtstsInstruction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ftsts_instruction)

        val arrowBtn = findViewById<ImageView>(R.id.arrow_button);
        val ftsts = findViewById<Button>(R.id.next_button);

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            
            override fun handleOnBackPressed() {

                if (RoleHelpers.isHealthProfessional()) {
                    intent = Intent(this@FtstsInstruction, ListagemPacientes::class.java)

                    startActivity(intent);

                } else {
                    intent = Intent(this@FtstsInstruction, HomePaciente::class.java)

                    startActivity(intent)
                }
            }
        })


        arrowBtn.setOnClickListener {

            if (RoleHelpers.isHealthProfessional()) {
                intent = Intent(this@FtstsInstruction, HomeProfissional::class.java)

                startActivity(intent);

            } else {

                intent = Intent(this@FtstsInstruction, HomePaciente::class.java)

                startActivity(intent)
            }
        }

        ftsts.setOnClickListener {

           val intent = if (RoleHelpers.isHealthProfessional()) {

               val cpf = Intent().getStringExtra("cpf")
               intent.putExtra("cpf",cpf)

                Intent(this, HealthUnitActivity::class.java)

            } else {

                Intent(this, Contagem::class.java)
            }

            intent.putExtra("teste", "FTSTS")

            startActivity(intent)
        }

    }

}