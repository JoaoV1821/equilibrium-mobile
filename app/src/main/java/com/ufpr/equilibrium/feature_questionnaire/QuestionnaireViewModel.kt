package com.ufpr.equilibrium.feature_questionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionnaireViewModel(private val dao: AnswerDao) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    // mapa questionId -> AnswerEntity (em memória enquanto usuário responde)
    private val answers = mutableMapOf<Int, AnswerEntity>()

    fun loadQuestions(list: List<Question>) {
        _questions.value = list
    }

    // chamado pelo Adapter
    fun onAnswerChanged(questionId: Int, selectedIndex: Int, score: Int, note: String?) {
        val answer = AnswerEntity(
            questionId = questionId,
            selectedOptionIndex = selectedIndex,
            score = score,
            note = note
        )
        answers[questionId] = answer
        // opcional: salvar imediatamente no banco (debounce se preferir)
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(answer)
        }
    }

    suspend fun totalScore(): Int {
        // método que soma respostas persistidas no DB (mais robusto)
        val saved = dao.getAnswersFor("ivcf20")
        return saved.sumOf { it.score }
    }

    fun allQuestionsAnswered(): Boolean {
        val qs = _questions.value
        return qs.all { answers[it.id]?.selectedOptionIndex?.let { idx -> idx >= 0 } == true }
    }

    fun clearResponses() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clear("ivcf20")
            answers.clear()
        }
    }
}
