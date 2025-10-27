package com.ufpr.equilibrium.di

import com.ufpr.equilibrium.domain.auth.TokenProvider
import com.ufpr.equilibrium.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

class SessionTokenProvider : TokenProvider {
    override fun getToken(): String? = SessionManager.token
}

@Module
@InstallIn(SingletonComponent::class)
object TokenModule {

    @Provides
    @Singleton
    fun provideTokenProvider(): TokenProvider = SessionTokenProvider()
}


