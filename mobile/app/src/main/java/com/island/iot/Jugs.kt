package com.island.iot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.island.iot.ui.theme.IOTTheme

class Jugs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }

    }

    @Composable
    fun Jug(title: String){
        OutlinedCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.padding(6.dp, 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = title, modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(text = "Change filter")
                }
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(text = "Rename")
                }
            }
        }
    }

    @Composable
    fun Section() {
        Column{
            Jug("Kitchen Jug")
            Jug("Kitchen Jug")
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Pair a new jug")
            }
        }
    }

    @Composable
    fun Root(viewModel: StateViewModel = viewModel()) {
        Layout { username, password -> viewModel.register(username, password) }
    }

    @Preview(showBackground = true)
    @Composable
    fun Preview() {
        Layout { _, _ -> }
    }

    @Composable
    fun Layout(onRegister: (String, String) -> Unit) {
        val mContext = LocalContext.current
        var selectedItem by remember { mutableIntStateOf(0) }
        val items = listOf("Dashboard", "Charts", "Jugs", "Account")
        val icons = listOf(Icons.Filled.Home, Icons.Filled.Menu, Icons.Filled.Create, Icons.Filled.Person)
        val classes = listOf(Dashboard::class.java, Charts::class.java, Jugs::class.java, Account::class.java)
        IOTTheme {
            Scaffold(
                topBar = {
                    Text("Your jugs", modifier = Modifier.padding(16.dp))
                },
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(icons[index], contentDescription = item) },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index; mContext.startActivity(
                                    Intent(mContext, classes[index])
                                ) }
                            )
                        }
                    }
                }
            ){
                System.out.println(it)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(0.dp, 32.dp)
                    ) {
                        Section()
                    }
                }
            }
        }
    }
}