package com.example.runrpg.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrpg.data.AppDatabase
import com.example.runrpg.data.RunEntity
import com.example.runrpg.data.UserProgress
import com.example.runrpg.data.UserProgressRepository
import com.example.runrpg.gamification.GamificationEngine
import com.example.runrpg.model.Badge
import com.example.runrpg.model.Badges
import com.example.runrpg.model.Scenario
import com.example.runrpg.model.ScenarioType
import com.example.runrpg.model.Scenarios
import com.example.runrpg.service.LocationTrackingService
import com.example.runrpg.service.TrackingSnapshot
import com.example.runrpg.service.TrackingState
import com.example.runrpg.service.TrackingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** نتیجه نهایی یک جلسه دویدن، آماده برای نمایش در صفحه خلاصه. */
data class RunSummary(
    val run: RunEntity,
    val newlyUnlockedBadges: List<Badge>
)

class RunViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val runDao = db.runDao()
    private val progressRepository = UserProgressRepository(application)

    val trackingSnapshot: StateFlow<TrackingSnapshot> = TrackingState.snapshot

    val userProgress: StateFlow<UserProgress> = progressRepository.progressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress())

    val lastRun: StateFlow<RunEntity?> = runDao.getLastRun()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val history: StateFlow<List<RunEntity>> = runDao.getAllRuns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedScenario = MutableStateFlow<Scenario?>(null)
    val selectedScenario = _selectedScenario.asStateFlow()

    private val _lastSummary = MutableStateFlow<RunSummary?>(null)
    val lastSummary = _lastSummary.asStateFlow()

    fun selectScenario(type: ScenarioType) {
        _selectedScenario.value = Scenarios.byType(type)
    }

    fun setWeight(weightKg: Float) {
        viewModelScope.launch { progressRepository.setWeight(weightKg) }
    }

    /** پس از نمایش متن شروع داستان، دویدن واقعی و سرویس ردیابی آغاز می‌شود. */
    fun startTracking() {
        val context = getApplication<Application>()
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    fun pauseTracking() {
        sendServiceAction(LocationTrackingService.ACTION_PAUSE)
    }

    fun resumeTracking() {
        sendServiceAction(LocationTrackingService.ACTION_RESUME)
    }

    /** دویدن را متوقف می‌کند، محاسبات نهایی گیمیفیکیشن را انجام داده و در دیتابیس ذخیره می‌کند. */
    fun finishTracking() {
        sendServiceAction(LocationTrackingService.ACTION_STOP)

        val snapshot = trackingSnapshot.value
        val scenario = _selectedScenario.value ?: Scenarios.ZOMBIE_ESCAPE

        viewModelScope.launch {
            val progress = progressRepository.getCurrent()
            val avgSpeed = GamificationEngine.averageSpeedMps(snapshot.distanceMeters, snapshot.elapsedSeconds)
            val calories = GamificationEngine.estimateCalories(progress.weightKg, snapshot.elapsedSeconds)
            val xp = GamificationEngine.distanceToXp(snapshot.distanceMeters)
            val lootTier = GamificationEngine.speedToLootTier(avgSpeed)

            val now = System.currentTimeMillis()
            val dayBucket = now / 86_400_000L
            val runCount = runDao.getRunCount()

            val run = RunEntity(
                startTimeMillis = now - snapshot.elapsedSeconds * 1000,
                endTimeMillis = now,
                distanceMeters = snapshot.distanceMeters,
                durationSeconds = snapshot.elapsedSeconds,
                avgSpeedMps = avgSpeed,
                caloriesBurned = calories,
                xpEarned = xp,
                scenario = scenario.type.name,
                lootTier = lootTier.name
            )
            runDao.insertRun(run)

            val unlockedIds = progressRepository.applyRunResult(
                xpEarned = xp,
                distanceMeters = snapshot.distanceMeters,
                runDayBucket = dayBucket,
                totalRunCount = runCount
            )

            _lastSummary.value = RunSummary(
                run = run,
                newlyUnlockedBadges = unlockedIds.mapNotNull { Badges.byId(it) }
            )
        }
    }

    fun clearSummary() {
        _lastSummary.value = null
        _selectedScenario.value = null
    }

    private fun sendServiceAction(action: String) {
        val context = getApplication<Application>()
        val intent = Intent(context, LocationTrackingService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }
}
