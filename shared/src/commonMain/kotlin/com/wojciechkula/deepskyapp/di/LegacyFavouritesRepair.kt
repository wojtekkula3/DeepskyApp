package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.core.common.Logger
import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.core.common.logDescription
import com.wojciechkula.deepskyapp.domain.interactor.RepairLegacyFavouritesInteractor
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

private const val TAG = "LegacyFavouritesRepair"

// Called once per process from each platform's entry point, after initKoin, rather than per Favourites visit,
// so a day the new API cannot serve is not re-requested on every tab switch. Room and Ktor are main-safe, so
// the main scope only schedules the work.
fun startLegacyFavouritesRepair() {
    val koin = KoinPlatform.getKoin()
    MainScope().repairLegacyFavouritesOnceOnline(koin.get(), koin.get(), koin.get())
}

internal fun CoroutineScope.repairLegacyFavouritesOnceOnline(
    networkMonitor: NetworkMonitor,
    repairLegacyFavourites: RepairLegacyFavouritesInteractor,
    logger: Logger
): Job = launch {
    networkMonitor.isConnected.first { it }
    try {
        repairLegacyFavourites()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
        logger.e(TAG, "Legacy favourites repair failed: ${exception.logDescription()}")
    }
}
