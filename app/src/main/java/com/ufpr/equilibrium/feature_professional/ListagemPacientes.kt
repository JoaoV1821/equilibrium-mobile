package com.ufpr.equilibrium.feature_professional

// ListagemPacientes.kt
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_paciente.PacienteAdapter
import com.ufpr.equilibrium.network.PessoasAPI
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import com.ufpr.equilibrium.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID

// ---- MODELOS alinhados ao JSON -------------------

data class Meta(
    val total: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val lastPage: Int? = null
)

/** ESTE PacienteModel corresponde ao JSON do seu log */
data class PacienteModelList(
    val id: String?,
    val birthday: String?,
    val weight: Int?,       // no JSON veio Int
    val height: Int?,       // no JSON veio Int (175, 172, 1, ...)
    val zipCode: String?,
    val street: String?,
    val number: String?,
    val complement: String?,
    val neighborhood: String?,
    val socio_economic_level: String?,
    val scholarship: String?,
    val city: String?,
    val state: String?,
    val cpf: String?,
    val fullName: String?,
    val gender: String? = null,
    val role: String? = null,
    val phone: String? = null,
    val updatedAt: String? = null,
    val active: Boolean? = null
)

// Seu modelo de domínio usado pela UI/Adapter


// ---------------------------------------------------

@AndroidEntryPoint
class ListagemPacientes : AppCompatActivity() {
    @Inject lateinit var pessoasAPI: PessoasAPI
    private lateinit var recyclerView: RecyclerView
    private lateinit var pacienteAdapter: PacienteAdapter
    private val pacientes = mutableListOf<Paciente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem_pacientes)

        recyclerView = findViewById(R.id.rv_pacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pacienteAdapter = PacienteAdapter(this, pacientes)
        recyclerView.adapter = pacienteAdapter

        getPacientes()

        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Sim") { _, _ ->
            SessionManager.clearSession()
            startActivity(Intent(this@ListagemPacientes, HomeProfissional::class.java))
        }
        builder.setNegativeButton("Não") { _, _ -> }

        val searchInput = findViewById<TextInputEditText>(R.id.searchInput)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pacienteAdapter.filtrarPorCpf(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@ListagemPacientes, HomeProfissional::class.java)
                startActivity(intent)
            }
        })
    }

    private fun getPacientes() {
        val call = pessoasAPI.getPacientes()

        call.enqueue(object : Callback<PacientesEnvelope> {
            override fun onResponse(
                call: Call<PacientesEnvelope>,
                response: Response<PacientesEnvelope>
            ) {
                if (response.isSuccessful) {
                    val listaApi = response.body()?.data.orEmpty()
                    val lista = listaApi.map { it.toDomain() }

                    pacientes.clear()
                    pacientes.addAll(lista)
                    pacienteAdapter.atualizarLista(lista)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Erro ao buscar pacientes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<PacientesEnvelope>, t: Throwable) {
                Log.e("Erro", "Falha ao buscar pacientes", t)
                Toast.makeText(
                    applicationContext,
                    "Falha na conexão. Tente novamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // --------- MAPPER SIMPLES e SEGURO (sem user) ----------------

    private fun PacienteModelList.toDomain(): Paciente = Paciente(
        id       = parseUuidOrFromCpf(id, cpf),
        fullName = fullName.orEmpty(),
        cpf      = cpf.orEmpty(),
        age      = parseAgeFromIso(birthday),
        weight   = (weight ?: 0).toFloat(),
        height   = normalizeHeight(height),  // trata cm vs m=1/2/… do seu dataset
        downFall = false                     // ajuste quando houver origem real
    )

    private fun parseUuidOrFromCpf(idStr: String?, cpf: String?): UUID {
        // tenta o id; se não der, gera UUID determinístico pelo CPF; fallback random
        parseUuid(idStr)?.let { return it }
        if (!cpf.isNullOrBlank()) return UUID.nameUUIDFromBytes(cpf.toByteArray())
        return UUID.randomUUID()
    }

    private fun parseUuid(idStr: String?): UUID? = try {
        if (idStr.isNullOrBlank()) null else UUID.fromString(idStr)
    } catch (_: Exception) { null }

    private fun parseAgeFromIso(iso: String?): Int {
        return try {
            if (iso.isNullOrBlank()) return 0
            // Ex.: "1953-07-15T00:00:00.000Z"
            val instant = Instant.parse(iso)
            val birth = instant.atZone(ZoneId.systemDefault()).toLocalDate()
            Period.between(birth, LocalDate.now()).years
        } catch (_: Exception) {
            // Tenta "yyyy-MM-dd" simples, se vier assim algum dia
            try {
                val birth = LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE)
                Period.between(birth, LocalDate.now()).years
            } catch (_: Exception) { 0 }
        }
    }

    private fun normalizeHeight(h: Int?): Int {
        val v = h ?: 0
        // Seus dados mostram 175/172 (cm) e 1 (provável metro).
        // Heurística: >= 100 => já em cm; 1..3 => metros -> cm; senão retorna como está.
        return when {
            v >= 100 -> v
            v in 1..3 -> v * 100
            else -> v
        }
    }
}
