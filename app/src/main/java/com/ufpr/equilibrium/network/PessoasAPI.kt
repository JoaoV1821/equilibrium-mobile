package com.ufpr.equilibrium.network

import com.ufpr.equilibrium.feature_healthUnit.HealthUnit
import com.ufpr.equilibrium.feature_login.Login
import com.ufpr.equilibrium.feature_login.LoginResult
import com.ufpr.equilibrium.feature_paciente.CadastroPacienteModel
import com.ufpr.equilibrium.feature_professional.Paciente
import com.ufpr.equilibrium.feature_professional.PacienteModel
import com.ufpr.equilibrium.feature_professional.ProfessionalModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PessoasAPI {
    @GET("lookup")
    fun getPessoaByid(@Query("upc") cpf: String ): Call<Usuario>

    @POST("auth/login")
    fun authenticate(@Body request: Login): Call<LoginResult>

    @GET("patient")
    fun getPacientes(@Header("Authorization") token: String): Call<List<Paciente>>

    @POST("patient")
    fun postPatient(@Body request: PacienteModel, @Header("Authorization") token: String): Call<PacienteModel>

    @POST("evaluation")
    fun postTestes(@Body request: Teste, @Header("Authorization") token: String): Call<Teste>

    @POST("healthProfessional")
    fun postProfessional(@Body request: ProfessionalModel): Call<ProfessionalModel>

    @GET("health-unit")
    fun getHealthUnit(@Header("Authorization") token: String): Call<List<HealthUnit>>
}





