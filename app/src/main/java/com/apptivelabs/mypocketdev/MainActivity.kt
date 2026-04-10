package com.apptivelabs.mypocketdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.apptivelabs.mypocketdev.data.api.ClaudeApiService
import com.apptivelabs.mypocketdev.data.repository.CodeAnalysisRepository
import com.apptivelabs.mypocketdev.ui.screens.AnalysisScreen
import com.apptivelabs.mypocketdev.ui.screens.AnalysisViewModel
import com.apptivelabs.mypocketdev.ui.theme.MyPocketDevTheme

class MainActivity : ComponentActivity() {

    // Simple manual DI — keeps the demo lean without Hilt boilerplate
    private val apiService by lazy {
        ClaudeApiService(apiKey = BuildConfig.CLAUDE_API_KEY)
    }
    private val repository by lazy {
        CodeAnalysisRepository(apiService)
    }
    private val viewModel by lazy {
        AnalysisViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPocketDevTheme {
                AnalysisScreen(viewModel = viewModel)
            }
        }
    }
}
