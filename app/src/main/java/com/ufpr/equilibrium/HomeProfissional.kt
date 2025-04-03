package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeProfissional : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pacienteAdapter: PacienteAdapter
    private val pacientes = mutableListOf<Paciente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_profissional)

        recyclerView = findViewById(R.id.rv_pacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val arrowBtn = findViewById<ImageView>(R.id.arrow_back);
        val pacienteButton = findViewById<Button>(R.id.btn_add_paciente)

        pacienteAdapter = PacienteAdapter(this, pacientes)
        recyclerView.adapter = pacienteAdapter;

        getPacientes();

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

        arrowBtn.setOnClickListener {
           builder.show()

        }

        pacienteButton.setOnClickListener {
            startActivity(Intent(this@HomeProfissional, Cadastro::class.java))
        }

    }


    private fun getPacientes() {

        val api = RetrofitClient.instancePessoasAPI
        val call = api.getPacientes()

        call.enqueue(object : Callback<List<Paciente>> {
            override fun onResponse(call: Call<List<Paciente>>, response: Response<List<Paciente>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        pacientes.clear()
                        pacientes.addAll(it)
                        pacienteAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(applicationContext, "Erro ao buscar pacientes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Paciente>>, t: Throwable) {
                Log.e("Erro", "Falha ao buscar pacientes", t)
            }
        })
    }
}