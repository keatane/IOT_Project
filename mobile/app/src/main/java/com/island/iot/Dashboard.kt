package com.island.iot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Metric(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, modifier = Modifier.padding(8.dp, 16.dp))
        Text(value, modifier = Modifier.padding(8.dp, 16.dp))
    }
}

@Composable
fun Grid(jugsList: List<JugElement>, selectedJug: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp, 0.dp)
        ) {
            Metric("Total consumption", "LLL")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.3f))
            Metric("Daily consumption", "LLL")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp, 0.dp)
        ) {
            Metric("Filter capacity", jugsList[selectedJug].filter.toString() + "L")
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.6f))
            Metric("Filter life", "LLL")
        }
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp)
    ) {
        Column {
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth(.5f))
            Metric("Quantity of plastic save", "LLL") // totalFilter*32/500000
        }
    }
    Row() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Filter status", modifier = Modifier.padding(4.dp))
            Text("%%%") // totalFilter/filterCapacity*100 (trim 2,3 o 4)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ExtendedFloatingActionButton(
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Filled.ShoppingCart, "Buy filter") },
            text = { Text(text = "Buy filter") },
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
