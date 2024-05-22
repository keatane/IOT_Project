package com.island.iot

class StateRepository(db: AppDatabase) {
    val _remoteDataSource = RemoteDataSource("http://api.smartjug.com:1881")
    val _localDataSource = LocalDataSource(db)
    suspend fun register(username: String, password: String): User {
        val registerResult = _remoteDataSource.register(RegisterRequest(username, password))
        when (registerResult.status) {
            ResponseStatus.OK -> {}
        }
        return login(username, password)
    }

    suspend fun login(username: String, password: String): User {
        val result = _remoteDataSource.login(RegisterRequest(username, password))
        when (result.status) {
            ResponseStatus.OK -> {}
        }
        val user=User(result.userId!!,result.token!!)
        _localDataSource.setUser(user)
        return user
    }
}