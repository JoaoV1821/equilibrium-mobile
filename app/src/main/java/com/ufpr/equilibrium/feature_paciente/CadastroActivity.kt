package com.ufpr.equilibrium.feature_paciente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class CadastroActivity : AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var cpf: EditText
    private lateinit var telefone: EditText
    private lateinit var dataNascimento: EditText
    private lateinit var escolaridade:  EditText
    private lateinit var nivelSocio: EditText
    private lateinit var peso: EditText
    private lateinit var altura: EditText
    private lateinit var sexo: RadioGroup
    private lateinit var historicoQueda: RadioGroup
    private lateinit var cep: EditText
    private lateinit var numero: EditText
    private lateinit var rua: EditText
    private lateinit var complemento: EditText
    private lateinit var bairro: EditText
    private lateinit var cidade: EditText
    private lateinit var uf: EditText

    private lateinit var selectedSexo : RadioButton
    private lateinit var selectedQueda: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        nome = findViewById(R.id.nome)
        cpf = findViewById(R.id.cpf)
        escolaridade = findViewById(R.id.escolaridade)
        nivelSocio = findViewById(R.id.nivelSocioeconomico)
        peso = findViewById(R.id.peso)
        numero = findViewById(R.id.numero)
        rua = findViewById(R.id.rua)
        complemento = findViewById(R.id.complemento)
        bairro = findViewById(R.id.bairro)
        cidade = findViewById(R.id.cidade)
        uf = findViewById(R.id.estado)
        dataNascimento = findViewById(R.id.senha)
        sexo = findViewById(R.id.radioGroupSexo)
        historicoQueda = findViewById(R.id.radioGroupQueda)
        altura = findViewById(R.id.altura)
        telefone = findViewById(R.id.telefone)
        cep = findViewById(R.id.cep)

        cpf.addMask("###.###.###-##")
        dataNascimento.addMask("##/##/####")
        altura.addMask("#.##")

        telefone.addMask("(##) #####-####")

        cep.addMask("#####-###")

        val btnCadastrar = findViewById<Button>(R.id.enviar)

        btnCadastrar.setOnClickListener {

            val nomeStr = nome.text.toString().trim()
            val cpfStr = cpf.text.toString().trim()
            val dataNascStr = dataNascimento.text.toString().trim()

            when {

                nomeStr.isEmpty() -> {
                    nome.error = "Informe o nome"
                    nome.requestFocus()
                }

                cpfStr.isEmpty() -> {
                    cpf.error = "Informe o CPF"
                    cpf.requestFocus()
                }

                cpfStr.length != 14 -> {
                    cpf.error = "CPF deve ter 11 dígitos"
                    cpf.requestFocus()
                }

                dataNascStr.isEmpty() -> {
                    dataNascimento.error = "Informe a data de nascimento"
                    dataNascimento.requestFocus()
                }

                sexo.checkedRadioButtonId == -1 -> {
                    Toast.makeText(this, "Selecione o sexo", Toast.LENGTH_SHORT).show()
                }

                historicoQueda.checkedRadioButtonId == -1 -> {
                    Toast.makeText(this, "Informe o histórico de quedas", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    postData()
                }
            }
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

    }


    private  fun postData() {
        val api = RetrofitClient.instancePessoasAPI

        val idSelectedSexo = sexo.checkedRadioButtonId
        val idSelectedQueda = historicoQueda.checkedRadioButtonId

        selectedSexo = findViewById(idSelectedSexo)
        selectedQueda = findViewById(idSelectedQueda)

        val dataNascStr = try {
            val sdfEntrada = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val sdfSaida = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val data = sdfEntrada.parse(dataNascimento.text.toString().trim())

            sdfSaida.format(data!!)

        } catch (e: Exception) {
            "" // caso a data seja inválida
        }

        println(selectedQueda.text.toString())

        val paciente = CadastroPacienteModel(

            cpf = cpf.text.toString(),
            dateOfBirth = dataNascStr,
            educationLevel = escolaridade.text.toString(),
            socioEconomicStatus = nivelSocio.text.toString(),
            cep = cep.text.toString(),
            street = rua.text.toString(),
            number = numero.text.toString().toInt(),
            neighborhood = bairro.text.toString(),
            city = cidade.text.toString(),
            state = uf.text.toString(),
            weight = peso.text.toString().toFloat(),
            age = 0,
            downFall = selectedQueda.text.toString() == "Sim",
            gender = selectedSexo.text.toString(),
            height = altura.text.toString().toFloat(),
            profile = "patient"

        )

            val call = api.postPatient(paciente);

            call.enqueue(object : retrofit2.Callback<CadastroPacienteModel> {
                override fun onResponse(call: Call<CadastroPacienteModel>, response: Response<CadastroPacienteModel>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext,"Cadastro concluído", Toast.LENGTH_SHORT ).show()
                    }
                }

                override fun onFailure(call: Call<CadastroPacienteModel>, t: Throwable) {
                    Log.e("Erro", "Falha ao enviar o teste", t)
                }
            })
    }


    fun EditText.addMask(mask: String) {
        this.addTextChangedListener(object : TextWatcher {
            var isUpdating: Boolean = false
            var oldText: String = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                val unmasked = s.toString().replace(Regex("[^\\d]"), "")
                var maskedText = ""
                var i = 0

                for (m in mask.toCharArray()) {
                    if (m != '#' && unmasked.length > i) {
                        maskedText += m
                    } else {
                        try {
                            maskedText += unmasked[i]
                        } catch (_: Exception) { break }
                        i++
                    }
                }

                isUpdating = true
                this@addMask.setText(maskedText)
                this@addMask.setSelection(maskedText.length)
            }
        })
    }


}