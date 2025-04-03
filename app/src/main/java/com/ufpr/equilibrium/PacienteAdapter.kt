package com.ufpr.equilibrium


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PacienteAdapter (
    private val context: Context,
    private val pacientes: List<Paciente>

) : RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.tv_nome_paciente)
        val info: TextView = itemView.findViewById(R.id.tv_info_paciente)
        val btnTug: Button = itemView.findViewById(R.id.btn_tug)
        val btn5sts: Button = itemView.findViewById(R.id.btn_5sts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]

        holder.nome.text = paciente.cpf
        holder.info.text = "Idade: ${paciente.idade} anos\nPeso: ${paciente.peso}kg"

        holder.btnTug.setOnClickListener {
            val intent = Intent(context, TugInstruction::class.java)
            context.startActivity(intent)
        }

        holder.btn5sts.setOnClickListener {
            val intent = Intent(context, FtstsInstruction::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pacientes.size
}
