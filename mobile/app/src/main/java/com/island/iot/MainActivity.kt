package com.island.iot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.island.iot.ui.theme.IOTTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }
}

enum class Route(val id: String) {
    DASHBOARD("dashboard"),
    LOGINPAGE("loginpage"),
    ACCOUNT("account"),
    CHANGE_PASSWORD("changePassword"),
    CHARTS("charts"),
    JUGS("jugs"),
    NEWS("news")
}

enum class BottomButton(val route: Route, val text: String, val icon: ImageVector) {
    DASHBOARD(Route.DASHBOARD, "Dashboard", Icons.Filled.Home),
    CHARTS(Route.CHARTS, "Charts", Icons.Filled.Menu),
    JUGS(Route.JUGS, "Jugs", Icons.Filled.Create),
    ACCOUNT(Route.ACCOUNT, "Account", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Decorations(
    bottomBarVisible: Boolean = true,
    newsFeedVisible: Boolean = false,
    navigate: (String) -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    IOTTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                    title = {
                        Text(
                            text = if (newsFeedVisible) "News Feed" else if (bottomBarVisible) BottomButton.entries[selectedItem].text else "Login/Register",
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (bottomBarVisible) {
                            IconButton(onClick = { navigate(Route.DASHBOARD.id) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back"
                                )
                            }
                        }
                    },
                    actions = {
                        if (bottomBarVisible) {
                            IconButton(onClick = { navigate(Route.NEWS.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = "News feed"
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                if (bottomBarVisible)
                    NavigationBar {
                        BottomButton.entries.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.text) },
                                label = { Text(item.text) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index;navigate(item.route.id) }
                            )
                        }
                    }
            }
        ) {
            content(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .consumeWindowInsets(it)
            )
        }
    }
}

@Composable
fun LoginNavigate(user: Flow<User?>, navigate: () -> Unit) {
    val userValue by user.collectAsState(initial = null)
    if (userValue !== null) navigate()
}

fun navigateTo(controller: NavController, id: String) {
    controller.popBackStack(controller.currentDestination!!.route!!, inclusive = true)
    controller.navigate(id)
}

@Composable
fun Root(viewModel: StateViewModel = viewModel()) {
    val scope = viewModel.viewModelScope
    val state = viewModel.repository
    val controller = rememberNavController()
    var bottomBarVisible by rememberSaveable { mutableStateOf(false) }
    var newsFeedVisible by rememberSaveable { mutableStateOf(false) }
    Decorations(
        bottomBarVisible = bottomBarVisible,
        newsFeedVisible = newsFeedVisible,
        navigate = {
            navigateTo(controller, it)
        }
    ) {
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
                        newsFeedVisible = false
                        navigateTo(controller, Route.DASHBOARD.id)
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
            composable(Route.DASHBOARD.id) { Dashboard(
                initDashboard = {
                    bottomBarVisible = true
                    newsFeedVisible = false
                }
            ) }
            composable(Route.ACCOUNT.id) { Account(
                passwordPage = {
                    bottomBarVisible = true
                    newsFeedVisible = false
                    navigateTo(controller, Route.CHANGE_PASSWORD.id)
                },
            ) }
            composable(Route.CHANGE_PASSWORD.id) { ChangePassword(
                accountPage = {
                    bottomBarVisible = true
                    newsFeedVisible = false
                    navigateTo(controller, Route.ACCOUNT.id)
                },
            ) }
            composable(Route.CHARTS.id) { Chart(
                initCharts = {
                    bottomBarVisible = true
                    newsFeedVisible = false
                }
            ) }
            composable(Route.JUGS.id) { Jugs(
                initJugs = {
                    bottomBarVisible = true
                    newsFeedVisible = false
                }
            ) }
            composable(Route.NEWS.id) { News(
                initNews = {
                    bottomBarVisible = true
                    newsFeedVisible = true
                }
            ) }
        }
    }
    LoginNavigate(user = viewModel.repository.user) {
        bottomBarVisible = true
        newsFeedVisible = false
        navigateTo(controller, Route.DASHBOARD.id)
    }
}
