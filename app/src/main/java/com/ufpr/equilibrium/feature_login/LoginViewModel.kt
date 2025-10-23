package com.ufpr.equilibrium.feature_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufpr.equilibrium.core.common.Result
import com.ufpr.equilibrium.domain.auth.LoginUseCase
import com.ufpr.equilibrium.domain.model.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val session: UserSession) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            when (val result = loginUseCase(username, password)) {
                is Result.Success -> _uiState.value = LoginUiState.Success(result.value)
                is Result.Error -> _uiState.value = LoginUiState.Error(result.cause.message ?: "Erro desconhecido")
            }
        }
    }
}


