package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var cpf: EditText
    private lateinit var senha: EditText
    private lateinit var intent: Intent
    private lateinit var intentCadastro: Intent
    private lateinit var cadastroButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.login_button);

        intent = Intent(this, Testes::class.java)
        intentCadastro = Intent(this, Cadastro::class.java)
        cpf = findViewById(R.id.cpf)
        senha = findViewById(R.id.password)
        cadastroButton = findViewById(R.id.cadastroView)

        btnLogin.setOnClickListener {
            authentication()

        }

        cadastroButton.setOnClickListener {
            startActivity(intentCadastro)
        }
    }

    private fun authentication() {
        val api = RetrofitClient.instancePessoasAPI

        val login = Login(cpf.text.toString(), senha.text.toString())

        val call = api.authenticate(login)

        call.enqueue(object : Callback<LoginResult> {
            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
               if (response.isSuccessful) {
                   Toast.makeText(applicationContext, "Login realizado com sucesso !", Toast.LENGTH_SHORT).show()
                   println(response)

                   startActivity(intent);

               }
            }
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                Log.e("Erro", "Falha no login", t)
            }
        })


    }

}