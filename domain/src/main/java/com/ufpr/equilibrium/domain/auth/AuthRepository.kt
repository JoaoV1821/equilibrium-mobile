package com.ufpr.equilibrium.domain.auth

import com.ufpr.equilibrium.core.common.Result
import com.ufpr.equilibrium.domain.model.UserSession

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<UserSession>
}


