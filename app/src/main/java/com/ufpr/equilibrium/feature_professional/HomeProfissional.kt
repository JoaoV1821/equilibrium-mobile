package com.ufpr.equilibrium.feature_professional

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.CadastroActivity
import com.ufpr.equilibrium.utils.SessionManager


class HomeProfissional : AppCompatActivity() {

    private lateinit var newPacient: CardView
    private  lateinit var  newEvaluation: CardView
    private lateinit var updateCard: CardView

    private lateinit var userName: TextView
    private  lateinit var userPhone: TextView
    private lateinit var  userPerfil: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_profissional)

        val builder = AlertDialog.Builder(this);

        builder.setTitle("Deseja sair do aplicativo ?")

        builder.setPositiveButton("Sim") { dialog, which ->
            SessionManager.clearSession();
            startActivity(Intent(this@HomeProfissional, MainActivity::class.java))
        }

        builder.setNegativeButton("Não") { dialog, which ->

        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                builder.show();
            }
        })

        newPacient = findViewById(R.id.history)
        newEvaluation = findViewById(R.id.newEvaluation)
        userName = findViewById(R.id.userName)
        userPhone = findViewById(R.id.userNasc)
        userPerfil = findViewById(R.id.userPerfil)

        updateCard = findViewById(R.id.updateCard)

        userName.text = SessionManager.user?.name
        userPerfil.text = if ( SessionManager.user?.profile == "healthProfessional") "Profissional de Saúde" else ""
        userPhone.text = SessionManager.user?.phone

        newPacient.setOnClickListener {
            intent = Intent(this@HomeProfissional, CadastroActivity::class.java)
            startActivity(intent)
        }

        newEvaluation.setOnClickListener {
            intent = Intent(this@HomeProfissional,ListagemPacientes::class.java )
            startActivity(intent)
        }

        updateCard.setOnClickListener {
            intent = Intent(this@HomeProfissional, UpdateProfessional::class.java)
            startActivity(intent)
        }
    }
}