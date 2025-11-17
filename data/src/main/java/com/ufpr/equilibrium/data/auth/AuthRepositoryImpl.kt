package com.ufpr.equilibrium.data.auth

import android.util.Base64
import com.ufpr.equilibrium.core.common.Result
import com.ufpr.equilibrium.data.remote.PessoasService
import com.ufpr.equilibrium.data.remote.dto.LoginRequestDto
import com.ufpr.equilibrium.domain.auth.AuthRepository
import com.ufpr.equilibrium.domain.model.UserSession
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: PessoasService
) : AuthRepository {

    override suspend fun login(cpf: String, password: String): Result<UserSession> {
        return try {
            val result = service.login(LoginRequestDto(cpf, password))
            val token = result.access_token
            val payload = decodeJwtPayload(token)

            val session = UserSession(
                token = token,
                id = payload?.optString("sub"),
                cpf = payload?.optString("cpf"),
                fullName = payload?.optString("username"),
                phone = payload?.optString("phone"),
                gender = payload?.optString("gender"),
                role = payload?.optString("role")
            )
            Result.Success(session)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    private fun decodeJwtPayload(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            JSONObject(payload)
        } catch (_: Exception) {
            null
        }
    }
}


