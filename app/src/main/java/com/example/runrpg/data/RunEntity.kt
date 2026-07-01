package com.example.runrpg.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * نمایانگر یک جلسه دویدن ثبت‌شده در تاریخچه کاربر.
 */
@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val startTimeMillis: Long,
    val endTimeMillis: Long,

    // مسافت به متر (منبع حقیقت اصلی)
    val distanceMeters: Double,

    // مدت زمان خالص فعالیت (بدون احتساب زمان‌های پاز) بر حسب ثانیه
    val durationSeconds: Long,

    // سرعت میانگین بر حسب متر بر ثانیه -> در UI به کیلومتر بر ساعت تبدیل می‌شود
    val avgSpeedMps: Double,

    val caloriesBurned: Double,

    // امتیاز تجربه‌ای که از این دویدن کسب شده است
    val xpEarned: Int,

    // شناسه سناریوی داستانی انتخاب‌شده: ZOMBIE یا MARS
    val scenario: String,

    // کیفیت غنیمت (Loot) کسب‌شده بر اساس سرعت: COMMON, RARE, EPIC, LEGENDARY
    val lootTier: String
) {
    val distanceKm: Double get() = distanceMeters / 1000.0
    val avgSpeedKmh: Double get() = avgSpeedMps * 3.6
}
