package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val API_URL = "http://192.168.4.1:1881"


data class RegisterRequest(val username: String, val password: String)

data class LoginRequest(val username: String, val password: String, val firebaseToken: String?)

data class LoginResponse(val token: String, val userId: Int)

data class FilterRequest(val token: String, val jugId: Int, val filter: Int)

data class GetJugsRequest(val token: String)

data class GetJugsResponse(val jugs: List<JugElement>)

data class DeleteJugRequest(val token: String, val id: Int)

data class RenameJugRequest(val token: String, val id: Int, val name: String)

data class DeleteAccountRequest(val token: String)

data class ChangeEmailRequest(val token: String, val newEmail: String)

data class ChangePasswordRequest(val token: String, val oldPw: String, val newPw: String)

data class JugDataRequest(val token: String, val id: Int)

data class SetLocationRequest(val token: String, val id: Int, val lat: Double, val lon: Double)


interface RemoteDataSource {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest)

    @POST("login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("filter")
    suspend fun filter(@Body body: FilterRequest)

    /* To be added for the dashboard */
    @POST("getJugs")
    suspend fun getJugs(@Body body: GetJugsRequest): GetJugsResponse

    @POST("deleteJug")
    suspend fun deleteJug(@Body body: DeleteJugRequest)

    @POST("renameJug")
    suspend fun renameJug(@Body body: RenameJugRequest)

    @POST("deleteAccount")
    suspend fun deleteAccount(@Body body: DeleteAccountRequest)

    @POST("email")
    suspend fun changeEmail(@Body body: ChangeEmailRequest)

    @POST("pw")
    suspend fun changePassword(@Body body: ChangePasswordRequest)

    @POST("getTotalLitres")
    suspend fun totalLitres(@Body body: JugDataRequest): Double

    @POST("getLitresPerSecond")
    suspend fun litresPerSecond(@Body body: JugDataRequest): Double

    @POST("getTotalLitresFilter")
    suspend fun totalLitresFilter(@Body body: JugDataRequest): Double

    @POST("getDailyLitres")
    suspend fun dailyLitres(@Body body: JugDataRequest): Double

    @POST("getHourLitres")
    suspend fun hourLitres(@Body body: JugDataRequest): List<Double>

    @POST("getWeekLitres")
    suspend fun weekLitres(@Body body: JugDataRequest): List<Double>

    @POST("setLocation")
    suspend fun setLocation(@Body body: SetLocationRequest)
}

class RemoteDataSourceFake : RemoteDataSource {
    override suspend fun register(body: RegisterRequest) {
        throw NotImplementedError()
    }

    override suspend fun login(body: LoginRequest): LoginResponse {
        throw NotImplementedError()
    }

    override suspend fun filter(body: FilterRequest) {
        throw NotImplementedError()
    }

    override suspend fun getJugs(body: GetJugsRequest): GetJugsResponse {
        throw NotImplementedError()
    }

    override suspend fun deleteJug(body: DeleteJugRequest) {
        throw NotImplementedError()
    }

    override suspend fun renameJug(body: RenameJugRequest) {
        throw NotImplementedError()
    }

    override suspend fun deleteAccount(body: DeleteAccountRequest) {
        throw NotImplementedError()
    }

    override suspend fun changeEmail(body: ChangeEmailRequest) {
        throw NotImplementedError()
    }

    override suspend fun changePassword(body: ChangePasswordRequest) {
        throw NotImplementedError()
    }

    override suspend fun totalLitres(body: JugDataRequest): Double {
        throw NotImplementedError()
    }

    override suspend fun litresPerSecond(body: JugDataRequest): Double {
        throw NotImplementedError()
    }

    override suspend fun totalLitresFilter(body: JugDataRequest): Double {
        throw NotImplementedError()
    }

    override suspend fun dailyLitres(body: JugDataRequest): Double {
        throw NotImplementedError()
    }

    override suspend fun hourLitres(body: JugDataRequest): List<Double> {
        throw NotImplementedError()
    }

    override suspend fun weekLitres(body: JugDataRequest): List<Double> {
        throw NotImplementedError()
    }

    override suspend fun setLocation(body: SetLocationRequest) {
        throw NotImplementedError()
    }
}

class RemoteDataSourceImpl :
    RemoteDataSource by (Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(RemoteDataSource::class.java))