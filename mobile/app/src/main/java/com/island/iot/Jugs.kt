package com.island.iot

import androidx.compose.material3.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.island.iot.ui.theme.IOTTheme

@Composable
fun Jug(title: String) {
    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth().padding(16.dp, 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, modifier = Modifier.padding(8.dp).weight(1f))
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Create, contentDescription = "Rename")
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Build, contentDescription = "Change filter")
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Change filter")
            }
        }
    }
}

@Composable
fun Section() {
    Column {
        Jug("Kitchen Jug")
        Jug("Kitchen Jug")
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Pair a new jug")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    Decorations{
        Jugs()
    }
}

@Composable
fun Jugs() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(0.dp, 32.dp)
    ) {
        Section()
    }
}
