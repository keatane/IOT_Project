package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import coil.compose.AsyncImage
data class NewsArticle(val title: String, val content: String, val imageUrl: String)

//@Composable
//fun fetchNewsArticles(): List<NewsArticle> {
//    val newsArticles by remember { mutableStateOf(emptyList()) }
//
//    // Implement your network call logic here (e.g., using Retrofit)
//    // ...
//
//    return newsArticles
//}

// Modified NewsCard to handle online images
@Composable
fun NewsCard(article: NewsArticle) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier.padding(24.dp, 12.dp)
    ) {
        Column (
            modifier = Modifier.padding(12.dp)
        ){
            AsyncImage(
                model = article.imageUrl,
                contentDescription = "News article image",
            )
            Text(text = article.title, style = MaterialTheme.typography.titleMedium)
            Text(text = article.content, maxLines = 2)
        }
    }
}

// Sample news articles for demo
val sampleNewsArticles = listOf(
    NewsArticle(
        title = "Tech Giant Announces New Product",
        content = "A well-known tech company unveiled its latest innovation today. Read more to find out what it is!",
        imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png"
        ),
    NewsArticle(
        title = "Sports: Local Team Makes Big Win",
        content = "Our favorite sports team triumphed over their rivals in a nail-biting game! Get the details here.",
        imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png"
    ),
    NewsArticle(
        title = "Travel: Explore Breathtaking Destinations",
        content = "Looking for your next adventure? Discover some of the most stunning places to visit around the world.",
        imageUrl = "https://claynewsnetwork.com/wp-content/uploads/03-10-17BAnner.png"
    )
)

// Composable function to display the news feed
@Composable
fun NewsFeed() {
    val newsArticles = sampleNewsArticles // Replace with fetchNewsArticles() for real data
    Column {
        for (article in newsArticles) {
            NewsCard(article = article)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsPreview() {
    Decorations() {
        News()
    }
}

@Composable
fun News(
    initNews : () -> Unit = {}
) {
    initNews()
    ScrollableContent {
        NewsFeed()
    }
}
