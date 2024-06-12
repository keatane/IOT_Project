package com.island.iot

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import okio.IOException
import kotlin.math.max


class StateRepository(
    private val _launch: (suspend CoroutineScope.() -> Unit) -> Unit,
    private val _remoteDataSource: RemoteDataSource,
    private val _localDataSource: LocalDataSource,
    private val _memoryDataSource: MemoryDataSource,
    private val _arduinoDataSource: ArduinoDataSource
) {
    val jugList = _memoryDataSource.jugList.asStateFlow()
    val pairingState = _memoryDataSource.pairingState.asStateFlow()
    val user = _localDataSource.user
    val selectedJug =
        _memoryDataSource.jugList.combine(_memoryDataSource.selectedJugIndex) { list, index ->
            Log.d("JUG LIST", list.toString())
            Log.d("JUG INDEX", index.toString())
            list.getOrNull(index)
        }
    val lastError = _memoryDataSource.lastError.asStateFlow()

    private suspend fun updateJugs() {
        var first = true
        while (true) {
            val user = _localDataSource.user.first { it != null }
            user!!
            try {
                val jugs = _remoteDataSource.getJugs(GetJugsRequest(user.token)).jugs!!
                _memoryDataSource.jugList.value = jugs
            } catch (e: IOException) {
                // TODO: remove this
                if (first)
                    _memoryDataSource.jugList.value =
                        listOf(
                            JugElement(title = "Kitchen Jug", filter = 150, id = 0),
                            JugElement(title = "Living Room Jug", filter = 200, id = 1)
                        )
            }
            first = false
            delay(1000)
        }
    }

    private suspend fun clearErrors() {
        while (true) {
            _memoryDataSource.lastError.value = null
            delay(1000)
        }
    }

    fun launch(f: suspend CoroutineScope.() -> Unit) {
        _launch {
            try {
                f()
            } catch (e: Throwable) {
                Log.e("ERROR", "ERROR", e)
                _memoryDataSource.lastError.value = e.toString()
            }
        }
    }

    init {
        launch { updateJugs() }
        launch { clearErrors() }
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
        _modifySingleJug(jug, jug.copy(title = name))
        _remoteDataSource.renameJug(
            RenameJugRequest(_localDataSource.user.first()!!.token, name)
        )
    }

    suspend fun changeFilter(jug: JugElement, filter: Int) {
        _modifySingleJug(jug, jug.copy(filter = filter))
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
        val index =
            mutable.indexOf(_memoryDataSource.jugList.value.getOrNull(_memoryDataSource.selectedJugIndex.value))
        _memoryDataSource.selectedJugIndex.value = max(index, 0)
        _memoryDataSource.jugList.value = mutable
    }

    private fun _modifySingleJug(prev: JugElement, new: JugElement) {
        _modifyJugList { it[it.indexOf(prev)] = new }
    }

    private fun _enterConnecting() {
        _memoryDataSource.pairingState.value = PairingState.CONNECTING
    }

    fun enterAskPassword() {
        _memoryDataSource.wifiPassword.value = ""
        _memoryDataSource.pairingState.value = PairingState.ASK_PASSWORD
    }

    suspend fun enterSending(ssid: String, wifiPassword: String) {
        _memoryDataSource.pairingState.value = PairingState.SENDING
        _pair(
            ssid, wifiPassword,
            _localDataSource.user.first()!!.token
        )
        _memoryDataSource.pairingState.value = PairingState.NONE
    }

    fun setWifiPassword(password: String) {
        _memoryDataSource.wifiPassword.value = password
        Log.d("fhdjhdfdf", "Setting wifi password to")
    }

    suspend fun pairJug(pairing: Pairing) {
        val jug = pairing.selectJug()
        _enterConnecting()
        pairing.connectToJug(jug!!)
        val wifi = pairing.selectWifi()
        enterAskPassword()
        val password = _memoryDataSource.wifiPassword.first { it != "" }
        enterSending(wifi!!, password)
        pairing.disconnect()
    }

}
