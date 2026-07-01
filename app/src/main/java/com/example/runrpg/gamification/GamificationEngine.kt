package com.example.runrpg.gamification

/**
 * تمام فرمول‌های تبدیل داده خام دویدن به مقادیر بازی و سلامتی در این کلاس قرار دارند.
 */
object GamificationEngine {

    /** ضریب متوسط کالری‌سوزی هنگام دویدن (MET تقریبی برای دویدن با شدت متوسط). */
    private const val RUNNING_MET = 8.0

    /**
     * سرعت میانگین = مسافت (متر) / زمان (ثانیه) -> نتیجه بر حسب متر بر ثانیه.
     */
    fun averageSpeedMps(distanceMeters: Double, durationSeconds: Long): Double {
        if (durationSeconds <= 0) return 0.0
        return distanceMeters / durationSeconds
    }

    /**
     * تخمین کالری‌سوزی بر اساس فرمول استاندارد MET:
     * کالری = MET × وزن(kg) × زمان(ساعت)
     */
    fun estimateCalories(weightKg: Float, durationSeconds: Long): Double {
        val hours = durationSeconds / 3600.0
        return RUNNING_MET * weightKg * hours
    }

    /**
     * تبدیل مسافت به امتیاز تجربه: هر ۱۰۰ متر معادل ۱۰ امتیاز XP.
     */
    fun distanceToXp(distanceMeters: Double): Int {
        return ((distanceMeters / 100.0) * 10).toInt()
    }

    /**
     * تبدیل سرعت میانگین به کیفیت غنیمت (Loot).
     * هرچه سرعت بالاتر، شانس دریافت غنیمت کمیاب‌تر بیشتر است.
     */
    fun speedToLootTier(avgSpeedMps: Double): LootTier {
        val kmh = avgSpeedMps * 3.6
        return when {
            kmh >= 13.0 -> LootTier.LEGENDARY
            kmh >= 10.0 -> LootTier.EPIC
            kmh >= 7.0 -> LootTier.RARE
            else -> LootTier.COMMON
        }
    }
}

enum class LootTier(val displayName: String, val emoji: String) {
    COMMON("معمولی", "⚪"),
    RARE("کمیاب", "🔵"),
    EPIC("حماسی", "🟣"),
    LEGENDARY("افسانه‌ای", "🟡")
}
