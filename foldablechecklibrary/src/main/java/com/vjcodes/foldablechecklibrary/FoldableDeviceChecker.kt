package com.vjcodes.foldablechecklibrary

import android.app.Activity
import android.graphics.Rect
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class FoldableDeviceChecker(private val activity: Activity, private val callback: (Boolean) -> Unit) {

    /**
     * Observes the folding state of the device
     * @return Flow of FoldableState
     */
    @JvmOverloads
    fun observeFoldState(): Flow<FoldableState> {
        return WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity)
            .map {
                val foldingFeature = it.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()
                when {
                    foldingFeature == null -> FoldableState.FLAT
                    foldingFeature.state == FoldingFeature.State.HALF_OPENED -> FoldableState.HALF_FOLDED
                    foldingFeature.state == FoldingFeature.State.FLAT -> FoldableState.FLAT
                    else -> FoldableState.FOLDED
                }
            }
    }

    /**
     * Gets the current folding state
     * @return Current FoldableState
     */
    suspend fun getCurrentFoldState(): FoldableState {
        return WindowInfoTracker.getOrCreate(activity)
            .windowLayoutInfo(activity)
            .map {
                val foldingFeature = it.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull()

                when {
                    foldingFeature == null -> FoldableState.FLAT
                    foldingFeature.state == FoldingFeature.State.HALF_OPENED -> FoldableState.HALF_FOLDED
                    foldingFeature.state == FoldingFeature.State.FLAT -> FoldableState.FLAT
                    else -> FoldableState.FOLDED
                }
            }.first()
    }

    /**
     * Checks if the device is a foldable device
     * @return Boolean indicating if the device has folding capabilities
     */
    @JvmOverloads
    suspend fun isFoldableDevice(): Boolean {
        return try {
            WindowInfoTracker.getOrCreate(activity)
                .windowLayoutInfo(activity)
                .map {
                    it.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .isNotEmpty()
                }.first()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Java-friendly method to check if device is foldable
     * @return Boolean indicating if the device has folding capabilities
     */
    @JvmOverloads
    fun isFoldableDeviceBlocking(
        timeoutMillis: Long = 5000L
    ): Boolean = runBlocking {
        try {
            withTimeout(timeoutMillis) {
                WindowInfoTracker.getOrCreate(activity)
                    .windowLayoutInfo(activity)
                    .map {
                        it.displayFeatures
                            .filterIsInstance<FoldingFeature>()
                            .isNotEmpty()
                    }
                    .first()
            }
        } catch (e: TimeoutCancellationException) {
            false
        }
    }

    /**
     * Java-friendly method to get folding area
     * @return Rect representing the folding area, or null if no folding feature exists
     */
    @JvmOverloads
    fun getFoldingAreaBlocking(
        timeoutMillis: Long = 5000L
    ): Rect? = runBlocking {
        try {
            withTimeout(timeoutMillis) {
                WindowInfoTracker.getOrCreate(activity)
                    .windowLayoutInfo(activity)
                    .map {
                        it.displayFeatures
                            .filterIsInstance<FoldingFeature>()
                            .firstOrNull()?.bounds
                    }
                    .first()
            }
        } catch (e: TimeoutCancellationException) {
            null
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    fun startListening() {
        scope.launch {
            WindowInfoTracker.getOrCreate(activity)
                .windowLayoutInfo(activity)
                .collect { layoutInfo ->
                    val isFolded = layoutInfo.displayFeatures.any { it is FoldingFeature }
                    callback(isFolded)
                }
        }
    }

    fun stopListening() {
        scope.cancel()
    }
}