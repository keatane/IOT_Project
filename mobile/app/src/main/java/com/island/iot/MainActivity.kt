package com.island.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.island.iot.ui.theme.IOTTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }
}

@Composable
fun Decorations(bottomBarVisible:Boolean=true,content:@Composable (Modifier)->Unit){
    val items = listOf("Dashboard", "Charts", "Jugs", "Account")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Menu, Icons.Filled.Create, Icons.Filled.Person)
    var selectedItem by remember { mutableIntStateOf(0) }
    IOTTheme {
        Scaffold(
            topBar = {
                Text("Dashboard", modifier = Modifier.padding(16.dp))
            },
            bottomBar = {
                if(bottomBarVisible)
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {  }
                        )
                    }
                }
            }
        ) {
            content(
                Modifier
                    .padding(
                        it.calculateStartPadding(LayoutDirection.Ltr),
                        it.calculateTopPadding(),
                        it.calculateEndPadding(LayoutDirection.Ltr),
                        it.calculateBottomPadding()
                    )
                    .consumeWindowInsets(it)
            )
        }
    }
}

@Composable
fun Root(viewModel:StateViewModel= viewModel()) {
    val scope = viewModel.viewModelScope
    val state = viewModel.repository
    val controller = rememberNavController()
    var bottomBarVisible by rememberSaveable { mutableStateOf(false) }
    // A surface container using the 'background' color from the theme
    Decorations (bottomBarVisible = bottomBarVisible){
        NavHost(
            navController = controller,
            modifier = it,
            startDestination = "loginpage"
        ) {
            composable("loginpage") {
                LoginPage(
                    register = { username, password ->
                        scope.launch {
                            state.register(
                                username,
                                password
                            )
                        }
                    },
                    homePage = {
                        bottomBarVisible=true
                        controller.navigate("dashboard") },
                    login = { username, password ->
                        scope.launch {
                            state.login(
                                username,
                                password
                            )
                        }
                    }
                )
            }
            composable("dashboard") { Dashboard() }
        }
    }
}
