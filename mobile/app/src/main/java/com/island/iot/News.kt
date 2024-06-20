package com.island.iot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

@Composable
fun NewsCard(article: NewsArticle) {
    val uriHandler=LocalUriHandler.current
    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier.padding(24.dp, 12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.abyss)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = "News article image",
            )
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = colorResource(id = R.color.cream)
            )
            Text(text = article.content, maxLines = 2, color = colorResource(id = R.color.cream))
            Button(onClick={uriHandler.openUri(article.url)}){
                Text("Open")
            }
        }
    }
}

@Composable
fun NewsFeed(navController: NavController, stateRepository: StateRepository) {
    val newsArticles =
        stateRepository.news.collectAsState().value // Replace with fetchNewsArticles() for real data
    Column {
        Text(
            text = "Latest news",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .align(Alignment.CenterHorizontally)
        )
        if (newsArticles != null)
            for (article in newsArticles) {
                NewsCard(article = article)
                Spacer(modifier = Modifier.height(8.dp))
            }
        else Text("Loading")
    }
}

@Preview(showBackground = true)
@Composable
fun NewsPreview() {
    val controller = rememberNavController()
    Decorations(controller, Route.NEWS) {
        News(controller, FAKE_REPOSITORY)
    }
}

@Composable
fun News(
    navController: NavController, stateRepository: StateRepository
) {
    ScrollableContent {
        NewsFeed(navController, stateRepository)
    }
}
