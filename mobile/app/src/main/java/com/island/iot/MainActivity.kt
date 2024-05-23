package com.island.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class Route(val id:String){
    DASHBOARD("dashboard"),
    LOGINPAGE("loginpage"),
    ACCOUNT("account"),
    CHANGE_PASSWORD("changePassword"),
    CHARTS("charts"),
    JUGS("jugs")
}

enum class BottomButton(val route:Route,val text:String,val icon:ImageVector){
    DASHBOARD(Route.DASHBOARD,"Dashboard",Icons.Filled.Home),
    CHARTS(Route.CHARTS,"Charts",Icons.Filled.Menu),
    JUGS(Route.JUGS,"Justs",Icons.Filled.Create),
    ACCOUNT(Route.ACCOUNT,"Account",Icons.Filled.Person)
}

@Composable
fun Decorations(
    bottomBarVisible: Boolean = true,
    navigate:(String)->Unit={},
    content: @Composable (Modifier) -> Unit
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    IOTTheme {
        Scaffold(
            topBar = {
                Text("Dashboard", modifier = Modifier.padding(16.dp))
            },
            bottomBar = {
                if (bottomBarVisible)
                    NavigationBar {
                        BottomButton.entries.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.text) },
                                label = { Text(item.text) },
                                selected = selectedItem == index,
                                onClick = { selectedItem=index;navigate(item.route.id) }
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
fun Root(viewModel: StateViewModel = viewModel()) {
    val scope = viewModel.viewModelScope
    val state = viewModel.repository
    val controller = rememberNavController()
    var bottomBarVisible by rememberSaveable { mutableStateOf(false) }
    // A surface container using the 'background' color from the theme
    Decorations(
        bottomBarVisible = bottomBarVisible,
        navigate = { controller.navigate(it) }) {
        NavHost(
            navController = controller,
            modifier = it,
            startDestination = Route.LOGINPAGE.id
        ) {
            composable(Route.LOGINPAGE.id) {
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
                        bottomBarVisible = true
                        controller.navigate(Route.DASHBOARD.id)
                    },
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
            composable(Route.DASHBOARD.id) { Dashboard() }
            composable(Route.ACCOUNT.id) { Account() }
            composable(Route.CHANGE_PASSWORD.id) { ChangePassword() }
            composable(Route.CHARTS.id) { Chart() }
            composable(Route.JUGS.id) { Jugs() }
        }
    }
}
