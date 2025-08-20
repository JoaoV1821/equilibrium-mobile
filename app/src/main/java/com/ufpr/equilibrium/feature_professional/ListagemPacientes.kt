package com.ufpr.equilibrium.feature_professional

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.Paciente
import com.ufpr.equilibrium.feature_paciente.PacienteAdapter
import com.ufpr.equilibrium.network.RetrofitClient
import com.ufpr.equilibrium.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListagemPacientes : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pacienteAdapter: PacienteAdapter
    private val pacientes = mutableListOf<Paciente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

        recyclerView = findViewById(R.id.rv_pacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val arrowBtn = findViewById<ImageView>(R.id.arrow_back);

        pacienteAdapter = PacienteAdapter(this, pacientes)
        recyclerView.adapter = pacienteAdapter;

        getPacientes();

        val builder = AlertDialog.Builder(this);

        builder.setTitle("Deseja sair do aplicativo ?")

        builder.setPositiveButton("Sim") { dialog, which ->
            SessionManager.clearSession();
            startActivity(Intent(this@ListagemPacientes, HomeProfissional::class.java))
        }

        builder.setNegativeButton("Não") { dialog, which ->

        }

        val searchInput = findViewById<TextInputEditText>(R.id.searchInput)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pacienteAdapter.filtrarPorCpf(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }

    private fun getPacientes() {

        val api = RetrofitClient.instancePessoasAPI
        val call = api.getPacientes()

        call.enqueue(object : Callback<List<Paciente>> {
            override fun onResponse(call: Call<List<Paciente>>, response: Response<List<Paciente>>) {
                if (response.isSuccessful) {
                    response.body()?.let {

                        println(it)

                        pacientes.clear()
                        pacientes.addAll(it)
                        pacienteAdapter.atualizarLista(it)
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