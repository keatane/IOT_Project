package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class PairRequest(val ssid: String, val pw: String, val token: String)

data class PairResponse(val status: ResponseStatus)

interface ArduinoService {
    @POST("/")
    suspend fun pair(@Body body: PairRequest): PairResponse
}

class ArduinoDataSource :
    ArduinoService by (Retrofit.Builder().baseUrl("http://192.168.4.1:8080")
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(ArduinoService::class.java))