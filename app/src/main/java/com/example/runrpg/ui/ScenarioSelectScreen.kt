package com.example.runrpg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runrpg.model.ScenarioType
import com.example.runrpg.model.Scenarios
import com.example.runrpg.ui.theme.SurfaceDark
import com.example.runrpg.ui.theme.TextSecondary

@Composable
fun ScenarioSelectScreen(onScenarioChosen: (ScenarioType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "یک ماجراجویی را انتخاب کن",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "پیش از شروع دویدن، سناریوی داستانی خود را انتخاب کنید",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 24.dp)
        )

        ScenarioCard(
            emoji = "🧟",
            title = Scenarios.ZOMBIE_ESCAPE.title,
            subtitle = "فرار از موج زامبی‌ها تا پناهگاه امن",
            onClick = { onScenarioChosen(ScenarioType.ZOMBIE) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScenarioCard(
            emoji = "🚀",
            title = Scenarios.MARS_MISSION.title,
            subtitle = "دویدن به سمت ایستگاه مرکزی مریخ قبل از اتمام اکسیژن",
            onClick = { onScenarioChosen(ScenarioType.MARS) }
        )
    }
}

@Composable
private fun ScenarioCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 40.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}
