package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING
}

data class JugElement(val title: String?, val filter: Int, val id: Int)
class MemoryDataSource(val callback: suspend (String, String) -> Unit) {

    val pairingState = MutableStateFlow(PairingState.NONE)

    val ssid = MutableStateFlow("")
    val wifiPassword = MutableStateFlow("")
    val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(title = "Kitchen Jug", filter = 150, id = 0),
            JugElement(title = "Living Room Jug", filter = 200, id = 1)
        )
    )

    fun modifyJugList(callback: (MutableList<JugElement>) -> Unit) {
        jugList.update {
            val mutable = it.toMutableList()
            callback(mutable)
            mutable
        }
    }

    fun modifySingleJug(id: Int, callback: (JugElement) -> JugElement) {
        modifyJugList { it[id] = callback(it[id]) }
    }

    fun enterConnecting() {
        pairingState.value = PairingState.CONNECTING
    }

    fun enterAskPassword(ssid: String) {
        this.ssid.value = ssid
        wifiPassword.value = ""
        pairingState.value = PairingState.ASK_PASSWORD
    }

    suspend fun enterSending() {
        pairingState.value = PairingState.SENDING
        callback(ssid.value, wifiPassword.value)
    }
}