package com.ufpr.equilibrium.feature_questionnaire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ufpr.equilibrium.R

class QuestionnaireAdapter(
    private val questions: List<Question>,
    private val onAnswerChanged: (questionId: Int, selectedIndex: Int, score: Int, note: String?) -> Unit
) : RecyclerView.Adapter<QuestionnaireAdapter.VH>() {

    // guarda estado local (útil para rolagem)
    private val selectedIndices = mutableMapOf<Int, Int>().apply {
        questions.forEach { put(it.id, -1) }
    }
    private val notes = mutableMapOf<Int, String?>()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
        val rgOptions: RadioGroup = view.findViewById(R.id.rgOptions)
        val etNote: EditText = view.findViewById(R.id.etNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val q = questions[position]
        holder.tvQuestion.text = "${position + 1}. ${q.text}"

        // limpar RadioGroup atual (reuso de views)
        holder.rgOptions.removeAllViews()

        q.options.forEachIndexed { idx, opt ->
            val rb = RadioButton(holder.itemView.context).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = opt.text
                // armazenar o score/índice como tag para recuperar depois
                tag = Pair(idx, opt.score)
                isClickable = true
                isFocusable = true
            }
            holder.rgOptions.addView(rb)
        }

        // visibilidade campo de nota
        holder.etNote.visibility = if (q.allowNote) View.VISIBLE else View.GONE
        holder.etNote.setText(notes[q.id] ?: "")

        // restaurar seleção se houver
        val prevSelected = selectedIndices[q.id] ?: -1
        if (prevSelected >= 0 && prevSelected < holder.rgOptions.childCount) {
            val rb = holder.rgOptions.getChildAt(prevSelected) as RadioButton
            rb.isChecked = true
        } else holder.rgOptions.clearCheck()

        // listener de seleção
        holder.rgOptions.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                // nenhuma seleção
                selectedIndices[q.id] = -1
                onAnswerChanged(q.id, -1, 0, holder.etNote.text?.toString())
            } else {
                val rb = group.findViewById<RadioButton>(checkedId)
                val (index, score) = rb.tag as Pair<Int, Int>
                selectedIndices[q.id] = index
                onAnswerChanged(q.id, index, score, holder.etNote.text?.toString())
            }
        }

        // atualizar nota ao digitar
        holder.etNote.doAfterTextChanged { text ->
            notes[q.id] = text?.toString()
            val sel = selectedIndices[q.id] ?: -1
            val score = if (sel >= 0) q.options[sel].score else 0
            onAnswerChanged(q.id, sel, score, notes[q.id])
        }

        // permitir clicar no card para abrir opções (usabilidade)
        holder.itemView.setOnClickListener { holder.rgOptions.performClick() }
    }

    override fun getItemCount(): Int = questions.size
}
