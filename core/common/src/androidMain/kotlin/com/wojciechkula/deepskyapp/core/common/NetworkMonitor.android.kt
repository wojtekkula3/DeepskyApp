package com.wojciechkula.deepskyapp.core.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidNetworkMonitor(private val context: Context) : NetworkMonitor {
    override val isConnected: Flow<Boolean> = callbackFlow {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // onAvailable/onLost are per-network, not a global connectivity signal: losing Wi-Fi while
        // cellular is still up delivers onLost. Track the available networks and report connectivity
        // as "at least one of them is left". Accessed only from the callback thread.
        val availableNetworks = mutableSetOf<Network>()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                availableNetworks += network
                trySend(true)
            }

            override fun onLost(network: Network) {
                availableNetworks -= network
                trySend(availableNetworks.isNotEmpty())
            }
        }

        manager.activeNetwork
            ?.takeIf { network ->
                manager.getNetworkCapabilities(network)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            }
            ?.let { availableNetworks += it }
        trySend(availableNetworks.isNotEmpty())

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        manager.registerNetworkCallback(request, callback)
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
