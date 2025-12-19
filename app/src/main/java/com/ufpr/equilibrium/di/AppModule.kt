package com.ufpr.equilibrium.di

import com.ufpr.equilibrium.domain.auth.AuthRepository
import com.ufpr.equilibrium.domain.auth.LoginUseCase
import com.ufpr.equilibrium.domain.repository.PatientRepository
import com.ufpr.equilibrium.domain.repository.QuestionnaireRepository
import com.ufpr.equilibrium.domain.usecase.patient.*
import com.ufpr.equilibrium.domain.usecase.questionnaire.*
import com.ufpr.equilibrium.domain.usecase.validation.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing use cases for dependency injection.
 * Use cases encapsulate business logic and are injected into ViewModels.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ========== Authentication Use Cases ==========
    
    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase = 
        LoginUseCase(repository)

    // ========== Patient Use Cases ==========
    
    @Provides
    @Singleton
    fun provideGetPatientsUseCase(repository: PatientRepository): GetPatientsUseCase =
        GetPatientsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetPatientByCpfUseCase(repository: PatientRepository): GetPatientByCpfUseCase =
        GetPatientByCpfUseCase(repository)
    
    @Provides
    @Singleton
    fun provideCreatePatientUseCase(repository: PatientRepository): CreatePatientUseCase =
        CreatePatientUseCase(repository)

    // ========== Questionnaire Use Cases ==========
    
    @Provides
    @Singleton
    fun provideLoadQuestionsUseCase(repository: QuestionnaireRepository): LoadQuestionsUseCase =
        LoadQuestionsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideSaveAnswerUseCase(repository: QuestionnaireRepository): SaveAnswerUseCase =
        SaveAnswerUseCase(repository)
    
    @Provides
    @Singleton
    fun provideCalculateTotalScoreUseCase(): CalculateTotalScoreUseCase =
        CalculateTotalScoreUseCase()
    
    @Provides
    @Singleton
    fun provideCalculateGroupScoreUseCase(): CalculateGroupScoreUseCase =
        CalculateGroupScoreUseCase()
    
    @Provides
    @Singleton
    fun provideInterpretScoreUseCase(): InterpretScoreUseCase =
        InterpretScoreUseCase()
    
    @Provides
    @Singleton
    fun provideValidateCompletionUseCase(): ValidateCompletionUseCase =
        ValidateCompletionUseCase()

    // ========== Validation Use Cases ==========
    
    @Provides
    @Singleton
    fun provideValidateCpfUseCase(): ValidateCpfUseCase =
        ValidateCpfUseCase()
    
    @Provides
    @Singleton
    fun provideValidateBirthDateUseCase(): ValidateBirthDateUseCase =
        ValidateBirthDateUseCase()
}


