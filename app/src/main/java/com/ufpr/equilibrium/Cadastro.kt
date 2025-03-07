package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Cadastro : AppCompatActivity() {


    private lateinit var cpf: EditText
    private lateinit var senha: EditText
    private lateinit var nome: EditText
    private lateinit var telefone: EditText
    private lateinit var perfil: String
    private  lateinit var checkM: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_cadastro)

        val spinnerPerfil: Spinner = findViewById(R.id.spinnerPerfil)
        val opcoesPerfil = arrayOf("pesquisador", "paciente", "profissional")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opcoesPerfil)
        spinnerPerfil.adapter = adapter

        spinnerPerfil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val itemSelecionado = parent.getItemAtPosition(position).toString()
                perfil = itemSelecionado
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        val btnEnviar = findViewById<Button>(R.id.enviar);

        cpf = findViewById(R.id.cpf);
        senha = findViewById(R.id.senha)
        nome = findViewById(R.id.nome)
        telefone = findViewById(R.id.telefone)
        checkM = findViewById(R.id.checkMasculino)

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
            if (checkM.isChecked) "M" else "F",
            perfil
        )

        val call = api.postPessoas(usuario)

        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Cadastro realizado com sucesso !",
                        Toast.LENGTH_SHORT
                    ).show()
                    println(response)

                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Log.e("Erro", "Falha ao cadastrar", t)
            }
        })
      }
    }