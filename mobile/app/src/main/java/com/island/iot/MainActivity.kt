package com.island.iot

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.island.iot.ui.theme.IOTTheme


class MainActivity : ComponentActivity() {
    private val pairing = PairingImpl(this)

    companion object {
        @Composable
        fun getPairing(): Pairing {
            val context = LocalContext.current
            return if (context is MainActivity) context.pairing else FAKE_PAIRING
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pairing.onCreate(this)
        setContent {
            Root()
        }
    }
}

enum class Route(
    val id: String,
    val title: String,
    val bottomBar: Boolean,
    val clearStack: Boolean
) {
    DASHBOARD("dashboard", "Dashboard", true, true),
    LOGINPAGE("loginpage", "Login Page", false, true),
    REGISTERPAGE("registerpage", "Register Page", false, false),
    ACCOUNT("account", "Account", true, true),
    CHANGE_PASSWORD("changePassword", "Change Password", true, false),
    CHARTS("charts", "Charts", true, true),
    JUGS("jugs", "Jugs", true, true),
    NEWS("news", "News Feed", true, false);

    companion object {
        fun getCurrentRoute(backStackEntry: NavBackStackEntry?): Route? {
            val id = backStackEntry?.destination?.route ?: return null
            return Route.entries.find { it.id == id }!!
        }
    }

    fun open(controller: NavController) {
        if (clearStack)
            controller.popBackStack(controller.graph.id, inclusive = true)
        controller.navigate(id)
    }
}

enum class BottomButton(val route: Route, val text: String, val icon: ImageVector) {
    DASHBOARD(Route.DASHBOARD, "Dashboard", Icons.Filled.Home),
    CHARTS(Route.CHARTS, "Charts", Icons.Filled.Menu),
    JUGS(Route.JUGS, "Jugs", Icons.Filled.Create),
    ACCOUNT(Route.ACCOUNT, "Account", Icons.Filled.Person);

    companion object {
        fun getSelectedButton(route: Route): BottomButton? {
            return BottomButton.entries.find { it.route == route }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Decorations(
    navController: NavController,
    previewRoute: Route?,
    content: @Composable (Modifier) -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val prevBackStackEntry = navController.previousBackStackEntry
    val currentRoute = Route.getCurrentRoute(currentBackStackEntry) ?: previewRoute!!
    Log.d("djsjdsd", currentRoute.toString())
    val bottomBarVisible = currentRoute.bottomBar
    val selectedButton = BottomButton.getSelectedButton(currentRoute)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    IOTTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (bottomBarVisible) {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = colorResource(id = R.color.rock),
                            titleContentColor = colorResource(id = R.color.cream),
                            navigationIconContentColor = colorResource(id = R.color.cream),
                            actionIconContentColor = colorResource(id = R.color.cream),
                        ),
                        title = {
                            Text(
                                text = currentRoute.title,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            if (prevBackStackEntry != null)
                                IconButton(onClick = {
                                    while (navController.previousBackStackEntry != null)
                                        navController.popBackStack()
                                }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Go back"
                                    )
                                }
                        },
                        actions = {
                            IconButton(onClick = { Route.NEWS.open(navController) }) {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = "News feed"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }
            },
            bottomBar = {
                if (bottomBarVisible)
                    NavigationBar(
                        containerColor = colorResource(id = R.color.rock),
                        contentColor = colorResource(id = R.color.cream),
                    ) {
                        BottomButton.entries.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.text) },
                                label = { Text(item.text) },
                                selected = item == selectedButton,
                                onClick = {
                                    item.route.open(navController)
//                                    selectedItem = index; if (prevSelected != selectedItem) navigate(item.route)
                                },
                                colors = NavigationBarItemColors(
                                    selectedIconColor = colorResource(id = R.color.abyss),
                                    unselectedIconColor = colorResource(id = R.color.cream),
                                    selectedTextColor = colorResource(id = R.color.oxygen),
                                    unselectedTextColor = colorResource(id = R.color.cream),
                                    disabledIconColor = Color.Gray,
                                    disabledTextColor = Color.Gray,
                                    selectedIndicatorColor = colorResource(id = R.color.oxygen),
                                )
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
fun PasswordDialog(callback: (String?) -> Unit) {
    var text by remember { mutableStateOf("") }
    GenericDialog(
        onDismissRequest = { callback(null) },
        onConfirmation = { callback(text) },
        dialogTitle = "Wifi password",
        icon = Icons.Default.Edit,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun Root(viewModel: StateViewModel = viewModel()) {
    val state = viewModel.repository
    val controller = rememberNavController()
    Decorations(navController = controller, previewRoute = Route.LOGINPAGE) {
        NavHost(
            navController = controller,
            modifier = it,
            startDestination = Route.LOGINPAGE.id
        ) {
            composable(Route.REGISTERPAGE.id) {
                RegisterPage(
                    controller, state
                )
            }
            composable(Route.LOGINPAGE.id) {
                LoginPage(controller, state)
            }
            composable(Route.DASHBOARD.id) {
                Dashboard(controller, state)
            }
            composable(Route.ACCOUNT.id) {
                Account(
                    controller, state
                )
            }
            composable(Route.CHANGE_PASSWORD.id) {
                ChangePassword(
                    controller, state
                )
            }
            composable(Route.CHARTS.id) {
                Chart(
                    controller, state
                )
            }
            composable(Route.JUGS.id) {
                Jugs(
                    controller, state
                )
            }
            composable(Route.NEWS.id) {
                News(
                    controller, state
                )
            }
        }
        SideEffects(controller = controller, state = state)
    }
}

@Composable
fun SideEffects(controller: NavController, state: StateRepository) {
    val pairingState by state.pairingState.collectAsState()
    if (pairingState == PairingState.ASK_PASSWORD)
        PasswordDialog {
            state.setWifiPassword(it!!)
        }
    if (pairingState == PairingState.CONNECTING) {
        BlockingDialog(dialogTitle = "Connecting the jug")
    }
    if (pairingState == PairingState.SENDING) {
        BlockingDialog(dialogTitle = "Pairing the jug")
    }
    if (pairingState == PairingState.DONE) {
        GenericDialog(
            onDismissRequest = { state.resetPairingState() },
            onConfirmation = { state.resetPairingState() },
            dialogTitle = "Paired the jug",
            icon = Icons.Filled.Home
        ) {

        }
    }
    val user by state.user.collectAsState(null)
    LaunchedEffect(key1 = user) {
        if (user == null) Route.LOGINPAGE.open(controller)
        if (user != null) Route.DASHBOARD.open(controller)
    }
    val lastError by state.lastError.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = lastError) {
        if (lastError != null)
            Toast.makeText(context, lastError, Toast.LENGTH_SHORT).show()
    }
}