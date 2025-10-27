package com.ufpr.equilibrium.di

import com.ufpr.equilibrium.network.PessoasAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LegacyApiModule {

    @Provides
    @Singleton
    fun providePessoasApi(retrofit: Retrofit): PessoasAPI =
        retrofit.create(PessoasAPI::class.java)
}


