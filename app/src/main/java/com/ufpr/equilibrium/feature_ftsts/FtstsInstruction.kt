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
import com.ufpr.equilibrium.utils.PacienteManager
import java.util.UUID

class FtstsInstruction : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ftsts_instruction)

        // Get patient ID from intent and store in PacienteManager
        val pacienteIdString = intent.getStringExtra("paciente_id")
        if (!pacienteIdString.isNullOrBlank()) {
            try {
                PacienteManager.uuid = UUID.fromString(pacienteIdString)
                android.util.Log.d("FtstsInstruction", "Patient ID saved successfully: $pacienteIdString")
            } catch (e: IllegalArgumentException) {
                // If UUID parsing fails, log but don't crash
                android.util.Log.w("FtstsInstruction", "Invalid patient ID: $pacienteIdString", e)
            }
        } else {
            android.util.Log.w("FtstsInstruction", "No patient ID received in intent")
        }
      
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