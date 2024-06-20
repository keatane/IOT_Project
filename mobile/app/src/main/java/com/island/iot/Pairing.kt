@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.island.iot

import android.app.Activity.RESULT_OK
import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.WifiDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiNetworkSpecifier
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.regex.Pattern
import kotlin.coroutines.resume

interface Pairing {
    suspend fun selectJug(): String?
    suspend fun connectToJug(ssid: String): Boolean
    suspend fun selectWifi(): String?
    suspend fun disconnect()
}

val FAKE_PAIRING = PairingFake()

class PairingFake : Pairing {
    override suspend fun selectJug(): String? {
        throw NotImplementedError()
    }

    override suspend fun connectToJug(ssid: String): Boolean {
        throw NotImplementedError()
    }

    override suspend fun selectWifi(): String? {
        throw NotImplementedError()
    }

    override suspend fun disconnect() {
        throw NotImplementedError()
    }
}

class PairingImpl(activity: ActivityResultCaller) : Pairing {
    private var wifiCallback: ((String?) -> Unit)? = null
    private val wifiSelection =
        activity.registerForActivityResult(WifiChooserContract()) {
            wifiCallback!!(it)
        }
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var deviceManager: CompanionDeviceManager
    private val jugWifiFilter: WifiDeviceFilter =
        WifiDeviceFilter.Builder().setNamePattern(Pattern.compile("jug_\\d+")).build()
    private val jugRequest = AssociationRequest.Builder().addDeviceFilter(jugWifiFilter).build()
    private val wifiRequest = AssociationRequest.Builder().build()
    private lateinit var networkCallback: NetworkCallback

    private class WifiChooserContract : ActivityResultContract<IntentSenderRequest, String?>() {
        private val contract = ActivityResultContracts.StartIntentSenderForResult()

        override fun createIntent(context: Context, input: IntentSenderRequest): Intent {
            return contract.createIntent(context, input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val activityResult = contract.parseResult(resultCode, intent)
            return when (activityResult.resultCode) {
                RESULT_OK -> {
                    // The user chose to pair the app with a Bluetooth device.
                    val deviceToPair: ScanResult =
                        activityResult.data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)!!
                    deviceToPair.SSID
                }

                else -> {
                    null
                }
            }
        }
    }

    fun onCreate(context: Context) {
        connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        deviceManager = context.getSystemService(CompanionDeviceManager::class.java)
    }

    override suspend fun selectJug(): String? {
        val intentSender = wifiScan(jugRequest)
        return selectWifi(intentSender)
    }

    override suspend fun connectToJug(ssid: String): Boolean {
        return suspendCancellableCoroutine { cont ->
            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssid)
                .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier)
                .build()

            networkCallback = object :
                NetworkCallback() {
                override fun onAvailable(network: Network) {
                    connectivityManager.bindProcessToNetwork(network)
                    cont.resume(true)
                }

                override fun onUnavailable() {
                    cont.resume(false)
                }
            }

            connectivityManager.requestNetwork(networkRequest, networkCallback)
        }
    }

    override suspend fun selectWifi(): String? {
        val intentSenderRequest = wifiScan(wifiRequest)
        return selectWifi(intentSenderRequest)
    }

    override suspend fun disconnect() {
        connectivityManager.bindProcessToNetwork(null)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private suspend fun selectWifi(intentSenderRequest: IntentSenderRequest): String? {
        return suspendCancellableCoroutine { cont ->
            wifiCallback = { cont.resume(it) }
            wifiSelection.launch(intentSenderRequest)
        }
    }


    private suspend fun wifiScan(
        request: AssociationRequest,
    ): IntentSenderRequest {
        return suspendCancellableCoroutine { cont ->
            deviceManager.associate(
                request,
                object : CompanionDeviceManager.Callback() {
                    // Called when a device is found. Launch the IntentSender so the user
                    // can select the device they want to pair with.
                    override fun onDeviceFound(chooserLauncher: IntentSender) {
                        cont.resume(IntentSenderRequest.Builder(chooserLauncher).build())
                    }

                    override fun onFailure(error: CharSequence?) {
                        // Handle the failure.
                    }
                }, null
            )
        }
    }
}
