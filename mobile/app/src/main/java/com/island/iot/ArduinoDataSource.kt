package com.island.iot

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val ARDUINO_URL = "http://192.168.4.1:8080"

data class PairRequest(val ssid: String, val pw: String, val token: String)


interface ArduinoDataSource {
    @POST("/")
    suspend fun pair(@Body body: PairRequest)
}

class ArduinoDataSourceFake : ArduinoDataSource {
    override suspend fun pair(body: PairRequest) {
        throw NotImplementedError()
    }
}

class ArduinoDataSourceImpl :
    ArduinoDataSource by (Retrofit.Builder().baseUrl(ARDUINO_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(ArduinoDataSource::class.java))