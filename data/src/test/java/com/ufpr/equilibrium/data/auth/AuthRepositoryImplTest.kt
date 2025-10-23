package com.ufpr.equilibrium.data.auth

import com.google.gson.GsonBuilder
import com.ufpr.equilibrium.data.remote.PessoasService
import com.ufpr.equilibrium.data.remote.dto.LoginRequestDto
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepositoryImplTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun login_success_parsesToken() = runTest {
        val responseJson = """{"access_token":"header.payload.sig"}"""
        // payload base64Url of {"role":"PATIENT"}
        val payload = "eyJyb2xlIjoiUEFUSUVOVCJ9"
        val token = "aaa.$payload.bbb"
        server.enqueue(
            MockResponse()
                .setBody("""{"access_token":"$token"}""")
                .setResponseCode(200)
        )

        val service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(PessoasService::class.java)

        val repo = AuthRepositoryImpl(service)
        val result = repo.login("user", "pass")
        assertTrue(result is com.ufpr.equilibrium.core.common.Result.Success)
    }
}


