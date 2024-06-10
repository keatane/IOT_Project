package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun ChartsPreview() {
    val controller = rememberNavController()
    Decorations(
        controller, FAKE_REPOSITORY,Route.CHARTS
    ) {
        Chart(controller, FAKE_REPOSITORY)
    }
}

@Composable
fun ChartChart(title: String) {
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, modifier = Modifier.padding(64.dp))
    }
}


@Composable
fun Chart(
    controller: NavController, repository: StateRepository
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ScrollableContent {
            ChartChart("Litres consumed in the last hour")
            HorizontalDivider(
                thickness = 2.dp, modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(36.dp)
            )
            ChartChart("Litres consumed in the last days")
        }
    }
}
