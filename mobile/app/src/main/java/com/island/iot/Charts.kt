package com.island.iot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation

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
fun TimeChart(data: List<Pair<Int, Double>>) {
    LineChart(
        linesChartData = listOf(
            LineChartData(
                lineDrawer = SolidLineDrawer(),
                points = data.map { LineChartData.Point(it.second.toFloat(), it.first.toString()) }
            )
        ),
        // Optional properties.
        modifier = Modifier
            .width(360.dp)
            .height(360.dp),
        animation = simpleChartAnimation(),
        pointDrawer = FilledCircularPointDrawer(),
        xAxisDrawer = SimpleXAxisDrawer(),
        yAxisDrawer = SimpleYAxisDrawer(),
        horizontalOffset = 5f,
        labels = listOf("label 1")
    )
}

@Composable
fun Chart(
    controller: NavController, repository: StateRepository
) {

    ScrollableContent{
        val hourLitres by repository.hourLitres.collectAsState(initial = null)
        val weekLitres by repository.weekLitres.collectAsState(initial = null)
        Text(
            text = "Litres consumed in the last days",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        if(weekLitres!=null) TimeChart(weekLitres!!.mapIndexed{x,y->Pair(x,y)})
        else Text("Loading data")
        Text(
            text = "Litres consumed in the last hour",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        if(hourLitres!=null)
            TimeChart(hourLitres!!.mapIndexed{x,y->Pair(x,y)})
        else Text("Loading data")
    }
}
