package com.island.iot

import android.content.SharedPreferences

class StateRepository(sp: SharedPreferences) {
    val remoteDataSource = RemoteDataSource("http://192.168.4.1:1881")
    val localDataSource = LocalDataSource(sp)
    suspend fun register(username: String, password: String): String {
        val registerResult = remoteDataSource.register(RegisterRequest(username, password))
        when (registerResult.status) {
            ResponseStatus.OK -> {}
        }
        return login(username, password)
    }

    suspend fun login(username: String, password: String): String {
        val result = remoteDataSource.login(RegisterRequest(username, password))
        when (result.status) {
            ResponseStatus.OK -> {}
        }
        val token = result.token!!
        localDataSource.saveToken(token)
        localDataSource.saveUserId(result.userId!!)
        return token
    }
}