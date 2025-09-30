package com.ufpr.equilibrium.feature_paciente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufpr.equilibrium.feature_ftsts.FtstsInstruction
import com.ufpr.equilibrium.utils.PacienteManager
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_professional.Paciente


class PacienteAdapter(
    private val context: Context,
    private var pacientes: List<Paciente>
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    private var pacientesFiltrados: MutableList<Paciente> = pacientes.toMutableList()

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.tv_nome_paciente)
        val info: TextView = itemView.findViewById(R.id.tv_info_paciente)
        val btn5sts: Button = itemView.findViewById(R.id.btn_5sts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        PacienteManager.init(context)
        val view = LayoutInflater.from(context).inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientesFiltrados[position]

        holder.nome.text = paciente.fullName
        holder.info.text = "CPF: ${paciente.cpf}"

        holder.btn5sts.setOnClickListener {
            PacienteManager.uuid = paciente.id
            context.startActivity(Intent(context, FtstsInstruction::class.java))
        }
    }

    override fun getItemCount(): Int = pacientesFiltrados.size

    /** Atualiza a lista completa exibida pelo adapter */
    fun atualizarLista(novaLista: List<Paciente>) {
        this.pacientes = novaLista
        this.pacientesFiltrados = novaLista.toMutableList()
        notifyDataSetChanged()
    }

    /** Filtra por CPF (ignora máscara/pontos/traços) */
    fun filtrarPorCpf(cpf: String) {
        val query = cpf.filter { it.isDigit() }
        pacientesFiltrados = if (query.isBlank()) {
            pacientes.toMutableList()
        } else {
            pacientes.filter { it.cpf.filter(Char::isDigit).contains(query) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
