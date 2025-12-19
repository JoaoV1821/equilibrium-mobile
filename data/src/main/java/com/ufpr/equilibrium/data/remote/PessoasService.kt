package com.ufpr.equilibrium.data.remote

import com.ufpr.equilibrium.data.remote.dto.LoginRequestDto
import com.ufpr.equilibrium.data.remote.dto.LoginResultDto
import com.ufpr.equilibrium.data.remote.dto.PatientDto
import com.ufpr.equilibrium.data.remote.dto.PatientRegistrationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service interface for Pessoas API.
 */
interface PessoasService {
    
    // ========== Authentication ==========
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResultDto
    
    // ========== Patient Endpoints ==========
    
    /**
     * Get list of patients with optional pagination and CPF filter.
     */
    @GET("patient")
    suspend fun getPatients(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("cpf") cpf: String? = null
    ): PatientsEnvelope
    
    /**
     * Create a new patient.
     */
    @POST("patient")
    suspend fun postPatient(@Body request: PatientRegistrationDto): PatientRegistrationDto
}

/**
 * Envelope response for patient list with pagination metadata.
 */
data class PatientsEnvelope(
    val data: List<PatientDto>,
    val meta: PaginationMeta?
)

/**
 * Pagination metadata.
 */
data class PaginationMeta(
    val total: Int?,
    val page: Int?,
    val pageSize: Int?,
    val lastPage: Int?
)


