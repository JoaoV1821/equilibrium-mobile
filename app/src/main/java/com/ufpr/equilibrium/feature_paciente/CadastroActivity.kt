package com.ufpr.equilibrium.feature_paciente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.ufpr.equilibrium.R


class CadastroActivity : AppCompatActivity() {

    private lateinit var nome: EditText
    private lateinit var cpf: EditText
    private lateinit var dataNascimento: EditText
    private lateinit var sexo: RadioGroup
    private lateinit var historicoQueda: RadioGroup
    private lateinit var radioHistorico: RadioButton
    private lateinit var radioSexo: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        nome = findViewById(R.id.nome)
        cpf = findViewById(R.id.cpf)
        dataNascimento = findViewById(R.id.dataNasc)
        sexo = findViewById(R.id.radioGroupSexo)
        historicoQueda = findViewById(R.id.radioGroupQueda)

        cpf.addMask("###.###.###-##")
        dataNascimento.addMask("##/##/####")

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
                    postPaciente()
                }
            }
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

    }

    private fun postPaciente() {

        radioHistorico = findViewById(historicoQueda.checkedRadioButtonId)
        radioSexo = findViewById(sexo.checkedRadioButtonId)

        val nomeStr = nome.text.toString().trim()
        val cpfStr = cpf.text.toString().trim().replace(Regex("[^\\d]"), "")

        val dataNascStr = try {
            val sdfEntrada = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val sdfSaida = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val data = sdfEntrada.parse(dataNascimento.text.toString().trim())

            sdfSaida.format(data!!)

        } catch (e: Exception) {
            "" // caso a data seja inválida
        }

        val historico = radioHistorico.text.toString() == "Sim"
        val sexoStr = radioSexo.text.toString()
        val paciente = CadastroPacienteModel(nomeStr, cpfStr, dataNascStr, sexoStr, historico )

        println(paciente)

        Toast.makeText(applicationContext,"Cadastro concluído", Toast.LENGTH_SHORT ).show()

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