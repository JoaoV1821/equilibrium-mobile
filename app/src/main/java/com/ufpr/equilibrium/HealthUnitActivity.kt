package com.ufpr.equilibrium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthUnitActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var btnConfirmar: Button

    private var healthUnitList: List<HealthUnit> = listOf()
    private var selectedUnitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_unit)

        spinner = findViewById(R.id.spinner_unidades)
        btnConfirmar = findViewById(R.id.btn_confirmar)

        handlerHealthUnits()

        btnConfirmar.setOnClickListener {
            if (selectedUnitId != null) {

                val cpf = intent.getStringExtra("cpf");
                val teste = intent.getStringExtra("teste");

                val intent = Intent(this, Contagem::class.java)

                println(selectedUnitId)

                intent.putExtra("id_unidade", selectedUnitId)

                intent.putExtra("cpf",cpf)

                intent.putExtra("teste", teste)


                startActivity(intent)

                setResult(RESULT_OK, intent)
                finish()

            } else {
                Toast.makeText(this, "Selecione uma unidade de saúde.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handlerHealthUnits() {
        val call = RetrofitClient.instancePessoasAPI.getHealthUnit()

        call.enqueue(object : Callback<List<HealthUnit>> {
            override fun onResponse(
                call: Call<List<HealthUnit>>,
                response: Response<List<HealthUnit>>
            ) {

                if (response.isSuccessful && response.body() != null) {
                    healthUnitList = response.body()!!

                    val nomes = healthUnitList.map { it.name }

                    val adapter = ArrayAdapter(
                        this@HealthUnitActivity,
                        android.R.layout.simple_spinner_item,
                        nomes
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected (
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long

                        ) {
                            selectedUnitId = healthUnitList[position].id.toString()

                            println(selectedUnitId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            selectedUnitId = null
                        }
                    }

                } else {
                    Toast.makeText(this@HealthUnitActivity, "Erro ao carregar unidades", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<HealthUnit>>, t: Throwable) {
                Log.e("API", "Falha na requisição", t)
                Toast.makeText(this@HealthUnitActivity, "Falha na conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
