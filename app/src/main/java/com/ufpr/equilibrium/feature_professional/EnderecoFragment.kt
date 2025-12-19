package com.ufpr.equilibrium.feature_professional


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_login.LoginActivity
import com.ufpr.equilibrium.network.PessoasAPI
import com.ufpr.equilibrium.utils.ErrorMessages
import com.ufpr.equilibrium.utils.BrazilianState
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import com.ufpr.equilibrium.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.widget.ArrayAdapter


@AndroidEntryPoint
class EnderecoFragment : Fragment() {

    @Inject lateinit var pessoasAPI: PessoasAPI

    private lateinit var cep: EditText
    private lateinit var numero: EditText
    private lateinit var rua: EditText
    private lateinit var complemento: EditText
    private lateinit var bairro : EditText
    private lateinit var cidade: EditText
    private lateinit var uf: EditText
    private lateinit var loadingOverlay: View

    private lateinit var ruaAdapter: ArrayAdapter<String>
    private lateinit var bairroAdapter: ArrayAdapter<String>
    private lateinit var cidadeAdapter: ArrayAdapter<String>
    private lateinit var ufAdapter: ArrayAdapter<String>

    // Places removido

    override fun onCreateView (


        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        return inflater.inflate(R.layout.fragment_endereco, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val btnVoltar = view.findViewById<MaterialButton>(R.id.voltar)
        val btnEnviar = view.findViewById<MaterialButton>(R.id.enviar)

        cep = view.findViewById(R.id.cep)
        numero = view.findViewById(R.id.numero)
        rua = view.findViewById(R.id.rua)
        complemento = view.findViewById(R.id.complemento)
        bairro = view.findViewById(R.id.bairro)
        cidade = view.findViewById(R.id.cidade)
        uf = view.findViewById(R.id.estado)

        // Setup adapters for AutoCompleteTextViews
        ruaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        bairroAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        cidadeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        
        // Setup UF adapter with Brazilian states
        val allStates = BrazilianState.getAllUFs()
        ufAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, allStates.toMutableList())

        (rua as? android.widget.AutoCompleteTextView)?.setAdapter(ruaAdapter)
        (bairro as? android.widget.AutoCompleteTextView)?.setAdapter(bairroAdapter)
        (cidade as? android.widget.AutoCompleteTextView)?.setAdapter(cidadeAdapter)
        
        // Configure UF field with custom behavior
        (uf as? android.widget.AutoCompleteTextView)?.apply {
            setAdapter(ufAdapter)
            threshold = 1 // Show suggestions after 1 character
            
            // Limit to 2 uppercase characters and filter states
            addTextChangedListener(object : TextWatcher {
                private var isUpdating = false
                
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isUpdating) return
                    isUpdating = true
                    
                    val input = s.toString().uppercase().take(2)
                    if (input != s.toString()) {
                        setText(input)
                        setSelection(input.length)
                    }
                    
                    // Filter states based on input
                    val filtered = if (input.isEmpty()) {
                        allStates
                    } else {
                        allStates.filter { it.startsWith(input) }
                    }
                    ufAdapter.clear()
                    ufAdapter.addAll(filtered)
                    ufAdapter.notifyDataSetChanged()
                    
                    isUpdating = false
                }
            })
        }

        loadingOverlay = view.findViewById(R.id.loading_overlay)

        val viewModel = ViewModelProvider(requireActivity()).get(FormViewModel::class.java)

        // Sem Google Places: apenas ViaCEP

        // Busca CEP automático quando completar 8 dígitos
        cep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val digits = s?.toString()?.replace(Regex("[^\\d]"), "") ?: ""
                if (digits.length == 8) fetchAddressByCep(digits)
            }
        })

        // Sem Google Places: sugestões apenas do ViaCEP

        btnVoltar.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

            viewPager?.let {
                if (it.currentItem > 0) it.currentItem = it.currentItem - 1
            }
        }

        btnEnviar.setOnClickListener {
            // Validate all required fields before proceeding
            if (!validateAddressFields()) {
                return@setOnClickListener
            }

            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

            // Safely assign values to ViewModel with validation
            try {
                viewModel.cep.value = cep.text.toString()
                viewModel.numero.value = numero.text.toString().toIntOrNull() ?: 0
                viewModel.rua.value = rua.text.toString()
                viewModel.complemento.value = complemento.text.toString()
                viewModel.bairro.value = bairro.text.toString()
                viewModel.cidade.value = cidade.text.toString()
                viewModel.uf.value = uf.text.toString()

                viewPager?.let {

                    if (it.currentItem < (it.adapter?.itemCount ?: 1) - 1) {
                        it.currentItem = it.currentItem + 1

                    } else {
                        submitPatientData()
                    }
                }
            } catch (e: Exception) {
                Log.e("EnderecoFragment", "Error processing form data", e)
                Toast.makeText(requireContext(), "Erro ao processar dados do formulário", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateAddressFields(): Boolean {
        val cepText = cep.text.toString().trim()
        val numeroText = numero.text.toString().trim()
        val ruaText = rua.text.toString().trim()
        val bairroText = bairro.text.toString().trim()
        val cidadeText = cidade.text.toString().trim()
        val ufText = uf.text.toString().trim()

        // Check for empty required fields
        when {
            cepText.isEmpty() -> {
                cep.error = "CEP é obrigatório"
                cep.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha o CEP", Toast.LENGTH_SHORT).show()
                return false
            }
            cepText.replace(Regex("[^\\d]"), "").length != 8 -> {
                cep.error = "CEP deve ter 8 dígitos"
                cep.requestFocus()
                Toast.makeText(requireContext(), "CEP inválido. Digite 8 dígitos", Toast.LENGTH_SHORT).show()
                return false
            }
            numeroText.isEmpty() -> {
                numero.error = "Número é obrigatório"
                numero.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha o número", Toast.LENGTH_SHORT).show()
                return false
            }
            numeroText.toIntOrNull() == null -> {
                numero.error = "Número inválido"
                numero.requestFocus()
                Toast.makeText(requireContext(), "Digite um número válido", Toast.LENGTH_SHORT).show()
                return false
            }
            ruaText.isEmpty() -> {
                rua.error = "Rua é obrigatória"
                rua.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha a rua", Toast.LENGTH_SHORT).show()
                return false
            }
            bairroText.isEmpty() -> {
                bairro.error = "Bairro é obrigatório"
                bairro.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha o bairro", Toast.LENGTH_SHORT).show()
                return false
            }
            cidadeText.isEmpty() -> {
                cidade.error = "Cidade é obrigatória"
                cidade.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha a cidade", Toast.LENGTH_SHORT).show()
                return false
            }
            ufText.isEmpty() -> {
                uf.error = "Estado (UF) é obrigatório"
                uf.requestFocus()
                Toast.makeText(requireContext(), "Por favor, preencha o estado", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    private fun submitPatientData() {
        val viewModel = ViewModelProvider(requireActivity()).get(FormViewModel::class.java)

        try {
            val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sdfOutput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val date = sdfInput.parse(viewModel.dataNasc.value ?: "")
            val birthdayOnly = date?.let { sdfOutput.format(it) } ?: ""
            
            val zipDigitsOnly = viewModel.cep.value.toString().replace(Regex("[^\\d]"), "")

            val user = User(
                fullName = viewModel.nome.value.toString(),
                cpf = viewModel.cpf.value.toString().replace(Regex("[.-]"), ""),
                gender = if (viewModel.sexo.value.toString() == "Masculino") "MALE" else "FEMALE"
            )

            val paciente = PacienteModel(
                birthday = birthdayOnly,
                weight = viewModel.peso.value.toString().toInt(),
                height = viewModel.altura.value,
                zipCode = zipDigitsOnly,
                street = viewModel.rua.value.toString(),
                number = viewModel.numero.value.toString(),
                complement = viewModel.complemento.value.toString(),
                neighborhood = viewModel.bairro.value.toString(),
                city = viewModel.cidade.value.toString(),
                state = viewModel.uf.value.toString(),
                socio_economic_level = viewModel.nivelSocio.value.toString(),
                scholarship = viewModel.escolaridade.value.toString(),
                user = user
            )

            println(paciente)

            loadingOverlay.visibility = View.VISIBLE

            pessoasAPI.postPatient(paciente).enqueue(object : Callback<PacienteModel> {
                override fun onResponse(call: Call<PacienteModel>, response: Response<PacienteModel>) {
                    loadingOverlay.visibility = View.GONE

                    if (response.isSuccessful) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Cadastro concluído")
                            .setMessage("O paciente foi cadastrado com sucesso!")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()

                                if (SessionManager.isLoggedIn()) {
                                    val intent = Intent(requireContext(), HomeProfissional::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(requireContext(), LoginActivity::class.java)
                                    startActivity(intent)
                                }

                                requireActivity().finish()
                            }
                            .show()
                    } else {
                        val message = ErrorMessages.forHttpStatus(requireContext(), response.code())
                        AlertDialog.Builder(requireContext())
                            .setTitle("Erro")
                            .setMessage(message)
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                        Log.e("postPatient", response.errorBody()?.string().orEmpty())
                    }
                }

                override fun onFailure(call: Call<PacienteModel>, t: Throwable) {
                    loadingOverlay.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                    Log.e("postPatient", "Network error", t)
                }
            })
        } catch (e: Exception) {
            loadingOverlay.visibility = View.GONE
            Log.e("EnderecoFragment", "Error submitting patient data", e)
            AlertDialog.Builder(requireContext())
                .setTitle("Erro")
                .setMessage("Erro ao processar dados. Por favor, verifique todos os campos.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun fetchAddressByCep(cep: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://viacep.com.br/ws/$cep/json/")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("ViaCEP", "Falha ao buscar CEP", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) return
                    val body = it.body?.string() ?: return
                    val json = JSONObject(body)
                    if (json.optBoolean("erro", false)) return
                    val logradouro = json.optString("logradouro")
                    val bairroValue = json.optString("bairro")
                    val localidade = json.optString("localidade")
                    val ufValue = json.optString("uf")

                    requireActivity().runOnUiThread {
                        // Update text
                        if (logradouro.isNotBlank()) rua.setText(logradouro)
                        if (bairroValue.isNotBlank()) bairro.setText(bairroValue)
                        if (localidade.isNotBlank()) cidade.setText(localidade)
                        if (ufValue.isNotBlank()) uf.setText(ufValue)

                        // Update adapters with suggestions and show dropdowns
                        ruaAdapter.clear(); if (logradouro.isNotBlank()) ruaAdapter.add(logradouro)
                        bairroAdapter.clear(); if (bairroValue.isNotBlank()) bairroAdapter.add(bairroValue)
                        cidadeAdapter.clear(); if (localidade.isNotBlank()) cidadeAdapter.add(localidade)
                        ufAdapter.clear(); if (ufValue.isNotBlank()) ufAdapter.add(ufValue)

                        (rua as? android.widget.AutoCompleteTextView)?.showDropDown()
                        (bairro as? android.widget.AutoCompleteTextView)?.showDropDown()
                        (cidade as? android.widget.AutoCompleteTextView)?.showDropDown()
                        (uf as? android.widget.AutoCompleteTextView)?.showDropDown()
                    }
                }
            }
        })
    }

    // Sem Google Places
}
