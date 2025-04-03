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

    @GET("/pessoas")
    fun getPessoas(): Call<List<Usuario>>

    @POST("/autenticacao/login")
    fun authenticate(@Body request: Login): Call<LoginResult>

    @POST("/pessoas")
    fun postPessoas(@Body request: Usuario): Call<Usuario>

    @DELETE("/pessoas")
    fun deletePessoas(@Query("upc") cpf: String): Call<Usuario>

    @PUT("/pessoas")
    fun updatePessoas(@Query("upc") request: Usuario): Call<Usuario>


    @GET("/paciente")
    fun getPacientes(): Call<List<Paciente>>
}