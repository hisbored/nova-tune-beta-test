package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.NovatuneApp
import com.example.ui.theme.NovatuneTheme
import com.example.ui.theme.ObsidianBackground

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val wallpaperSettings by viewModel.wallpaperSettings.collectAsStateWithLifecycle()

            NovatuneTheme(
                themeMode = wallpaperSettings.themeMode,
                useDynamicColors = wallpaperSettings.useDynamicColors,
                activePresetId = wallpaperSettings.selectedPresetId
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBackground
                ) {
                    NovatuneApp(viewModel = viewModel)
                }
            }
        }
    }
}

