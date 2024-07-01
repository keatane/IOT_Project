@file:Suppress("UNUSED_PARAMETER")

package com.island.iot

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow

fun calculateEstimatedFilterLifeHours(
    dailyConsumption: Double?,
    totalLitresFilter: Double?,
    filter: Int?
): Double? {
    dailyConsumption ?: return null
    totalLitresFilter ?: return null
    filter ?: return null
    if (dailyConsumption == 0.0) return null
    val remainingFilter = filter - totalLitresFilter
    return max(remainingFilter / dailyConsumption, 0.0)
}

fun plasticSaved(total: Double?): Double? {
    total ?: return null
    return total * 32.0 / 500000
}

fun filterStatus(totalLitresFilter: Double?, filter: Int?): Double? {
    totalLitresFilter ?: return null
    filter ?: return null
    return totalLitresFilter * 100 / filter
}

@Composable
fun Metric(
    title: String,
    value: String,
    cardColor: Int,
    isLast: Boolean = false,
    red: Boolean = false
) {
    Card(
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(containerColor = if (red) Color.Red else colorResource(id = cardColor)),
        modifier = Modifier
            .width(if (!isLast) 150.dp else 300.dp)
            .height(130.dp)
            .padding(0.dp, 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                modifier = Modifier
                    .padding(8.dp, 8.dp)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.cream)
            )
            Text(
                title,
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Thin,
                color = colorResource(id = R.color.cream)
            )
        }
    }
}

fun nullRound(x: Double?, digits: Int = 1): String? {
    x ?: return null
    if (digits == 0) return ceil(x).toInt().toString()
    val rounded = Math.round(x * 10.0.pow(digits)) / 10.0.pow(digits)
    var result = rounded.toString()
    if (result.contains("E-")) result = "0"
    val parts = result.split(".").toMutableList()
    if (parts.size == 1) parts.add("")
    parts[1] = parts[1].padEnd(digits, '0').substring(0, digits)
    return parts.joinToString(".")
}

fun nullAppend(x: Any?, y: String): String? {
    x ?: return null
    return x.toString() + y
}


@Composable
fun Grid(navController: NavController, repository: StateRepository) {
    val uriHandler = LocalUriHandler.current
    val selectedJug by repository.selectedJug.collectAsState(null)
    val totalLitres by repository.totalLitres.collectAsState(null)
    val litresPerSecond by repository.litresPerSecond.collectAsState(null)
    val totalLitresFilter by repository.totalLitresFilter.collectAsState(null)
    val dailyLitres by repository.dailyLitres.collectAsState(null)
    val hasFilter = (selectedJug?.filtercapacity ?: 0) != 0
    val usedPercentage = filterStatus(
        totalLitresFilter,
        selectedJug?.filtercapacity
    )

    ScrollableContent {
        Text(
            text = selectedJug?.name ?: stringResource(id = R.string.jug_not_selected),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Metric(
                    stringResource(R.string.total_consumption),
                    nullAppend(nullRound(totalLitres), stringResource(R.string.l))
                        ?: stringResource(R.string.n_a),
                    cardColor = R.color.aquamarine
                )
                Metric(
                    stringResource(R.string.daily_consumption),
                    nullAppend(nullRound(dailyLitres), stringResource(R.string.l_d))
                        ?: stringResource(R.string.n_a),
                    cardColor = R.color.aquamarine
                )
            }
            Column(
                modifier = Modifier.padding(16.dp, 0.dp)
            ) {
                Metric(
                    stringResource(R.string.filter_capacity),
                    nullAppend(selectedJug?.filtercapacity, "L") ?: stringResource(R.string.n_a),
                    cardColor = R.color.aquamarine
                )
                Metric(
                    stringResource(R.string.estimated_filter_life),
                    if (hasFilter) nullAppend(
                        nullRound(
                            calculateEstimatedFilterLifeHours(
                                dailyLitres,
                                totalLitresFilter,
                                selectedJug?.filtercapacity
                            ), 0
                        ), stringResource(R.string.d)
                    ) ?: stringResource(R.string.n_a) else stringResource(R.string.n_a),
                    cardColor = R.color.aquamarine
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Metric(
                stringResource(R.string.litres_per_second),
                nullAppend(nullRound(litresPerSecond, 2), stringResource(R.string.l_s))
                    ?: stringResource(R.string.n_a),
                cardColor = R.color.crab,
                true
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Metric(
                stringResource(R.string.quantity_of_plastic_saved),
                nullAppend(nullRound(plasticSaved(totalLitres), 3), stringResource(R.string.kg))
                    ?: stringResource(R.string.n_a),
                cardColor = R.color.seaweed,
                true
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp)
        ) {
            Metric(
                stringResource(R.string.filter_usage),
                if (hasFilter) nullAppend(
                    nullRound(
                        usedPercentage
                    ), stringResource(R.string.percentage)
                ) ?: stringResource(R.string.n_a) else stringResource(R.string.n_a),
                cardColor = R.color.octopus,
                red = (usedPercentage
                    ?: 0.0) >= 80.0,
                isLast = true
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            ExtendedFloatingActionButton(
                onClick = { uriHandler.openUri("https://www.google.com/search?q=jug+filter") },
                icon = {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        stringResource(R.string.buy_filter),
                        tint = colorResource(id = R.color.cream)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.buy_filter),
                        color = colorResource(id = R.color.cream)
                    )
                },
                containerColor = colorResource(id = R.color.water),
            )
        }
    }
}

@Composable
@Preview
fun DashboardPreview() {
    val navController = rememberNavController()
    Decorations(navController, Route.DASHBOARD) {
        Dashboard(navController, FAKE_REPOSITORY)
    }
}

@Composable
fun Dashboard(
    navController: NavController,
    repository: StateRepository,
) {
    Grid(navController, repository)
}
