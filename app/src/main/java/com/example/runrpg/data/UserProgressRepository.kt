package com.example.runrpg.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_progress")

data class UserProgress(
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100,
    val streakDays: Int = 0,
    val lastRunDayBucket: Long = -1L,
    val unlockedBadges: Set<String> = emptySet(),
    val weightKg: Float = 70f
)

/**
 * مسئول نگهداری وضعیت گیمیفیکیشن کاربر (سطح، XP، Streak، Badges) به صورت پایدار.
 */
class UserProgressRepository(private val context: Context) {

    private object Keys {
        val LEVEL = intPreferencesKey("level")
        val XP = intPreferencesKey("xp")
        val XP_TO_NEXT = intPreferencesKey("xp_to_next")
        val STREAK = intPreferencesKey("streak")
        val LAST_RUN_DAY = longPreferencesKey("last_run_day")
        val BADGES = stringSetPreferencesKey("badges")
        val WEIGHT = floatPreferencesKey("weight_kg")
    }

    val progressFlow: Flow<UserProgress> = context.dataStore.data.map { prefs ->
        UserProgress(
            level = prefs[Keys.LEVEL] ?: 1,
            currentXp = prefs[Keys.XP] ?: 0,
            xpToNextLevel = prefs[Keys.XP_TO_NEXT] ?: 100,
            streakDays = prefs[Keys.STREAK] ?: 0,
            lastRunDayBucket = prefs[Keys.LAST_RUN_DAY] ?: -1L,
            unlockedBadges = prefs[Keys.BADGES] ?: emptySet(),
            weightKg = prefs[Keys.WEIGHT] ?: 70f
        )
    }

    suspend fun setWeight(weightKg: Float) {
        context.dataStore.edit { it[Keys.WEIGHT] = weightKg }
    }

    suspend fun getCurrent(): UserProgress = progressFlow.first()

    /**
     * پس از پایان یک دویدن، XP اضافه می‌شود و در صورت پر شدن نوار تجربه، سطح بالا می‌رود.
     * همچنین Streak روزهای متوالی و نشان‌های جدید به‌روزرسانی می‌شوند.
     * خروجی: لیست نشان‌های تازه باز شده در این جلسه (برای نمایش به کاربر).
     */
    suspend fun applyRunResult(
        xpEarned: Int,
        distanceMeters: Double,
        runDayBucket: Long,
        totalRunCount: Int
    ): List<String> {
        val newlyUnlocked = mutableListOf<String>()

        context.dataStore.edit { prefs ->
            var level = prefs[Keys.LEVEL] ?: 1
            var xp = (prefs[Keys.XP] ?: 0) + xpEarned
            var xpToNext = prefs[Keys.XP_TO_NEXT] ?: 100

            // بالا رفتن سطح تا زمانی که XP باقی‌مانده کمتر از آستانه بعدی باشد
            while (xp >= xpToNext) {
                xp -= xpToNext
                level += 1
                xpToNext = (xpToNext * 1.2).toInt() // هر سطح کمی سخت‌تر می‌شود
            }

            // محاسبه Streak: اگر دویدن امروز یا دقیقاً روز بعد از آخرین دویدن باشد
            val lastDay = prefs[Keys.LAST_RUN_DAY] ?: -1L
            var streak = prefs[Keys.STREAK] ?: 0
            streak = when {
                lastDay == runDayBucket -> streak // همان روز، تغییری نمی‌کند
                lastDay == runDayBucket - 1 -> streak + 1 // روز متوالی
                else -> 1 // زنجیره شکسته شد، از نو شروع می‌شود
            }

            val badges = (prefs[Keys.BADGES] ?: emptySet()).toMutableSet()

            if (distanceMeters >= 5000.0 && "FIRST_5K" !in badges) {
                badges += "FIRST_5K"
                newlyUnlocked += "FIRST_5K"
            }
            if (streak >= 3 && "STREAK_3" !in badges) {
                badges += "STREAK_3"
                newlyUnlocked += "STREAK_3"
            }
            if (streak >= 7 && "STREAK_7" !in badges) {
                badges += "STREAK_7"
                newlyUnlocked += "STREAK_7"
            }
            if (totalRunCount + 1 == 1 && "FIRST_RUN" !in badges) {
                badges += "FIRST_RUN"
                newlyUnlocked += "FIRST_RUN"
            }

            prefs[Keys.LEVEL] = level
            prefs[Keys.XP] = xp
            prefs[Keys.XP_TO_NEXT] = xpToNext
            prefs[Keys.STREAK] = streak
            prefs[Keys.LAST_RUN_DAY] = runDayBucket
            prefs[Keys.BADGES] = badges
        }

        return newlyUnlocked
    }
}
