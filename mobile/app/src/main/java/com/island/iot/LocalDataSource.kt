package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalDataSource {
    val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    suspend fun saveToken(token: String) {

    }

    suspend fun readToken(): String {
    }
}