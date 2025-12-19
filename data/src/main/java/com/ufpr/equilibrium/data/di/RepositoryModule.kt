package com.ufpr.equilibrium.data.di

import com.ufpr.equilibrium.data.auth.AuthRepositoryImpl
import com.ufpr.equilibrium.data.repository.PatientRepositoryImpl
import com.ufpr.equilibrium.data.repository.QuestionnaireRepositoryImpl
import com.ufpr.equilibrium.domain.auth.AuthRepository
import com.ufpr.equilibrium.domain.repository.PatientRepository
import com.ufpr.equilibrium.domain.repository.QuestionnaireRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindPatientRepository(impl: PatientRepositoryImpl): PatientRepository
    
    @Binds
    @Singleton
    abstract fun bindQuestionnaireRepository(impl: QuestionnaireRepositoryImpl): QuestionnaireRepository
}


