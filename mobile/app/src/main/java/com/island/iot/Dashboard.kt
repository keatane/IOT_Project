package com.island.iot

import androidx.collection.intListOf
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Metric(title: String, value: String, cardColor: Int, isLast: Boolean = false) {
    Card (
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(containerColor = colorResource(id = cardColor)),
        modifier = Modifier
            .width(if(!isLast) 150.dp else 300.dp)
            .height(130.dp)
            .padding(0.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(value, modifier = Modifier
                .padding(8.dp, 8.dp)
                .wrapContentHeight(), textAlign = TextAlign.Center, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(title, modifier = Modifier
                .padding(8.dp, 0.dp)
                .wrapContentHeight(), textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Thin)
        }
    }
}

@Composable
fun Grid(jugsList: List<JugElement>, selectedJug: Int) {
    val uriHandler = LocalUriHandler.current
    val colors = intListOf(R.color.ocean, R.color.sand, R.color.crab, R.color.abyss, R.color.seaside, R.color.red)
    Text(text = jugsList[selectedJug].title.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 0.dp))
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(0.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Metric("Total consumption", "N/A", cardColor = colors[0])
            Metric("Daily consumption", "N/A", cardColor = colors[1])
        }
        Column(
            modifier = Modifier.padding(16.dp, 0.dp)
        ) {
            Metric("Filter capacity", if (jugsList.isNotEmpty()) jugsList[selectedJug].filter.toString() + "L" else "N/A", cardColor = colors[2])
            Metric("Filter life", "N/A", cardColor = colors[3])
        }
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        Metric("Quantity of plastic saved", "N/A", cardColor = colors[4], true) // totalFilter*32/500000
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        Metric("Filter status", "N/A", cardColor = colors[5], true) // totalFilter*32/500000, // totalFilter/filterCapacity*100 (trim 2,3 o 4)
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        ExtendedFloatingActionButton(
            onClick = { uriHandler.openUri("https://www.google.com/search?q=jug+filter") },
            icon = { Icon(Icons.Filled.ShoppingCart, "Buy filter") },
            text = { Text(text = "Buy filter") },
            containerColor = colorResource(id = R.color.water),
        )
    }
}

@Composable
@Preview
fun DashboardPreview() {
    Decorations {
        Dashboard()
    }
}

@Composable
fun Dashboard(
    initDashboard: () -> Unit = {}, jugsList: List<JugElement> = listOf(), selectedJug: Int = 0
) {
    initDashboard()
    ScrollableContent {
        Grid(jugsList, selectedJug)
    }
}
