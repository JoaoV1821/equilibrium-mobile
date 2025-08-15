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


class PacienteAdapter(
    private val context: Context,
    private var pacientes: List<Paciente>
) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    private var pacientesFiltrados: List<Paciente> = pacientes.toList()

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
        holder.nome.text = paciente.name
        holder.info.text = "CPF: ${paciente.cpf}\nIdade: ${paciente.age}"

        holder.btn5sts.setOnClickListener {
            val intent = Intent(context, FtstsInstruction::class.java)
            PacienteManager.cpf = paciente.cpf
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = pacientesFiltrados.size

    // Atualiza a lista completa
    fun atualizarLista(novaLista: List<Paciente>) {
        this.pacientes = novaLista
        this.pacientesFiltrados = novaLista
        notifyDataSetChanged()
    }

    // Filtra por CPF
    fun filtrarPorCpf(cpf: String) {
        pacientesFiltrados = if (cpf.isBlank()) {
            pacientes
        } else {
            pacientes.filter { it.cpf.contains(cpf, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
