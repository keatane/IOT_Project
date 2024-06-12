package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING
}

data class JugElement(val title: String?, val filter: Int, val id: Int)

interface MemoryDataSource {
    val pairingState: MutableStateFlow<PairingState>
    val wifiPassword: MutableStateFlow<String>
    val jugList: MutableStateFlow<List<JugElement>>
    val selectedJugIndex: MutableStateFlow<Int>
    val lastError: MutableStateFlow<String?>
}

class MemoryDataSourceImpl : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(listOf())
    override val selectedJugIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
}

class MemoryDataSourceFake : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(title = "Kitchen Jug", filter = 150, id = 0),
            JugElement(title = "Living Room Jug", filter = 200, id = 1)
        )
    )
    override val selectedJugIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
}
