package com.ufpr.equilibrium

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface PessoasAPI {
    @GET("lookup")
    fun getPessoaByid(@Query("upc") cpf: String ): Call<Usuario>

    @POST("/auth/login")
    fun authenticate(@Body request: Login): Call<LoginResult>

    @GET("/patient")
    fun getPacientes(): Call<List<Paciente>>

    @POST("/evaluation")
    fun postTestes(@Body request: Teste): Call<Teste>

    @GET("/healthUnit")
    fun getHealthUnit(): Call<List<HealthUnit>>
}