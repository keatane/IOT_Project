package com.island.iot

import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.WifiDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiNetworkSpecifier
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import java.util.regex.Pattern


class MainActivity : ComponentActivity() {
    val DEVICE_REQUEST_CODE = 1
    val NORMAL_WIFI_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root(
                searchJugs = { search(this, pairingRequest, DEVICE_REQUEST_CODE) },
                onPaired = { disconnect() })
        }
    }

    val deviceFilter: WifiDeviceFilter =
        WifiDeviceFilter.Builder().setNamePattern(Pattern.compile("jug_\\d+")).build()
    val pairingRequest = AssociationRequest.Builder().addDeviceFilter(deviceFilter).build()

    val allPairingRequest = AssociationRequest.Builder().build()

    fun search(context: Context, request: AssociationRequest, code: Int) {
        val deviceManager = context.getSystemService(CompanionDeviceManager::class.java)

        deviceManager.associate(
            request,
            object : CompanionDeviceManager.Callback() {
                // Called when a device is found. Launch the IntentSender so the user
                // can select the device they want to pair with.
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    startIntentSenderForResult(
                        chooserLauncher,
                        code, null, 0, 0, 0
                    )
                }

                override fun onFailure(error: CharSequence?) {
                    // Handle the failure.
                }
            }, null
        )

    }

    fun connect(ssid: String) {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        connectivityManager.requestNetwork(networkRequest, object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("fdhjhjdsjdjs", "CONNETED YAYAYAYYAY")
                connectivityManager.bindProcessToNetwork(network)
                search(this@MainActivity, allPairingRequest, NORMAL_WIFI_CODE)
            }
        })
    }

    fun disconnect() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.bindProcessToNetwork(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            DEVICE_REQUEST_CODE -> when (resultCode) {
                RESULT_OK -> {
                    // The user chose to pair the app with a Bluetooth device.
                    val deviceToPair: ScanResult? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    Log.d("dhjfhdjhhdjhf", deviceToPair!!.BSSID)
                    val viewModel: StateViewModel by viewModels()
                    viewModel.repository.memoryDataSource.enterConnecting()
                    connect(deviceToPair.SSID)
                }
            }

            NORMAL_WIFI_CODE -> when (resultCode) {
                RESULT_OK -> {
                    // The user chose to pair the app with a Bluetooth device.
                    val deviceToPair: ScanResult? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    Log.d("dhjfhdjhhdjhf", deviceToPair!!.BSSID)
                    val viewModel: StateViewModel by viewModels()
                    viewModel.repository.memoryDataSource.enterAskPassword(deviceToPair.SSID)
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

enum class Route(val id: String) {
    DASHBOARD("dashboard"),
    LOGINPAGE("loginpage"),
    REGISTERPAGE("registerpage"),
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
                if (bottomBarVisible || newsFeedVisible) {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text(
                                text = if (newsFeedVisible) "News Feed" else BottomButton.entries[selectedItem].text,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navigate(Route.DASHBOARD.id) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navigate(Route.NEWS.id) }) {
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
                    NavigationBar {
                        val prevSelected = selectedItem
                        BottomButton.entries.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.text) },
                                label = { Text(item.text) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem =
                                        index; if (prevSelected != selectedItem) navigate(item.route.id)
                                }
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
fun PasswordDialog(callback: (String) -> Unit) {
    DialogGeneric(
        onDismissRequest = { },
        onConfirmation = callback,
        dialogTitle = "Wifi password",
        icon = Icons.Default.Edit
    )
}

@Composable
fun Root(viewModel: StateViewModel = viewModel(), searchJugs: () -> Unit, onPaired: () -> Unit) {
    val scope = viewModel.viewModelScope
    val state = viewModel.repository
    val controller = rememberNavController()
    var bottomBarVisible by rememberSaveable { mutableStateOf(false) }
    var newsFeedVisible by rememberSaveable { mutableStateOf(false) }
    val jugs by viewModel.repository.memoryDataSource.jugList.collectAsState()
    var selectedJug by rememberSaveable {
        mutableStateOf(0)
    }
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
            composable(Route.REGISTERPAGE.id) {
                RegisterPage(
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
                    login = {
                        bottomBarVisible = false
                        newsFeedVisible = false
                        navigateTo(controller, Route.LOGINPAGE.id)
                    }
                )
            }
            composable(Route.LOGINPAGE.id) {
                LoginPage(
                    register = {
                        bottomBarVisible = false
                        newsFeedVisible = false
                        navigateTo(controller, Route.REGISTERPAGE.id)
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
            composable(Route.DASHBOARD.id) {
                Dashboard(
                    initDashboard = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                    },
                    jugsList = jugs, selectedJug = selectedJug
                )
            }
            composable(Route.ACCOUNT.id) {
                Account(
                    initAccount = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                    },
                    passwordPage = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                        navigateTo(controller, Route.CHANGE_PASSWORD.id)
                    },
                    deleteAccount = { username, password ->
                        scope.launch {
                            state.delete(
                                username,
                                password
                            )
                        }
                    }
                )
            }
            composable(Route.CHANGE_PASSWORD.id) {
                ChangePassword(
                    accountPage = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                        navigateTo(controller, Route.ACCOUNT.id)
                    },
                )
            }
            composable(Route.CHARTS.id) {
                Chart(
                    initCharts = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                    }
                )
            }
            composable(Route.JUGS.id) {
                Jugs(
                    initJugs = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                    },
                    searchJugs = searchJugs,
                    changeFilter = { jugId, filter ->
                        scope.launch {
                            state.changeFilter(jugId, filter)
                        }
                    },
                    dashboardPage = {
                        bottomBarVisible = true
                        newsFeedVisible = false
                        navigateTo(controller, Route.DASHBOARD.id)
                    },
                    jugList = jugs,
                    renameJug = { id, name ->
                        scope.launch {
                            state.renameJug(id, name)
                        }
                    },
                    deleteJug = { id -> scope.launch { state.deleteJug(id) } },
                    selectJug = { selectedJug = it }
                )
            }
            composable(Route.NEWS.id) {
                News(
                    initNews = {
                        bottomBarVisible = true
                        newsFeedVisible = true
                    }
                )
            }
        }
    }
    LoginNavigate(user = viewModel.repository.user) {
        bottomBarVisible = true
        newsFeedVisible = false
        navigateTo(controller, Route.DASHBOARD.id)
    }
    val pairingState by state.memoryDataSource.pairingState.collectAsState()
    if (pairingState == PairingState.ASK_PASSWORD)
        PasswordDialog {
            state.memoryDataSource.wifiPassword.value = it
            scope.launch { state.memoryDataSource.enterSending() }.invokeOnCompletion { onPaired() }
        }
}
