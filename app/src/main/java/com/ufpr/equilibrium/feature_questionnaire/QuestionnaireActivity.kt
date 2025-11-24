package com.ufpr.equilibrium.feature_questionnaire

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ufpr.equilibrium.databinding.ActivityQuestionnaireBinding
import kotlinx.coroutines.launch

class QuestionnaireActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionnaireBinding
    private val viewModel: QuestionnaireViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = QuestionnaireDatabase.getInstance(applicationContext)
                return QuestionnaireViewModel(db.answerDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Exemplo: criar perguntas (substitua pelo JSON gerado do PDF)
        val questions = listOf (
            Question(1, "Perda de peso involuntária nos últimos 3 meses?", listOf(
                Option("Não", 0),
                Option("Sim — leve", 1),
                Option("Sim — moderado", 2),
                Option("Sim — severo", 3)
            )),

            Question(2, "Dificuldade para caminhar 400m?", listOf(
                Option("Não", 0),
                Option("Sim", 1)
            ), allowNote = true)
            // ... adicione todas as perguntas do PDF
        )

        viewModel.loadQuestions(questions)

        val adapter = QuestionnaireAdapter(questions) { qid, idx, score, note ->
            viewModel.onAnswerChanged(qid, idx, score, note)
        }

        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = adapter

        binding.fabFinish.setOnClickListener {
            // valida se todas respondidas (se necessário)
            if (!viewModel.allQuestionsAnswered()) {
                Snackbar.make(binding.root, "Responda todas as perguntas antes de concluir.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val total = viewModel.totalScore()
                // abrir tela de resultado ou mostrar diálogo
                val message = "Pontuação total: $total"
                AlertDialog.Builder(this@QuestionnaireActivity)
                    .setTitle("Resultado")
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .show()
            }
        }
    }
}
// Questionnaire feature removed. File kept empty to avoid build-time missing references.
