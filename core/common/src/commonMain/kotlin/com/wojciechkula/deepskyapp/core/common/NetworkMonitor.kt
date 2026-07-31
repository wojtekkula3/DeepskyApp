package com.wojciechkula.deepskyapp.core.common

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isConnected: Flow<Boolean>
}
