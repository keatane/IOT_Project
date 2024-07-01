package com.island.iot

import kotlinx.coroutines.flow.MutableStateFlow


enum class PairingState {
    NONE, CONNECTING, ASK_PASSWORD, SENDING, DONE, ERROR
}

data class JugElement(val name: String, val filtercapacity: Int, val id: Int)

interface MemoryDataSource {
    val pairingState: MutableStateFlow<PairingState>
    val wifiPassword: MutableStateFlow<String?>
    val jugList: MutableStateFlow<List<JugElement>>
    val lastError: MutableStateFlow<String?>
    val totalLitres: MutableStateFlow<Double?>
    val litresPerSecond: MutableStateFlow<Double?>
    val totalLitresFilter: MutableStateFlow<Double?>
    val dailyLitres: MutableStateFlow<Double?>
    val hourLitres: MutableStateFlow<List<Double>?>
    val weekLitres: MutableStateFlow<List<Double>?>
    val news: MutableStateFlow<List<NewsArticle>?>
}

class MemoryDataSourceImpl : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword: MutableStateFlow<String?> = MutableStateFlow(null)
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(listOf())
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
    override val totalLitres: MutableStateFlow<Double?> = MutableStateFlow(null)
    override val litresPerSecond: MutableStateFlow<Double?> = MutableStateFlow(null)
    override val totalLitresFilter: MutableStateFlow<Double?> = MutableStateFlow(null)
    override val dailyLitres: MutableStateFlow<Double?> = MutableStateFlow(null)
    override val hourLitres: MutableStateFlow<List<Double>?> = MutableStateFlow(null)
    override val weekLitres: MutableStateFlow<List<Double>?> = MutableStateFlow(null)
    override val news: MutableStateFlow<List<NewsArticle>?> = MutableStateFlow(null)
}

class MemoryDataSourceFake : MemoryDataSource {
    override val pairingState = MutableStateFlow(PairingState.NONE)
    override val wifiPassword: MutableStateFlow<String?> = MutableStateFlow(null)
    override val jugList: MutableStateFlow<List<JugElement>> = MutableStateFlow(
        listOf(
            JugElement(name = "Kitchen Jug", filtercapacity = 150, id = 0),
            JugElement(name = "Living Room Jug", filtercapacity = 200, id = 1)
        )
    )
    override val lastError: MutableStateFlow<String?> = MutableStateFlow(null)
    override val totalLitres: MutableStateFlow<Double?> = MutableStateFlow(500.0)
    override val litresPerSecond: MutableStateFlow<Double?> = MutableStateFlow(1.0)
    override val totalLitresFilter: MutableStateFlow<Double?> = MutableStateFlow(50.0)
    override val dailyLitres: MutableStateFlow<Double?> = MutableStateFlow(30.0)
    override val hourLitres: MutableStateFlow<List<Double>?> =
        MutableStateFlow(generateSequence(0.0) { it + 1 }.take(60).toList())
    override val weekLitres: MutableStateFlow<List<Double>?> =
        MutableStateFlow(generateSequence(0.0) { it + 1 }.take(7).toList())
    override val news: MutableStateFlow<List<NewsArticle>?> = MutableStateFlow(
        listOf(
            NewsArticle(
                title = "Tech Giant Announces New Product",
                content = "A well-known tech company unveiled its latest innovation today. Read more to find out what it is!",
                imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png",
                url = "http://www.google.com"
            ),
            NewsArticle(
                title = "Sports: Local Team Makes Big Win",
                content = "Our favorite sports team triumphed over their rivals in a nail-biting game! Get the details here.",
                imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png",
                url = "http://www.google.com"
            ),
            NewsArticle(
                title = "Travel: Explore Breathtaking Destinations",
                content = "Looking for your next adventure? Discover some of the most stunning places to visit around the world.",
                imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png",
                url = "http://www.google.com"
            )
        )
    )
}
