package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

enum class ResponseStatus {
    OK
}

data class RegisterRequest(val username: String, val password: String)

data class RegisterResponse(val status: ResponseStatus)

data class LoginResponse(val status: ResponseStatus, val token: String?, val userId: Int?)


interface RemoteService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body body: RegisterRequest): LoginResponse
}

class RemoteDataSource(url: String) :
    RemoteService by (Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(RemoteService::class.java))