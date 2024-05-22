package com.island.iot

import retrofit2.Retrofit
import retrofit2.http.POST

enum class ResponseStatus {
    OK
}

data class RegisterResponse(val status: ResponseStatus)

data class LoginResponse(val status: ResponseStatus, val token: String?)


interface RemoteService {
    @POST("register")
    suspend fun register(username: String, password: String): RegisterResponse

    @POST("login")
    suspend fun login(username: String, password: String): LoginResponse
}

class RemoteDataSource(url: String) :
    RemoteService by (Retrofit.Builder().baseUrl(url).build().create(RemoteService::class.java))