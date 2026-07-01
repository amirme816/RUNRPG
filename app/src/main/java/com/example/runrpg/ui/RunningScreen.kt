package com.example.runrpg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runrpg.service.TrackingSnapshot
import com.example.runrpg.service.TrackingStatus
import com.example.runrpg.ui.theme.AccentGreen
import com.example.runrpg.ui.theme.AccentOrange
import com.example.runrpg.ui.theme.DangerRed
import com.example.runrpg.ui.theme.TextSecondary

@Composable
fun RunningScreen(
    snapshot: TrackingSnapshot,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%.2f".format(snapshot.distanceMeters / 1000.0),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Text("کیلومتر", color = TextSecondary, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            MetricBlock(label = "زمان", value = formatDuration(snapshot.elapsedSeconds))
            MetricBlock(label = "سرعت لحظه‌ای", value = "%.1f km/h".format(snapshot.currentSpeedMps * 3.6))
        }

        Spacer(modifier = Modifier.height(48.dp))

        when (snapshot.status) {
            TrackingStatus.RUNNING -> {
                Button(
                    onClick = onPause,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("توقف موقت (Pause)", fontWeight = FontWeight.Bold) }
            }
            TrackingStatus.PAUSED -> {
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("ادامه (Resume)", fontWeight = FontWeight.Bold) }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onFinish,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
            modifier = Modifier.fillMaxWidth()
        ) { Text("پایان دویدن") }
    }
}

@Composable
private fun MetricBlock(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}
