package com.island.iot

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip
import java.net.SocketTimeoutException


class StateRepository(
    val launch: (suspend CoroutineScope.() -> Unit) -> Unit,
    private val _remoteDataSource: RemoteDataSource,
    private val _localDataSource: LocalDataSource,
    private val _memoryDataSource: MemoryDataSource,
    private val _arduinoDataSource: ArduinoDataSource
) {
    val jugList = _memoryDataSource.jugList.asStateFlow()
    val pairingState = _memoryDataSource.pairingState.asStateFlow()
    val user = _localDataSource.user
    val currentRoute = _memoryDataSource.currentRoute
    val selectedJug =
        _memoryDataSource.jugList.zip(_memoryDataSource.selectedJugIndex) { list, index ->
            list.getOrNull(index ?: return@zip null)
        }

    suspend fun _updateJugs() {
        val user = _localDataSource.user.first()
        if (user != null) {
            try {
                val jugs = _remoteDataSource.getJugs(GetJugsRequest(user.token)).jugs!!
                _memoryDataSource.jugList.value = jugs
            } catch (e: SocketTimeoutException) {
                Log.e("ERRORS", "ERROR", e)
            }
        }
    }

    init {
        launch { _updateJugs() }
    }

    fun setSelectedJug(jug: JugElement) {
        _memoryDataSource.selectedJugIndex.value = jugList.value.indexOf(jug)
    }

    suspend fun deleteJug(jug: JugElement) {
        Log.d("LAST STUCK", "LAST STUCK")
        val jugs = _memoryDataSource.jugList.first()
        Log.d("JUGS SIZE", jugs.size.toString())
        Log.d("djksjdf", jugs.toString())
        _modifyJugList { it.remove(jug) }
        val user = _localDataSource.user.first()
        user!!
        _remoteDataSource.deleteJug(DeleteJugRequest(user.token, jug.id))
    }

    suspend fun renameJug(jug: JugElement, name: String) {
        _modifySingleJug(jug.id) { it.copy(title = name) }
        _remoteDataSource.renameJug(
            RenameJugRequest(_localDataSource.user.first()!!.token, name)
        )
    }

    suspend fun changeFilter(jug: JugElement, filter: Int) {
        _modifySingleJug(jug.id) { it.copy(filter = filter) }
        _filter(
            _localDataSource.user.first()!!.token,
            jug.id,
            filter
        )
    }


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
        val user = User(result.userId!!, result.token!!)
        _localDataSource.setUser(user)
        return user
    }

    suspend fun delete(username: String, password: String) {
        val result = _remoteDataSource.delete(RegisterRequest(username, password))
        when (result.status) {
            ResponseStatus.OK -> {}
        }
    }

    suspend fun _filter(username: String, jugId: Int, filter: Int) {
        val result = _remoteDataSource.filter(FilterRequest(username, jugId, filter))
        when (result.status) {
            ResponseStatus.OK -> {}
        }
    }

    suspend fun _pair(ssid: String, password: String, token: String) {
        Log.d("fhdjhfdjfhjdhfj", "START PAIRING")
        val result = _arduinoDataSource.pair(PairRequest(ssid, password, token))
        when (result.status) {
            ResponseStatus.OK -> {}
        }
    }

    private fun _modifyJugList(callback: (MutableList<JugElement>) -> Unit) {
        val mutable = _memoryDataSource.jugList.value.toMutableList()
        callback(mutable)
        _memoryDataSource.jugList.value = mutable
    }

    private fun _modifySingleJug(id: Int, callback: (JugElement) -> JugElement) {
        _modifyJugList { it[id] = callback(it[id]) }
    }

    fun enterConnecting() {
        _memoryDataSource.pairingState.value = PairingState.CONNECTING
    }

    fun enterAskPassword(ssid: String) {
        _memoryDataSource.ssid.value = ssid
        _memoryDataSource.wifiPassword.value = ""
        _memoryDataSource.pairingState.value = PairingState.ASK_PASSWORD
    }

    suspend fun enterSending(wifiPassword: String) {
        _memoryDataSource.wifiPassword.value = wifiPassword
        _memoryDataSource.pairingState.value = PairingState.SENDING
        _pair(
            _memoryDataSource.ssid.value,
            _memoryDataSource.wifiPassword.value,
            _localDataSource.user.first()!!.token
        )
    }
}
