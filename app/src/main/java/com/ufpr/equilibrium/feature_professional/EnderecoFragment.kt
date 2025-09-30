package com.ufpr.equilibrium.feature_professional


import android.content.Intent
import android.os.Bundle
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
import com.ufpr.equilibrium.network.RetrofitClient
import com.ufpr.equilibrium.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class EnderecoFragment : Fragment() {

    private lateinit var cep: EditText
    private lateinit var numero: EditText
    private lateinit var rua: EditText
    private lateinit var complemento: EditText
    private lateinit var bairro : EditText
    private lateinit var cidade: EditText
    private lateinit var uf: EditText

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

        val viewModel = ViewModelProvider(requireActivity()).get(FormViewModel::class.java)

        btnVoltar.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

            viewPager?.let {
                if (it.currentItem > 0) it.currentItem = it.currentItem - 1
            }
        }

        btnEnviar.setOnClickListener {

            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

            viewModel.cep.value = cep.text.toString()
            viewModel.numero.value = numero.text.toString().toInt()
            viewModel.rua.value = rua.text.toString()
            viewModel.complemento.value = complemento.text.toString()
            viewModel.bairro.value = bairro.text.toString()
            viewModel.cidade.value = cidade.text.toString()
            viewModel.uf.value = uf.text.toString()

            viewPager?.let {

                if (it.currentItem < (it.adapter?.itemCount ?: 1) - 1) {
                    it.currentItem = it.currentItem + 1

                } else {

                    val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    val sdfOutput =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    sdfOutput.timeZone = TimeZone.getTimeZone("UTC")

                    val date = sdfInput.parse(viewModel.dataNasc.value ?: "")
                    val birthdayIso = date?.let { sdfOutput.format(it) } ?: ""

                    val user = User (

                        fullName = viewModel.nome.value.toString(),
                        cpf = viewModel.cpf.value.toString().replace(Regex("[.-]"), ""),
                        gender = if (viewModel.sexo.value.toString() == "Masculino") "MALE" else "FEMALE")

                    val paciente = PacienteModel (

                        birthday = birthdayIso,
                        weight = viewModel.peso.value.toString().toInt(),
                        height = viewModel.altura.value.toString().toFloat(),
                        zipCode = viewModel.cep.value.toString(),
                        street = viewModel.rua.value.toString(),
                        number = viewModel.numero.value.toString(),
                        complement = viewModel.complemento.value.toString(),
                        neighborhood = viewModel.bairro.value.toString(),
                        city = viewModel.cidade.value.toString(),
                        state = viewModel.estado.value.toString(),
                        socio_economic_level = viewModel.nivelSocio.value.toString(),
                        scholarship = viewModel.escolaridade.value.toString(),
                        user = user

                    )

                    println(paciente)

                    val api = RetrofitClient.instancePessoasAPI

                    api.postPatient(paciente, "Bearer " + SessionManager.token).enqueue(object : Callback<PacienteModel> {
                        override fun onResponse(call: Call<PacienteModel>, response: Response<PacienteModel>) {

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
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Erro")
                                    .setMessage("Erro ao enviar paciente: ${response.code()}")
                                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<PacienteModel>, t: Throwable) {
                            Toast.makeText(requireContext(), "Falha na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                   })
                }
            }
        }
    }
}
