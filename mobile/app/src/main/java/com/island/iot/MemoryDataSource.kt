package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING
}

data class JugElement(val title: String?, val filter: Int, val id: Int)

interface MemoryDataSource {
    val pairingState: MutableStateFlow<PairingState>
    val ssid: MutableStateFlow<String>
    val wifiPassword: MutableStateFlow<String>
    val jugList: MutableStateFlow<List<JugElement>>
    val selectedJugIndex: MutableStateFlow<Int?>
    val currentRoute:MutableStateFlow<Route?>
}

class MemoryDataSourceImpl : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val ssid = MutableStateFlow("")
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(title = "Kitchen Jug", filter = 150, id = 0),
            JugElement(title = "Living Room Jug", filter = 200, id = 1)
        )
    )
    override val selectedJugIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    override val currentRoute: MutableStateFlow<Route?> = MutableStateFlow(null)
}

class MemoryDataSourceFake : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val ssid = MutableStateFlow("")
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(title = "Kitchen Jug", filter = 150, id = 0),
            JugElement(title = "Living Room Jug", filter = 200, id = 1)
        )
    )
    override val selectedJugIndex: MutableStateFlow<Int?> = MutableStateFlow(0)
    override val currentRoute: MutableStateFlow<Route?> = MutableStateFlow(null)
}
