package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.logging.Filter

enum class ResponseStatus {
    OK
}

data class RegisterRequest(val username: String, val password: String)

data class RegisterResponse(val status: ResponseStatus)

data class LoginResponse(val status: ResponseStatus, val token: String?, val userId: Int?)

data class FilterRequest(val username: String, val jugId: Int, val filter: Int)

interface RemoteService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body body: RegisterRequest): LoginResponse

    @POST("delete")
    suspend fun delete(@Body body: RegisterRequest): RegisterResponse

    @POST("filter")
    suspend fun filter(@Body body: FilterRequest): RegisterResponse

    /* To be added for the dashboard */
    @POST("totalLitres")
    suspend fun totalLitres(@Body body: FilterRequest): RegisterResponse
    @POST("dailyConsumption")
    suspend fun dailyConsumption(@Body body: FilterRequest): RegisterResponse
}

class RemoteDataSource(url: String) :
    RemoteService by (Retrofit.Builder().baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(RemoteService::class.java))