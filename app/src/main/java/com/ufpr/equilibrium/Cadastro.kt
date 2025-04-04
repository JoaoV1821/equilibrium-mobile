package com.ufpr.equilibrium

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Cadastro : AppCompatActivity() {

    private lateinit var cpf: EditText;
    private lateinit var nome: EditText;
    private lateinit var senha: EditText;
    private lateinit var telefone: EditText;
    private lateinit var cep: EditText;
    private lateinit var dataNasc: EditText;
    private lateinit var idade: EditText;
    private lateinit var altura: EditText;
    private lateinit var peso: EditText;
    private lateinit var escolaridade: EditText;
    private lateinit var nivelSocio: EditText;
    private lateinit var queda: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        val btnEnviar = findViewById<Button>(R.id.enviar)

        cpf = findViewById(R.id.cpf)
        senha = findViewById(R.id.senha)
        nome = findViewById(R.id.nome)
        telefone = findViewById(R.id.telefone)
        cep = findViewById(R.id.cep)
        dataNasc = findViewById(R.id.dataNasc)

        cpf.addTextChangedListener(MaskWatcher(cpf, "###.###.###-##"))
        telefone.addTextChangedListener(MaskWatcher(telefone, "(##) #####-####"))
        cep.addTextChangedListener(MaskWatcher(cep, "#####-###"))
        dataNasc.addTextChangedListener(MaskWatcher(dataNasc, "##/##/####"))

        btnEnviar.setOnClickListener {
            cadastrar()
        }
    }

    private fun cadastrar() {
        val api = RetrofitClient.instancePessoasAPI

        val usuario = Usuario(
            cpf.text.toString(),
            nome.text.toString(),
            senha.text.toString(),
            telefone.text.toString(),
            "",
            ""
        )

        val call = api.postPessoas(usuario)
        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Erro", "Falha ao cadastrar", t)
            }
        })
    }
}