package com.ufpr.equilibrium.feature_professional
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.ufpr.equilibrium.MainActivity
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DadosPessoaisFragment : Fragment() {

    private lateinit var editTextNome: EditText
    private lateinit var editTextCpf: EditText
    private lateinit var dataNasc: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var sexoGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dados_pessoais, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVoltar = view.findViewById<MaterialButton>(R.id.voltar)
        val btnEnviar = view.findViewById<MaterialButton>(R.id.enviar)

        editTextNome = view.findViewById(R.id.nome)
        editTextCpf = view.findViewById(R.id.cpf)
        editTextCelular = view.findViewById(R.id.telefone)
        dataNasc = view.findViewById(R.id.dataNascimento)
        sexoGroup = view.findViewById(R.id.radioGroupSexo)

        val viewModel = ViewModelProvider(requireActivity()).get(FormViewModel::class.java)

        aplicarMascaraCpf(editTextCpf)
        aplicarMascaraCelular(editTextCelular)
        aplicarMascaraData(dataNasc)

        btnVoltar.setOnClickListener {

            if (SessionManager.isLoggedIn()) {
                startActivity(Intent(requireContext(), HomeProfissional::class.java))

            } else {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }

        }

        btnEnviar.setOnClickListener {
            if (validarCampos()) {
                val idSelectedSexo = sexoGroup.checkedRadioButtonId
                val selectedSexo = view.findViewById<RadioButton>(idSelectedSexo)

                viewModel.nome.value = editTextNome.text.toString().trim()
                viewModel.cpf.value = editTextCpf.text.toString().trim()
                viewModel.telefone.value = editTextCelular.text.toString().trim()
                viewModel.dataNasc.value = dataNasc.text.toString().trim()
                viewModel.sexo.value = selectedSexo.text.toString()

                val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
                viewPager?.let {
                    if (it.currentItem < (it.adapter?.itemCount ?: 1) - 1) {
                        it.currentItem = it.currentItem + 1
                    }
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nome = editTextNome.text.toString().trim()
        val cpf = editTextCpf.text.toString().trim()
        val celular = editTextCelular.text.toString().trim()
        val dataNascimento = dataNasc.text.toString().trim()
        val sexoSelecionado = sexoGroup.checkedRadioButtonId

        when {
            nome.isEmpty() -> {
                mostrarErro("Preencha o nome")
                return false
            }
            cpf.length != 14 -> { // ###.###.###-##
                mostrarErro("CPF inválido")
                return false
            }
            celular.length !in 13..15 -> { // (99) 99999-9999
                mostrarErro("Telefone inválido")
                return false
            }
            !validarData(dataNascimento) -> {
                mostrarErro("Data de nascimento inválida (dd/MM/yyyy)")
                return false
            }
            sexoSelecionado == -1 -> {
                mostrarErro("Selecione o sexo")
                return false
            }
        }
        return true
    }

    private fun validarData(data: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                isLenient = false
            }
            val nascimento = sdf.parse(data) ?: return false

            val hoje = Calendar.getInstance()
            val calNasc = Calendar.getInstance().apply { time = nascimento }

            // 1) Não aceitar data no futuro
            if (nascimento.after(hoje.time)) return false

            // 2) Não aceitar datas muito antigas (ex.: antes de 01/01/1900)
            val min = Calendar.getInstance().apply { set(1900, Calendar.JANUARY, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
            if (nascimento.before(min.time)) return false

            // 3) Não aceitar idades irreais (ex.: > 120 anos)
            val idade = run {
                var anos = hoje.get(Calendar.YEAR) - calNasc.get(Calendar.YEAR)
                // Ajuste se ainda não fez aniversário no ano corrente
                val antesDoAniversario = hoje.get(Calendar.DAY_OF_YEAR) < calNasc.get(Calendar.DAY_OF_YEAR)
                if (antesDoAniversario) anos--
                anos
            }

            idade in 0..120
        } catch (e: Exception) {
            false
        }
    }


    private fun mostrarErro(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }

    private fun aplicarMascaraCpf(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                // Mantém apenas dígitos e limita a 11
                val str = s.toString().filter { it.isDigit() }.take(11)
                val mask = StringBuilder()
                for (i in str.indices) {
                    mask.append(str[i])
                    if (i == 2 || i == 5) mask.append(".")
                    if (i == 8) mask.append("-")
                }

                editText.setText(mask.toString())
                editText.setSelection(mask.length)
                isUpdating = false
            }
        })
    }

    private fun aplicarMascaraCelular(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val maxDigits = 11 // DDD + número (ex: 11999999999)

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                // Mantém apenas dígitos e limita à quantidade máxima
                val digits = s.toString().filter { it.isDigit() }.take(maxDigits)
                val mask = StringBuilder()

                for (i in digits.indices) {
                    when (i) {
                        0 -> mask.append("(").append(digits[i])
                        1 -> mask.append(digits[i]).append(") ")
                        6 -> mask.append(digits[i]).append("-")
                        else -> mask.append(digits[i])
                    }
                }

                editText.setText(mask.toString())
                editText.setSelection(mask.length)
                isUpdating = false
            }
        })
    }


    private fun aplicarMascaraData(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true
                val str = s.toString().filter { it.isDigit() }
                val mask = StringBuilder()
                for (i in str.indices) {
                    mask.append(str[i])
                    if (i == 1 || i == 3) mask.append("/")
                }
                editText.setText(mask.toString())
                editText.setSelection(editText.text.length)
                isUpdating = false
            }
        })
    }
}
