package com.island.iot

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.IOException
import retrofit2.HttpException
import kotlin.math.max


class StateRepository(
    private val _launch: (suspend CoroutineScope.() -> Unit) -> Unit,
    private val _remoteDataSource: RemoteDataSource,
    private val _localDataSource: LocalDataSource,
    private val _memoryDataSource: MemoryDataSource,
    private val _arduinoDataSource: ArduinoDataSource,
    private val _newsDataSource: NewsDataSource,
) {
    val jugList = _memoryDataSource.jugList.map { jugs -> jugs.sortedBy { it.id } }
    val pairingState = _memoryDataSource.pairingState.asStateFlow()
    val user = _localDataSource.user
    private val selectedJugIndex = _localDataSource.user.map { it?.selectedJugIndex ?: 0 }
    val selectedJug =
        _memoryDataSource.jugList.combine(selectedJugIndex) { list, index ->
            Log.d("JUG LIST", list.toString())
            Log.d("JUG INDEX", index.toString())
            list.getOrNull(index)
        }
    val lastError = _memoryDataSource.lastError.asStateFlow()
    val totalLitres =
        _memoryDataSource.totalLitres.map { if (selectedJug.first() == null) null else it }
    val totalLitresFilter =
        _memoryDataSource.totalLitresFilter.map { if (selectedJug.first() == null) null else it }
    val dailyLitres =
        _memoryDataSource.dailyLitres.map { if (selectedJug.first() == null) null else it }
    val hourLitres =
        _memoryDataSource.hourLitres.map { if (selectedJug.first() == null) null else it }
    val weekLitres =
        _memoryDataSource.weekLitres.map { if (selectedJug.first() == null) null else it }
    val news = _memoryDataSource.news.asStateFlow()

    private suspend fun updateJugs() {
        var first = true
        while (true) {
            val user = _localDataSource.user.filterNotNull().first()
            try {
                val jugs = _remoteDataSource.getJugs(GetJugsRequest(user.token)).jugs
                _memoryDataSource.jugList.value = jugs
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    logout()
                }
            } catch (e: IOException) {
                Log.e("UPDATE JUG", "ERROR", e)
                // TODO: remove this
                if (first)
                    _memoryDataSource.jugList.value =
                        listOf(
                            JugElement(name = "Kitchen Jug", filtercapacity = 150, id = 0),
                            JugElement(name = "Living Room Jug", filtercapacity = 200, id = 1)
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

    private suspend fun clearJugData() {
        selectedJug.collect {
            _memoryDataSource.totalLitres.value = null
            _memoryDataSource.totalLitresFilter.value = null
            _memoryDataSource.dailyLitres.value = null
            _memoryDataSource.hourLitres.value = null
            _memoryDataSource.weekLitres.value = null
        }
    }

    private suspend fun updateJugData() {
        while (true) {
            val user = user.filterNotNull().first()
            val jug = selectedJug.filterNotNull().first()
            try {
                _memoryDataSource.totalLitres.value =
                    _remoteDataSource.totalLitres(JugDataRequest(token = user.token, id = jug.id))
            } catch (e: Exception) {
                Log.e("TOTAL LITRES", "ERROR", e)
            }
            try {
                _memoryDataSource.totalLitresFilter.value = _remoteDataSource.totalLitresFilter(
                    JugDataRequest(
                        token = user.token,
                        id = jug.id
                    )
                )
            } catch (e: Exception) {
                Log.e("TOTAL LITRES FILTER", "ERROR", e)
            }
            try {
                _memoryDataSource.dailyLitres.value =
                    _remoteDataSource.dailyLitres(JugDataRequest(token = user.token, id = jug.id))
            } catch (e: Exception) {
                Log.e("DAILY USAGE", "ERROR", e)
            }
            try {
                _memoryDataSource.hourLitres.value =
                    _remoteDataSource.hourLitres(JugDataRequest(token = user.token, id = jug.id))
            } catch (e: Exception) {
                Log.e("HOUR USAGE", "ERROR", e)
            }
            try {
                _memoryDataSource.weekLitres.value =
                    _remoteDataSource.weekLitres(JugDataRequest(token = user.token, id = jug.id))
            } catch (e: Exception) {
                Log.e("WEEK USAGE", "ERROR", e)
            }
            delay(3000)
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

    private suspend fun updateNews() {
        try {
            _memoryDataSource.news.value = _newsDataSource.getNews()
        } catch (e: Exception) {
            Log.e("UPDATE NEWS", "ERROR", e)
        }
    }

    init {
        launch { updateJugs() }
        launch { clearErrors() }
        launch { clearJugData() }
        launch { updateJugData() }
        launch { updateNews() }
    }

    suspend fun _setSelectedJugIndex(index: Int) {
        _localDataSource.setUser(user.filterNotNull().first().copy(selectedJugIndex = index))
    }

    suspend fun setSelectedJug(jug: JugElement) {
        _setSelectedJugIndex(_memoryDataSource.jugList.first().indexOf(jug))
    }

    suspend fun deleteJug(jug: JugElement) {
        Log.d("LAST STUCK", "LAST STUCK")
        val jugs = _memoryDataSource.jugList.first()
        Log.d("JUGS SIZE", jugs.size.toString())
        Log.d("djksjdf", jugs.toString())
        _modifyJugList { it.remove(jug) }
        val user = _localDataSource.user.filterNotNull().first()
        _remoteDataSource.deleteJug(DeleteJugRequest(user.token, jug.id))
    }

    suspend fun renameJug(jug: JugElement, name: String) {
        _remoteDataSource.renameJug(
            RenameJugRequest(_localDataSource.user.first()!!.token, jug.id, name)
        )
        _modifySingleJug(jug, jug.copy(name = name))
    }

    suspend fun changeFilter(jug: JugElement, filter: Int) {
        _remoteDataSource.filter(
            FilterRequest(
                _localDataSource.user.filterNotNull().first().token,
                jug.id,
                filter
            )
        )
        _modifySingleJug(jug, jug.copy(filtercapacity = filter))
    }


    suspend fun register(username: String, password: String): User {
        _remoteDataSource.register(RegisterRequest(username, password))
        return login(username, password)
    }

    suspend fun login(username: String, password: String): User {
        val result = _remoteDataSource.login(RegisterRequest(username, password))
        val user = User(result.userId, result.token)
        _localDataSource.setUser(user)
        return user
    }

    suspend fun deleteAccount() {
        _remoteDataSource.deleteAccount(DeleteAccountRequest(user.filterNotNull().first().token))
        logout()
    }

    suspend fun changeEmail(email: String) {
        _remoteDataSource.changeEmail(ChangeEmailRequest(user.filterNotNull().first().token, email))
    }

    suspend fun changePassword(oldPassword: String, newPassword: String) {
        _remoteDataSource.changePassword(
            ChangePasswordRequest(
                user.filterNotNull().first().token,
                oldPassword,
                newPassword
            )
        )
    }

    suspend fun _pair(ssid: String, password: String, token: String) {
        Log.d("fhdjhfdjfhjdhfj", "START PAIRING")
        return _arduinoDataSource.pair(PairRequest(ssid, password, token))
    }

    private suspend fun _modifyJugList(callback: (MutableList<JugElement>) -> Unit) {
        val mutable = _memoryDataSource.jugList.value.toMutableList()
        callback(mutable)
        val index =
            mutable.indexOf(_memoryDataSource.jugList.value.getOrNull(selectedJugIndex.first()))
        _setSelectedJugIndex(max(index, 0))
        _memoryDataSource.jugList.value = mutable
    }

    private suspend fun _modifySingleJug(prev: JugElement, new: JugElement) {
        _modifyJugList { it[it.indexOf(prev)] = new }
    }

    private fun _enterConnecting() {
        _memoryDataSource.pairingState.value = PairingState.CONNECTING
    }

    fun enterAskPassword() {
        _memoryDataSource.wifiPassword.value = null
        _memoryDataSource.pairingState.value = PairingState.ASK_PASSWORD
    }

    suspend fun enterSending(ssid: String, wifiPassword: String) {
        _memoryDataSource.pairingState.value = PairingState.SENDING
        _pair(
            ssid, wifiPassword,
            _localDataSource.user.filterNotNull().first().token
        )
    }

    fun resetPairingState() {
        _memoryDataSource.pairingState.value = PairingState.NONE
    }

    suspend fun logout() {
        _localDataSource.deleteUser(_localDataSource.user.filterNotNull().first())
    }

    fun setWifiPassword(password: String) {
        _memoryDataSource.wifiPassword.value = password
        Log.d("fhdjhdfdf", "Setting wifi password to")
    }

    suspend fun pairJug(pairing: Pairing) {
        val jug = pairing.selectJug() ?: return resetPairingState()
        val jugId = jug.split("_").last().toIntOrNull() ?: return resetPairingState()
        val jugElem = _memoryDataSource.jugList.first().find { it.id == jugId }
        if (jugElem != null)
            deleteJug(jugElem)
        _enterConnecting()
        try {
            val pairingResult = pairing.connectToJug(jug)
            if (!pairingResult) return resetPairingState()
            val wifi = pairing.selectWifi() ?: return resetPairingState()
            enterAskPassword()
            val password = _memoryDataSource.wifiPassword.filterNotNull().first()
            if (password.isEmpty()) return resetPairingState()
            enterSending(wifi, password)
        } finally {
            pairing.disconnect()
        }
        _memoryDataSource.jugList.map { jugList -> jugList.find { it.id == jugId } }.filterNotNull()
            .first()
        _memoryDataSource.pairingState.value = PairingState.DONE
    }
}
