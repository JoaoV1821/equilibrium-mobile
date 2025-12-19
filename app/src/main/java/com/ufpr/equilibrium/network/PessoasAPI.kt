package com.ufpr.equilibrium.network

import com.ufpr.equilibrium.feature_healthUnit.HealthUnit
import com.ufpr.equilibrium.feature_professional.PacienteModel
import com.ufpr.equilibrium.feature_professional.PacientesEnvelope
import com.ufpr.equilibrium.feature_professional.ProfessionalModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

import retrofit2.http.*

interface PessoasAPI {

    // Se o backend busca por CPF, alinhe o nome do query param
    // (no seu código estava "upc", que parece errado).
    @GET("lookup")
    fun getPessoaByCpf(
        @Query("cpf") cpf: String
    ): Call<Usuario>

    // legacy login removed; use data module AuthRepositoryImpl

    // /participant -> envelope com data + meta (como no seu log)
    @GET("participant")
    fun getPacientes(
        // opcional: suporte a paginação do backend
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        // opcional: busca por CPF
        @Query("cpf") cpf: String? = null
    ): Call<PacientesEnvelope>

    // /participant aceita o JSON plano do PacienteModel (sem "user")
    @POST("participant")
    fun postPatient(
        @Body request: PacienteModel
    ): Call<PacienteModel>

    @POST("evaluation")
    fun postTestes(
        @Body request: Teste,
        @Header("Authorization") token: String
    ): Call<Teste>

    // GET evaluations for a participant (returns list of evaluation objects)
    @GET("evaluation")
    fun getEvaluations(
        @Query("participantId") participantId: String
    ): Call<EvaluationsEnvelope>

    // Provável que também exija auth — adicione se o backend requer
    @POST("healthProfessional")
    fun postProfessional(
        @Body request: ProfessionalModel,
    ): Call<ProfessionalModel>

    // /health-unit: confira se retorna lista “plana” mesmo.
    @GET("health-unit")
    fun getHealthUnit(): Call<List<HealthUnit>>
}
