package com.ufpr.equilibrium.network

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ufpr.equilibrium.EquilibriumApp
import com.ufpr.equilibrium.R
import com.ufpr.equilibrium.feature_login.LoginActivity
import com.ufpr.equilibrium.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val hasAuthHeader = original.header("Authorization") != null
        val token = SessionManager.token

        val request = if (!hasAuthHeader && !token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}

class UnauthorizedInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Verifica se o token expirou (401 Unauthorized)
        // Ignora a rota de login, pois 401 lá significa credenciais inválidas, não token expirado
        val url = request.url.toString()
        val isLoginEndpoint = url.contains("auth/login", ignoreCase = true)
        
        if (response.code == 401 && !isLoginEndpoint) {
            // Clear session
            SessionManager.clearSession()

            // Notify user and redirect to Login
            val ctx = EquilibriumApp.appContext
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(ctx, ctx.getString(R.string.error_unauthorized), Toast.LENGTH_SHORT).show()
                val intent = Intent(ctx, LoginActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                ctx.startActivity(intent)
            }
        }
        return response
    }
}


