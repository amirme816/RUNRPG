package com.example.runrpg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runrpg.data.RunEntity
import com.example.runrpg.data.UserProgress
import com.example.runrpg.ui.theme.AccentGreen
import com.example.runrpg.ui.theme.AccentOrange
import com.example.runrpg.ui.theme.AccentPurple
import com.example.runrpg.ui.theme.SurfaceDark
import com.example.runrpg.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    progress: UserProgress,
    weeklyDistanceKm: Double,
    weeklyRunCount: Int,
    lastRun: RunEntity?,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- بخش بالا: سطح و خلاصه هفتگی ----------
        LevelHeader(progress = progress, weeklyDistanceKm = weeklyDistanceKm, weeklyRunCount = weeklyRunCount)

        Spacer(modifier = Modifier.weight(1f))

        // ---------- بخش مرکز: دکمه بزرگ START ----------
        StartButton(onClick = onStartClick)

        Spacer(modifier = Modifier.weight(1f))

        // ---------- بخش پایین: آخرین رکورد ----------
        LastRunCard(lastRun = lastRun)

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun LevelHeader(progress: UserProgress, weeklyDistanceKm: Double, weeklyRunCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("سطح", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        "Level ${progress.level}",
                        color = AccentGreen,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Streak", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        "🔥 ${progress.streakDays} روز",
                        color = AccentOrange,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // نوار پیشرفت XP تا سطح بعدی
            val ratio = (progress.currentXp.toFloat() / progress.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(SurfaceDark.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(ratio)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(AccentGreen, AccentPurple)))
                )
            }
            Text(
                "${progress.currentXp} / ${progress.xpToNextLevel} XP",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeeklyStat(label = "مسافت هفته", value = "%.1f کیلومتر".format(weeklyDistanceKm))
                WeeklyStat(label = "تعداد دویدن", value = "$weeklyRunCount جلسه")
            }
        }
    }
}

@Composable
private fun WeeklyStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun StartButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(180.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(AccentGreen, AccentGreen.copy(alpha = 0.6f))))
            .then(Modifier),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("START", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LastRunCard(lastRun: RunEntity?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("آخرین رکورد", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            if (lastRun == null) {
                Text("هنوز دویدنی ثبت نشده — اولین دویدنت را شروع کن!")
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LastRunStat("مسافت", "%.2f km".format(lastRun.distanceKm))
                    LastRunStat("زمان", formatDuration(lastRun.durationSeconds))
                    LastRunStat("سرعت", "%.1f km/h".format(lastRun.avgSpeedKmh))
                    LastRunStat("کالری", "%.0f kcal".format(lastRun.caloriesBurned))
                }
            }
        }
    }
}

@Composable
private fun LastRunStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

fun formatDuration(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
