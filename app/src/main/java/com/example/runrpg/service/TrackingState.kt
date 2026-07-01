package com.example.runrpg.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TrackingStatus { IDLE, RUNNING, PAUSED, FINISHED }

data class TrackingSnapshot(
    val status: TrackingStatus = TrackingStatus.IDLE,
    val distanceMeters: Double = 0.0,
    val elapsedSeconds: Long = 0L,
    val currentSpeedMps: Double = 0.0
)

/**
 * پل ارتباطی بین LocationTrackingService (که در پس‌زمینه اجرا می‌شود) و لایه UI/ViewModel.
 * چون سرویس Foreground است و ممکن است از اکتیویتی جدا زنده بماند، این Singleton
 * ساده‌ترین راه برای انتشار وضعیت لحظه‌ای بدون نیاز به Bind پیچیده است.
 */
object TrackingState {
    private val _snapshot = MutableStateFlow(TrackingSnapshot())
    val snapshot = _snapshot.asStateFlow()

    fun update(transform: (TrackingSnapshot) -> TrackingSnapshot) {
        _snapshot.value = transform(_snapshot.value)
    }

    fun reset() {
        _snapshot.value = TrackingSnapshot()
    }
}
