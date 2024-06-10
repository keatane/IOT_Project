package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val API_URL = "http://192.168.0.25:1880"

enum class ResponseStatus {
    OK
}

data class Response(val response: ResponseStatus)

data class RegisterRequest(val username: String, val password: String)

data class RegisterResponse(val status: ResponseStatus)

data class LoginResponse(val status: ResponseStatus, val token: String?, val userId: Int?)

data class FilterRequest(val token: String, val jugId: Int, val filter: Int)

data class GetJugsRequest(val token: String)

data class GetJugsResponse(val status: ResponseStatus, val jugs: List<JugElement>?)

data class DeleteJugRequest(val token: String, val id: Int)

data class RenameJugRequest(val token: String, val name: String)

interface RemoteDataSource {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body body: RegisterRequest): LoginResponse

    @POST("delete")
    suspend fun delete(@Body body: RegisterRequest): RegisterResponse

    @POST("filter")
    suspend fun filter(@Body body: FilterRequest): RegisterResponse

    /* To be added for the dashboard */
    @POST("getJugs")
    suspend fun getJugs(@Body body: GetJugsRequest): GetJugsResponse

    @POST("deleteJug")
    suspend fun deleteJug(@Body body: DeleteJugRequest): Response

    @POST("renameJug")
    suspend fun renameJug(@Body body: RenameJugRequest): Response
}

class RemoteDataSourceFake : RemoteDataSource {
    override suspend fun register(body: RegisterRequest): RegisterResponse {
        throw NotImplementedError()
    }

    override suspend fun login(body: RegisterRequest): LoginResponse {
        throw NotImplementedError()
    }

    override suspend fun delete(body: RegisterRequest): RegisterResponse {
        throw NotImplementedError()
    }

    override suspend fun filter(body: FilterRequest): RegisterResponse {
        throw NotImplementedError()
    }

    override suspend fun getJugs(body: GetJugsRequest): GetJugsResponse {
        throw NotImplementedError()
    }

    override suspend fun deleteJug(body: DeleteJugRequest): Response {
        throw NotImplementedError()
    }

    override suspend fun renameJug(body: RenameJugRequest): Response {
        throw NotImplementedError()
    }
}

class RemoteDataSourceImpl :
    RemoteDataSource by (Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(RemoteDataSource::class.java))