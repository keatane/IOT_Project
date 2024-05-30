package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING
}

class MemoryDataSource(val callback: suspend (String, String) -> Unit) {

    val pairingState = MutableStateFlow(PairingState.NONE)

    val ssid = MutableStateFlow("")
    val wifiPassword = MutableStateFlow("")

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