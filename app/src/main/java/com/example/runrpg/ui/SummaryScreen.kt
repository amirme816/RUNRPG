package com.example.runrpg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runrpg.gamification.LootTier
import com.example.runrpg.viewmodel.RunSummary
import com.example.runrpg.ui.theme.AccentGreen
import com.example.runrpg.ui.theme.SurfaceDark
import com.example.runrpg.ui.theme.TextSecondary

@Composable
fun SummaryScreen(summary: RunSummary, onDone: () -> Unit) {
    val run = summary.run
    val loot = runCatching { LootTier.valueOf(run.lootTier) }.getOrDefault(LootTier.COMMON)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("جلسه دویدن به پایان رسید 🎉", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SummaryRow("مسافت", "%.2f کیلومتر".format(run.distanceKm))
                SummaryRow("زمان", formatDuration(run.durationSeconds))
                SummaryRow("سرعت میانگین", "%.1f km/h".format(run.avgSpeedKmh))
                SummaryRow("کالری‌سوزی", "%.0f kcal".format(run.caloriesBurned))
                SummaryRow("امتیاز XP کسب‌شده", "+${run.xpEarned} XP")
                SummaryRow("غنیمت (Loot)", "${loot.emoji} ${loot.displayName}")
            }
        }

        if (summary.newlyUnlockedBadges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("نشان‌های جدید باز شد!", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            summary.newlyUnlockedBadges.forEach { badge ->
                Text("${badge.emoji} ${badge.title} — ${badge.description}", color = TextSecondary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            modifier = Modifier.fillMaxWidth()
        ) { Text("بازگشت به داشبورد", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
