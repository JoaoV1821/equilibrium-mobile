package com.ufpr.equilibrium.domain.auth

import com.ufpr.equilibrium.core.common.Result
import com.ufpr.equilibrium.domain.model.UserSession

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<UserSession> {
        return repository.login(username, password)
    }
}


