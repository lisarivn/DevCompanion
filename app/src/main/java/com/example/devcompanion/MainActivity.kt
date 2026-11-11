package com.example.devcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.devcompanion.core.navigation.DevCompanionApp
import com.example.devcompanion.core.ui.theme.AppTheme
import com.example.devcompanion.core.ui.theme.DevCompanionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val preferences by lazy {
        getSharedPreferences("devcompanion_settings", MODE_PRIVATE)
    }

    private var selectedTheme by mutableStateOf(AppTheme.SYSTEM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val savedTheme = preferences.getString(
            "app_theme",
            AppTheme.SYSTEM.name
        )

        selectedTheme = AppTheme.entries.firstOrNull {
            it.name == savedTheme
        } ?: AppTheme.SYSTEM

        setContent {
            DevCompanionTheme(appTheme = selectedTheme) {
                DevCompanionApp(
                    selectedTheme = selectedTheme,
                    onThemeChange = { theme ->
                        selectedTheme = theme

                        preferences.edit()
                            .putString("app_theme", theme.name)
                            .apply()
                    }
                )
            }
        }
    }
}
