package com.example.runrpg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runrpg.model.Scenario
import com.example.runrpg.ui.theme.AccentGreen
import com.example.runrpg.ui.theme.TextSecondary

/**
 * صفحه‌ای مشترک برای نمایش متن شروع یا پایان داستان.
 * @param isIntro اگر true باشد متن شروع، در غیر این صورت متن پایان نمایش داده می‌شود.
 */
@Composable
fun StoryScreen(
    scenario: Scenario,
    isIntro: Boolean,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = scenario.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGreen,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isIntro) scenario.introText else scenario.outroText,
            fontSize = 17.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isIntro) "شروع دویدن" else "پایان ماجراجویی",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}
