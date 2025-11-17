package com.ufpr.equilibrium.data.network

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor que verifica se o token foi expirado (401 Unauthorized)
 * e redireciona para a tela de login
 */
class UnauthorizedInterceptor @Inject constructor(
    private val application: Application
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Verifica se o token expirou (401 Unauthorized)
        // Ignora a rota de login, pois 401 lá significa credenciais inválidas, não token expirado
        val url = request.url.toString()
        val isLoginEndpoint = url.contains("auth/login", ignoreCase = true)
        
        if (response.code == 401 && !isLoginEndpoint) {
            // Limpa a sessão usando SharedPreferences diretamente
            val prefs: SharedPreferences = application.getSharedPreferences("user_session", Application.MODE_PRIVATE)
            prefs.edit().clear().apply()
            
            // Notifica o usuário e redireciona para Login
            Handler(Looper.getMainLooper()).post {
                try {
                    Toast.makeText(
                        application,
                        "Sessão expirada. Faça login novamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Usa reflection para acessar LoginActivity do módulo app
                    val loginActivityClass = Class.forName("com.ufpr.equilibrium.feature_login.LoginActivity")
                    val intent = Intent(application, loginActivityClass).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    application.startActivity(intent)
                } catch (e: Exception) {
                    // Se não conseguir acessar LoginActivity, apenas loga o erro
                    android.util.Log.e("UnauthorizedInterceptor", "Erro ao redirecionar para login", e)
                }
            }
        }
        
        return response
    }
}

