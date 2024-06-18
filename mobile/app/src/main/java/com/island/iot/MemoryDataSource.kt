package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING, DONE
}

data class JugElement(val name: String, val filtercapacity: Int, val id: Int)

interface MemoryDataSource {
    val pairingState: MutableStateFlow<PairingState>
    val wifiPassword: MutableStateFlow<String>
    val jugList: MutableStateFlow<List<JugElement>>
    val lastError: MutableStateFlow<String?>
    val totalLitres: MutableStateFlow<Int?>
}

class MemoryDataSourceImpl : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(listOf())
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
    override val totalLitres: MutableStateFlow<Int?> = MutableStateFlow(null)
}

class MemoryDataSourceFake : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword = MutableStateFlow("")
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(name = "Kitchen Jug", filtercapacity = 150, id = 0),
            JugElement(name = "Living Room Jug", filtercapacity = 200, id = 1)
        )
    )
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
    override val totalLitres: MutableStateFlow<Int?> = MutableStateFlow(null)
}
