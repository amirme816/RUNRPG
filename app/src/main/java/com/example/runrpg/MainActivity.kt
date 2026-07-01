package com.example.runrpg

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.runrpg.ui.AppNavigation
import com.example.runrpg.ui.theme.RunRPGTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* نتیجه مجوزها؛ در صورت رد شدن، ردیابی موقعیت مکانی کار نخواهد کرد */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        setContent {
            RunRPGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        // مجوز ACCESS_BACKGROUND_LOCATION باید طبق سیاست گوگل پلی به‌صورت جداگانه
        // و پس از اعطای مجوز موقعیت مکانی پیش‌زمینه درخواست شود.
        permissionLauncher.launch(permissions.toTypedArray())
    }
}
