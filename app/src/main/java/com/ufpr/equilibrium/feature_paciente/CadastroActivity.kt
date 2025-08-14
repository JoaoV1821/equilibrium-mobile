package com.ufpr.equilibrium.feature_paciente

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        nome = findViewById(R.id.nome)
        cpf = findViewById(R.id.cpf)
        dataNascimento = findViewById(R.id.dataNasc)
        sexo = findViewById(R.id.radioGroupSexo)
        historicoQueda = findViewById(R.id.radioGroupQueda)

        val btnCadastrar = findViewById<Button>(R.id.enviar)

        btnCadastrar.setOnClickListener {
            val nomeStr = nome.text.toString().trim()
            val cpfStr = cpf.text.toString().trim()
            val dataNascStr = dataNascimento.text.toString().trim()

            if (nomeStr.isNotEmpty() || cpfStr.isNotEmpty() || dataNascStr.isNotEmpty() || isValidCPF(cpfStr)) {
                postPaciente()

            } else {
                Toast.makeText(applicationContext,"Erro ao cadastrar", Toast.LENGTH_SHORT ).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

    }

    private fun isValidCPF(cpf: String): Boolean {
        val cleanedCPF = cpf.replace("\\D".toRegex(), "") // Remove caracteres não numéricos

        if (cleanedCPF.length != 11 || cleanedCPF.all { it == cleanedCPF[0] }) return false

        fun calculateDigit(cpfSlice: String, weights: IntProgression): Int {
            val sum = cpfSlice.mapIndexed { index, c -> c.digitToInt() * weights.elementAt(index) }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }

        val digit1 = calculateDigit(cleanedCPF.substring(0, 9), 10 downTo 2)
        val digit2 = calculateDigit(cleanedCPF.substring(0, 10), 11 downTo 2)

        println(cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2)

        return cleanedCPF[9].digitToInt() == digit1 && cleanedCPF[10].digitToInt() == digit2
    }

    private fun postPaciente(){

        val nomeStr = nome.text.toString().trim()
        val cpfStr = cpf.text.toString().trim()
        val dataNascStr = dataNascimento.text.toString().trim()
        val sexoStr = sexo.id.toString()
        val historico = historicoQueda.id.toString() == "Sim"
        val paciente = CadastroPacienteModel(nomeStr, cpfStr, dataNascStr, sexoStr, historico )

        println(paciente)

        Toast.makeText(applicationContext,"Cadastro concluído", Toast.LENGTH_SHORT ).show()

    }

}