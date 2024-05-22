package com.island.iot

import android.os.Bundle
import android.text.BoringLayout.Metrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.island.iot.ui.theme.IOTTheme

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }

    }

    val maxWidth = Modifier
        .fillMaxWidth()
        .padding(16.dp)

    @Composable
    fun TextFieldState(
        label: String,
        password: Boolean = false,
        text: String,
        onChange: (String) -> Unit
    ) {

        TextField(
            value = text,
            onValueChange = onChange,
            label = { Text(label) },
            modifier = maxWidth,
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
        )
    }

    @Composable
    fun Grid(onRegister: (String, String) -> Unit) {
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var confPassword by remember {
            mutableStateOf("")
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
    fun Metric(title:String, value: String){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(4.dp)
        ){
            Text(title, modifier = Modifier.padding(16.dp))
            Text(value, modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun Grid(){
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Metric("Total consumption", "LLL")
            Metric("Daily consumption", "LLL")
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Metric("Filter capacity", "LLL")
            Metric("Filter life", "LLL")
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Metric("Quantity of plastic save", "LLL")
        }
        Row(
            modifier = Modifier.padding(32.dp)
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ){
                Text("Filter status", modifier = Modifier.padding(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "",
                    contentScale = ContentScale.Fit
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(16.dp)) {
                Text(text = "Buy filter")
            }
        }
    }

    @Composable
    fun Layout(onRegister: (String, String) -> Unit) {
        IOTTheme {
            Scaffold(
                topBar = {
                    Text("Dashboard", modifier = Modifier.padding(16.dp))
                },
                bottomBar = {
                    NavigationBar (containerColor = Color(0xFF6200EE)) {
                        Button(onClick = { /*TODO*/ }) {
                            Text("Home")
                        }
                        Button(onClick = { /*TODO*/ }) {
                            Text("Dashboard")
                        }
                        Button(onClick = { /*TODO*/ }) {
                            Text("Settings")
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
                        Grid()
                    }
                }
            }
        }
    }
}