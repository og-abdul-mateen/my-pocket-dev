package com.apptivelabs.mypocketdev.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptivelabs.mypocketdev.data.model.CodeAnalysisResponse
import com.apptivelabs.mypocketdev.data.repository.CodeAnalysisRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalysisViewModel(
    private val repository: CodeAnalysisRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun onLanguageSelected(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun onTabSelected(tab: ResultTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun analyzeCode() {
        val state = _uiState.value
        if (state.code.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(analysisState = AnalysisState.Loading) }

            repository.analyzeCode(state.code, state.selectedLanguage)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(analysisState = AnalysisState.Success(response))
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            analysisState = AnalysisState.Error(
                                error.message ?: "Analysis failed"
                            )
                        )
                    }
                }
        }
    }

    fun clearResults() {
        _uiState.update {
            it.copy(
                code = "",
                analysisState = AnalysisState.Idle,
                activeTab = ResultTab.DESIGN_FLAWS
            )
        }
    }
}

data class AnalysisUiState(
    val code: String = "",
    val selectedLanguage: String = "Kotlin",
    val activeTab: ResultTab = ResultTab.DESIGN_FLAWS,
    val analysisState: AnalysisState = AnalysisState.Idle
)

enum class ResultTab(val label: String) {
    DESIGN_FLAWS("Design Flaws"),
    REFACTORING("Refactoring"),
    TESTS("Tests")
}

sealed interface AnalysisState {
    data object Idle : AnalysisState
    data object Loading : AnalysisState
    data class Success(val response: CodeAnalysisResponse) : AnalysisState
    data class Error(val message: String) : AnalysisState
}
