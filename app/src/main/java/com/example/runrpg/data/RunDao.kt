package com.example.runrpg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert
    suspend fun insertRun(run: RunEntity): Long

    @Query("SELECT * FROM runs ORDER BY startTimeMillis DESC")
    fun getAllRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY startTimeMillis DESC LIMIT 1")
    fun getLastRun(): Flow<RunEntity?>

    @Query("SELECT COUNT(*) FROM runs")
    suspend fun getRunCount(): Int

    @Query("SELECT MAX(distanceMeters) FROM runs")
    suspend fun getMaxDistance(): Double?

    // برای محاسبه Streak: تاریخ (به شکل روز-ماه-سال به میلی‌ثانیه شروع روز) هر دویدن
    @Query("SELECT DISTINCT (startTimeMillis / 86400000) as dayBucket FROM runs ORDER BY dayBucket DESC")
    suspend fun getDistinctRunDayBuckets(): List<Long>
}
