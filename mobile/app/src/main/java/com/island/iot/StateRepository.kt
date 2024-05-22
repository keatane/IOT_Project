package com.island.iot

class StateRepository {
    val remoteDataSource = RemoteDataSource("http://127.0.0.1:1880")
    val localDataSource = LocalDataSource()
    suspend fun register(username: String, password: String): String {
        val registerResult = remoteDataSource.register(username, password)
        when (registerResult.status) {
            ResponseStatus.OK -> {}
        }
        return login(username, password)
    }

    suspend fun login(username: String, password: String): String {
        val result = remoteDataSource.login(username, password)
        when (result.status) {
            ResponseStatus.OK -> {}
        }
        val token = result.token!!
        localDataSource.saveToken(token)
        return token
    }
}