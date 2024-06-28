package com.island.iot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.island.iot.ui.theme.IOTTheme
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {
    private val pairing = PairingImpl(this)
    private var firebaseToken: String? = null

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { }

    private fun askNotificationPermission() {
        val permission = mutableListOf<String>()
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(
                    NotificationChannel(
                        "test",
                        "test",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
                // FCM SDK (and your app) can post notifications.
            } else {
                permission.add(android.Manifest.permission.POST_NOTIFICATIONS)
                // Directly ask for the permission
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) permission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permission.isNotEmpty())
            requestPermissionLauncher.launch(permission.toTypedArray())
    }

    companion object {
        @Composable
        fun getPairing(): Pairing {
            return getMainActivity()?.pairing ?: FAKE_PAIRING
        }

        @Composable
        fun getMainActivity(): MainActivity? {
            val context = LocalContext.current
            return if (context is MainActivity) context else null
        }

        @Composable
        fun getToken(): String? {
            return getMainActivity()?.firebaseToken
        }
    }

    private fun setupFirebase() {
        val viewModel: StateViewModel by viewModels()
        viewModel.repository.launch { firebaseToken = Firebase.messaging.token.await() }
        askNotificationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pairing.onCreate(this)
        setContent {
            Root()
        }
        setupFirebase()

        // DEBUG
//        val viewModel: StateViewModel by viewModels()
//        viewModel.repository.launch { Log.d("Obtained location", pairing.getLocation().toString()) }
    }
}

enum class Route(
    val id: String,
    val title: Int,
    val bottomBar: Boolean,
    private val clearStack: Boolean,
) {
    DASHBOARD("dashboard", R.string.dashboard, true, true),
    LOGINPAGE("loginpage", R.string.login, false, true),
    REGISTERPAGE("registerpage", R.string.register, false, false),
    ACCOUNT("account", R.string.account, true, true),
    CHANGE_PASSWORD("changePassword", R.string.change_password, true, false),
    CHARTS("charts", R.string.charts, true, true),
    JUGS("jugs", R.string.jugs, true, true),
    NEWS("news", R.string.news_feed, true, false);

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

enum class BottomButton(val route: Route, val text: Int, val icon: ImageVector) {
    DASHBOARD(Route.DASHBOARD, R.string.dashboard, Icons.Filled.Home),
    CHARTS(Route.CHARTS, R.string.charts, Icons.Filled.Menu),
    JUGS(Route.JUGS, R.string.jugs, Icons.Filled.Create),
    ACCOUNT(Route.ACCOUNT, R.string.account, Icons.Filled.Person);

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
                                text = LocalContext.current.getString(currentRoute.title),
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
                                        contentDescription = stringResource(R.string.go_back)
                                    )
                                }
                        },
                        actions = {
                            IconButton(onClick = { Route.NEWS.open(navController) }) {
                                Icon(
                                    painterResource(id = R.drawable.news),
                                    stringResource(R.string.news_icon),
                                    tint = colorResource(id = R.color.cream)
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
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = LocalContext.current.getString(item.text)
                                    )
                                },
                                label = { Text(LocalContext.current.getString(item.text)) },
                                selected = item == selectedButton,
                                onClick = {
                                    item.route.open(navController)
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
    val visible = rememberSaveable { mutableStateOf(true) }
    PromptDialog(
        onDismissRequest = { callback(null) },
        dialogTitle = stringResource(R.string.wifi_password),
        icon = Icons.Default.Edit,
        visibleState = visible,
        password = true
    ) {
        callback(it)
        true
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
                Account(controller, state)
            }
            composable(Route.CHANGE_PASSWORD.id) {
                ChangePassword(controller, state)
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
            state.setWifiPassword(it ?: "")
        }
    if (pairingState == PairingState.CONNECTING) {
        BlockingDialog(dialogTitle = stringResource(R.string.connecting_the_jug))
    }
    if (pairingState == PairingState.SENDING) {
        BlockingDialog(dialogTitle = stringResource(R.string.pairing_the_jug))
    }
    if (pairingState == PairingState.DONE) {
        AlertDialog(
            stringResource(R.string.jug_successfully_paired),
            "",
            icon = Icons.Filled.CheckCircle
        ) {
            state.resetPairingState()
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
