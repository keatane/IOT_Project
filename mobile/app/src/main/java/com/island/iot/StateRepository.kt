package com.island.iot

import android.util.Log
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.net.SocketTimeoutException


class StateRepository(db: AppDatabase) {
    val _remoteDataSource = RemoteDataSource("http://192.168.137.1:1881")
    val _localDataSource = LocalDataSource(db)
    val user = _localDataSource.user.map {
        Log.d(
            "dhjshjdsf",
            "MAPPING DATABASE $it"
        );if (it.isEmpty()) null else it[0]
    }

    suspend fun updateJugs() {
        user.collect {
            if (it != null) {
                try {
                    val jugs = _remoteDataSource.getJugs(GetJugsRequest(it.token)).jugs!!
                    memoryDataSource.jugList.value = jugs
                } catch (e: SocketTimeoutException) {
                    Log.e("ERRORS", "ERROR", e)
                }
            }
        }
    }

    suspend fun deleteJug(index: Int) {
        Log.d("LAST STUCK", "LAST STUCK")
        val jugs = memoryDataSource.jugList.first()
        Log.d("JUGS SIZE", jugs.size.toString())
        Log.d("djksjdf", jugs.toString())
        val jug = jugs[index]
        memoryDataSource.modifyJugList { it.removeAt(index) }
        val user = user.first()
        user!!
        _remoteDataSource.deleteJug(DeleteJugRequest(user.token, jug.id))

    }

    suspend fun renameJug(id: Int, name: String) {
        memoryDataSource.modifySingleJug(id) { it.copy(title = name) }
        user.collectLatest {
            it!!
            _remoteDataSource.renameJug(RenameJugRequest(it.token, name))
        }
    }

    suspend fun changeFilter(id: Int, filter: Int) {
        memoryDataSource.modifySingleJug(id) { it.copy(filter = filter) }


        user.collectLatest { user ->
            user!!
            _filter(
                user.token,
                id,
                filter
            )
        }
    }

    val _arduinoDataSource = ArduinoDataSource()
    val memoryDataSource =
        MemoryDataSource { ssid, pw ->
            user.collect {
                if (it == null) Log.d(
                    "dhjshfjdfd",
                    "USER IS NULLLLLLL"
                ) else _pair(ssid, pw, it.token)
            }
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
}
