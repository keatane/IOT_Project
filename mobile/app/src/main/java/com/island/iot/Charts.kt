package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import java.util.Calendar

@Preview(showBackground = true)
@Composable
fun ChartsPreview() {
    val controller = rememberNavController()
    Decorations(
        controller, Route.CHARTS
    ) {
        Chart(controller, FAKE_REPOSITORY)
    }
}

val DAY_OF_WEEK = mapOf(
    Calendar.MONDAY to "Mon",
    Calendar.TUESDAY to "Tue",
    Calendar.WEDNESDAY to "Wed",
    Calendar.THURSDAY to "Thu",
    Calendar.FRIDAY to "Fri",
    Calendar.SATURDAY to "Sat",
    Calendar.SUNDAY to "Sun"
)

@Composable
fun DailyChart(data: List<Pair<Int, Double>>) {
    BarChart(
        barChartData = BarChartData(bars = data.map {
            val delta=6-it.first
            val day=Calendar.getInstance()
            day.add(Calendar.DATE,-delta)
            BarChartData.Bar(
                label = DAY_OF_WEEK[day.get(Calendar.DAY_OF_WEEK)]!!,
                value = it.second.toFloat(),
                color = colorResource(id = R.color.crab)
            )
        }),
        modifier = Modifier
            .width(350.dp)
            .height(250.dp)
            .padding(32.dp),
        animation = simpleChartAnimation(),
        barDrawer = SimpleBarDrawer(),
        xAxisDrawer = SimpleXAxisDrawer(axisLineColor = colorResource(id = R.color.cream)),
        yAxisDrawer = SimpleYAxisDrawer(labelTextColor = colorResource(id = R.color.cream), axisLineColor = colorResource(id = R.color.cream)),
        labelDrawer = SimpleValueDrawer(labelTextColor = colorResource(id = R.color.cream), drawLocation = SimpleValueDrawer.DrawLocation.XAxis)
    )
}

@Composable
fun TimeChart(data: List<Pair<Int, Double>>) {
    LineChart(
        linesChartData = listOf(
            LineChartData(
                lineDrawer = SolidLineDrawer(),
                points = data.map { LineChartData.Point(it.second.toFloat(), it.first.toString()) }
            )
        ),
        modifier = Modifier
            .width(350.dp)
            .height(250.dp)
            .padding(16.dp),
        animation = simpleChartAnimation(),
        pointDrawer = FilledCircularPointDrawer(
            color = colorResource(id = R.color.ocean),
            diameter = 2.dp
        ),
        xAxisDrawer = com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer(
            labelTextColor = colorResource(
                id = R.color.cream
            ), axisLineColor = colorResource(id = R.color.cream)
        ),
        yAxisDrawer = com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer(
            labelTextColor = colorResource(
                id = R.color.cream
            ), axisLineColor = colorResource(id = R.color.cream)
        ),
        horizontalOffset = 5f,
        labels = data.mapIndexed { index, elem -> if (index % 5 == 0) elem.first.toString() else "" }
    )
}

@Composable
fun Chart(
    controller: NavController, repository: StateRepository
) {
    val hourLitres by repository.hourLitres.collectAsState(initial = null)
    val weekLitres by repository.weekLitres.collectAsState(initial = null)
    val selectedJug by repository.selectedJug.collectAsState(initial = null)
    ScrollableContent {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp)
        ) {
            Text(
                text = selectedJug?.name ?: "Jug not selected",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.cream),
                modifier = Modifier.padding(16.dp)
            )
            OutlinedCard(
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.abyss)),
                border = BorderStroke(2.dp, colorResource(id = R.color.rock)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Litres consumed in the last hour",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.cream),
                    modifier = Modifier.padding(16.dp)
                )
                if (hourLitres != null)
                    TimeChart(hourLitres!!.mapIndexed { x, y -> Pair(x, y) })
                else Text(
                    "Loading data", modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
            OutlinedCard(
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.abyss)),
                border = BorderStroke(2.dp, colorResource(id = R.color.rock)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Litres consumed in the last days",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.cream),
                    modifier = Modifier.padding(16.dp)
                )
                if (weekLitres != null) DailyChart(weekLitres!!.mapIndexed { x, y -> Pair(x, y) })
                else Text(
                    "Loading data", modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
