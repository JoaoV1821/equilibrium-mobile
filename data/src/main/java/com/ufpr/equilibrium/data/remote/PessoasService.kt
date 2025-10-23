package com.ufpr.equilibrium.data.remote

import com.ufpr.equilibrium.data.remote.dto.LoginRequestDto
import com.ufpr.equilibrium.data.remote.dto.LoginResultDto
import retrofit2.http.Body
import retrofit2.http.POST

interface PessoasService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResultDto
}


