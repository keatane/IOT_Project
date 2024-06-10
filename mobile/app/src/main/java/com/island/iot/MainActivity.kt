package com.island.iot

import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.WifiDeviceFilter
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.island.iot.ui.theme.IOTTheme
import kotlinx.coroutines.flow.Flow
import java.util.regex.Pattern


class MainActivity : ComponentActivity() {
    val DEVICE_REQUEST_CODE = 1
    val NORMAL_WIFI_CODE = 2

    companion object {
        @Composable
        fun get(): MainActivity {
            return LocalContext.current as MainActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }

    val deviceFilter: WifiDeviceFilter =
        WifiDeviceFilter.Builder().setNamePattern(Pattern.compile("jug_\\d+")).build()
    val pairingRequest = AssociationRequest.Builder().addDeviceFilter(deviceFilter).build()

    val allPairingRequest = AssociationRequest.Builder().build()

    fun searchJugs() {
        search(pairingRequest, DEVICE_REQUEST_CODE)
    }

    private fun search(request: AssociationRequest, code: Int) {
        val deviceManager = getSystemService(CompanionDeviceManager::class.java)

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

    private fun connect(ssid: String) {
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
                search(allPairingRequest, NORMAL_WIFI_CODE)
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
                    viewModel.repository.enterConnecting()
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
                    viewModel.repository.enterAskPassword(deviceToPair.SSID)
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

enum class Route(val id: String, val bottomBar: Boolean, val title: String) {
    DASHBOARD("dashboard", true, "Dashboard"),
    LOGINPAGE("loginpage", false, "Login Page"),
    REGISTERPAGE("registerpage", false, "Register Page"),
    ACCOUNT("account", true, "Account"),
    CHANGE_PASSWORD("changePassword", true, "Change Password"),
    CHARTS("charts", true, "Charts"),
    JUGS("jugs", true, "Jugs"),
    NEWS("news", true, "News Feed");

    companion object {
        fun getCurrentRoute(controller: NavController): Route? {
            val id = controller.currentDestination?.route ?: return null
            return Route.entries.find { it.id == id }!!
        }
    }

    fun open(controller: NavController, repository: StateRepository) {
        controller.popBackStack(controller.currentDestination!!.route!!, inclusive = true)
        controller.navigate(id)
        repository.currentRoute.value = this
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
    repository: StateRepository,
    previewRoute: Route?,
    content: @Composable (Modifier) -> Unit
) {
    val currentRoute = repository.currentRoute.collectAsState().value ?: previewRoute!!
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
                            IconButton(onClick = {
                                Route.DASHBOARD.open(
                                    navController,
                                    repository
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { Route.NEWS.open(navController, repository) }) {
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
                                    item.route.open(navController, repository)
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
fun LoginNavigate(user: Flow<User?>, navigate: () -> Unit) {
    val userValue by user.collectAsState(initial = null)
    if (userValue !== null) navigate()
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
fun Root(viewModel: StateViewModel = viewModel()) {
    val state = viewModel.repository
    val controller = rememberNavController()
    Decorations(navController = controller, state, previewRoute = Route.LOGINPAGE) {
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
    }
    LoginNavigate(user = state.user) {
        Route.DASHBOARD.open(controller, state)
    }
    val pairingState by state.pairingState.collectAsState()
    val mainActivity = MainActivity.get()
    if (pairingState == PairingState.ASK_PASSWORD)
        PasswordDialog {
            state.launch {
                state.enterSending(it)
                mainActivity.disconnect()
            }
        }

}
