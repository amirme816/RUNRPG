package com.example.runrpg.service

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*

/**
 * سرویسی که در پس‌زمینه (حتی وقتی صفحه اپ باز نیست) موقعیت مکانی را با استفاده از
 * FusedLocationProviderClient دنبال می‌کند، مسافت طی‌شده را محاسبه و تایمر فعالیت
 * را با پشتیبانی از Pause/Resume مدیریت می‌کند.
 */
class LocationTrackingService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"

        private const val NOTIF_CHANNEL_ID = "run_tracking_channel"
        private const val NOTIF_ID = 1001

        // حداقل دقتی که یک نقطه GPS برای معتبر بودن باید داشته باشد (متر)
        private const val MIN_ACCURACY_METERS = 20f
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    private var lastLocation: Location? = null
    private var totalDistanceMeters = 0.0

    // زمان‌بندی مبتنی بر لحظات شروع/توقف برای محاسبه دقیق مدت زمان خالص فعالیت
    private var segmentStartElapsedRealtime = 0L
    private var accumulatedSeconds = 0L
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        totalDistanceMeters = 0.0
        accumulatedSeconds = 0
        lastLocation = null
        TrackingState.reset()
        TrackingState.update { it.copy(status = TrackingStatus.RUNNING) }

        startForeground(NOTIF_ID, buildNotification("در حال دویدن..."))
        beginLocationUpdates()
        beginTimerSegment()
    }

    private fun pauseTracking() {
        TrackingState.update { it.copy(status = TrackingStatus.PAUSED) }
        stopTimerSegment()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        updateNotification("فعالیت متوقف شد (Pause)")
    }

    private fun resumeTracking() {
        TrackingState.update { it.copy(status = TrackingStatus.RUNNING) }
        beginLocationUpdates()
        beginTimerSegment()
        updateNotification("در حال دویدن...")
    }

    private fun stopTracking() {
        stopTimerSegment()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        TrackingState.update { it.copy(status = TrackingStatus.FINISHED) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // ---------- GPS ----------

    private fun beginLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                if (location.accuracy > MIN_ACCURACY_METERS) return // نویز GPS نادیده گرفته می‌شود

                val previous = lastLocation
                if (previous != null) {
                    val delta = previous.distanceTo(location)
                    totalDistanceMeters += delta
                }
                lastLocation = location

                TrackingState.update {
                    it.copy(
                        distanceMeters = totalDistanceMeters,
                        currentSpeedMps = if (location.hasSpeed()) location.speed.toDouble() else it.currentSpeedMps
                    )
                }
                updateNotification("مسافت: %.2f کیلومتر".format(totalDistanceMeters / 1000.0))
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback as LocationCallback,
                mainLooper
            )
        } catch (e: SecurityException) {
            // مجوز موقعیت مکانی داده نشده است؛ اکتیویتی مسئول درخواست مجوز پیش از استارت سرویس است.
        }
    }

    // ---------- Timer با قابلیت Pause/Resume ----------

    private fun beginTimerSegment() {
        segmentStartElapsedRealtime = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                val elapsedInSegment = (System.currentTimeMillis() - segmentStartElapsedRealtime) / 1000
                val totalSeconds = accumulatedSeconds + elapsedInSegment
                TrackingState.update { it.copy(elapsedSeconds = totalSeconds) }
            }
        }
    }

    private fun stopTimerSegment() {
        timerJob?.cancel()
        val elapsedInSegment = (System.currentTimeMillis() - segmentStartElapsedRealtime) / 1000
        accumulatedSeconds += elapsedInSegment
    }

    // ---------- نوتیفیکیشن Foreground ----------

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "ردیابی دویدن",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("RunRPG")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = buildNotification(text)
        getSystemService(NotificationManager::class.java).notify(NOTIF_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
