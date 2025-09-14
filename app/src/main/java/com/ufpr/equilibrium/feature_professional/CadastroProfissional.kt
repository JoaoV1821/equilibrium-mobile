package com.ufpr.equilibrium.feature_professional

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroProfissional: AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var cpf: EditText
    private lateinit var senha: EditText
    private lateinit var telefone: EditText
    private lateinit var email: EditText
    private  lateinit var especialidade: EditText
    private lateinit var sexo: RadioGroup
    private lateinit var selectedSexo : RadioButton
    private lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cadastro_profissional)

        nome = findViewById(R.id.nome)
        cpf = findViewById(R.id.cpf)
        senha = findViewById(R.id.senha)
        telefone = findViewById(R.id.phone)
        email =  findViewById(R.id.email)
        especialidade = findViewById(R.id.especialidade)
        sexo = findViewById(R.id.radioGroupSexo)

        submit = findViewById(R.id.enviar)

        submit.setOnClickListener {
            postProfessional()
        }
    }


    private fun postProfessional () {

        val api = RetrofitClient.instancePessoasAPI

        val idSelectedSexo = sexo.checkedRadioButtonId

        selectedSexo = findViewById(idSelectedSexo)

        val professional = ProfessionalModel(
            cpf = cpf.text.toString(),
            nome = nome.text.toString(),
            password = senha.text.toString(),
            phone = telefone.text.toString(),
            email = email.text.toString(),
            expertise = especialidade.text.toString(),
            gender = "M",
            profile = "healthProfessional"
        )

        val call = api.postProfessional(professional)

        call.enqueue(object : Callback<ProfessionalModel> {

            override fun onResponse(call: Call<ProfessionalModel>, response: Response<ProfessionalModel>) {
               if (response.isSuccessful) {
                   Toast.makeText(
                       applicationContext,
                       "Profissional Cadastrado com sucesso!",
                       Toast.LENGTH_SHORT
                   ).show()
               }
            }

            override fun onFailure(call: Call<ProfessionalModel>, t: Throwable) {
                Log.e("Erro", "Falha no login", t)
                Toast.makeText(
                    applicationContext,
                    "Erro ao conectar ao servidor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

