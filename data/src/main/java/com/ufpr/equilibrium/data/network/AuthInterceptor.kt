package com.ufpr.equilibrium.data.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import com.ufpr.equilibrium.domain.auth.TokenProvider

/**
 * Adds Authorization header if a token is available from the provider.
 */
 
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
    
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider.getToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else original
        return chain.proceed(request)
    }
}


