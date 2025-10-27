package com.ufpr.equilibrium.domain.auth

/**
 * Abstraction to provide current auth token to networking layer.
 */
interface TokenProvider {
    fun getToken(): String?
}


