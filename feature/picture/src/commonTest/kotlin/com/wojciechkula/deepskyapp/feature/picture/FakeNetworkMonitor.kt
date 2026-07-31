package com.wojciechkula.deepskyapp.feature.picture

import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor(initial: Boolean = true) : NetworkMonitor {
    val connected = MutableStateFlow(initial)
    override val isConnected: Flow<Boolean> = connected
}
