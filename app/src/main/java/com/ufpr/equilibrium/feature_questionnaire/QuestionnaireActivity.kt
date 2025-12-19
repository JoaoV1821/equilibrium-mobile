package com.ufpr.equilibrium.feature_questionnaire

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.databinding.ActivityQuestionnaireBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionnaireActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionnaireBinding
    private val viewModel: QuestionnaireViewModel by viewModels()
  
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionnaireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load IVCF-20 questions via ViewModel
        viewModel.loadQuestions("ivcf20")

        // Flag to track if questionnaire is already set up
        var isSetupDone = false

        // Observe UI state for questions
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Only setup questionnaire ONCE when questions are loaded
                    if (!state.isLoading && state.questions.isNotEmpty() && !isSetupDone) {
                        setupQuestionnaire(state.questions)
                        isSetupDone = true
                    }
                    
                    state.error?.let { error ->
                        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.fabFinish.setOnClickListener {
            // Validate if all questions are answered
            if (!viewModel.isComplete()) {
                Snackbar.make(binding.root, R.string.answer_all_questions, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            
            lifecycleScope.launch {
                val total = viewModel.getTotalScore()
                val interpretation = viewModel.getInterpretation()
                
                // Build result message
                val message = buildString {
                    appendLine(getString(R.string.result_score, total, 14))
                    appendLine()
                    appendLine(getString(R.string.result_interpretation, interpretation))
                }
                
                // Show result dialog and ask if they want to submit
                AlertDialog.Builder(this@QuestionnaireActivity)
                    .setTitle("Resultado IVCF-20")
                    .setMessage(message)
                    .setPositiveButton("Enviar") { _, _ ->
                        submitQuestionnaire()
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }
    
    private fun submitQuestionnaire() {
        // Show loading feedback
        android.widget.Toast.makeText(this, "Enviando questionário...", android.widget.Toast.LENGTH_SHORT).show()
        binding.fabFinish.isEnabled = false
        
        // Get participant and professional IDs
        val participantId = com.ufpr.equilibrium.utils.PacienteManager.uuid?.toString()
        val professionalId = com.ufpr.equilibrium.utils.SessionManager.user?.id
        val token = com.ufpr.equilibrium.utils.SessionManager.token
        
        if (participantId == null) {
            Snackbar.make(binding.root, "Erro: Participante não identificado", Snackbar.LENGTH_LONG).show()
            binding.fabFinish.isEnabled = true
            return
        }
        
        if (professionalId == null || token == null) {
            Snackbar.make(binding.root, "Erro: Usuário não autenticado", Snackbar.LENGTH_LONG).show()
            binding.fabFinish.isEnabled = true
            return
        }
        
        viewModel.submitAnswers(
            participantId = participantId,
            healthProfessionalId = professionalId,
            token = token,
            onSuccess = { message ->
                binding.fabFinish.isEnabled = true
                
                AlertDialog.Builder(this@QuestionnaireActivity)
                    .setTitle("Sucesso!")
                    .setMessage(message)
                    .setPositiveButton("Ok") { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            },
            onError = { error ->
                binding.fabFinish.isEnabled = true
                
                AlertDialog.Builder(this@QuestionnaireActivity)
                    .setTitle("Erro ao Enviar")
                    .setMessage(error)
                    .setPositiveButton("Tentar Novamente") { _, _ ->
                        submitQuestionnaire()
                    }
                    .setNegativeButton("Cancelar") { _, _ ->
                        finish()
                    }
                    .show()
            }
        )
    }
    
    private fun setupQuestionnaire(questions: List<Question>) {
        val adapter = QuestionnaireAdapter(questions) { qid, idx, score, note ->
            viewModel.onAnswerChanged(qid, idx, score, note)
        }
        
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = adapter
    }
}
