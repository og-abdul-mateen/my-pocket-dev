package com.apptivelabs.mypocketdev.data.repository

import com.apptivelabs.mypocketdev.data.api.ClaudeApiService
import com.apptivelabs.mypocketdev.data.model.CodeAnalysisResponse

class CodeAnalysisRepository(private val apiService: ClaudeApiService) {

    suspend fun analyzeCode(code: String, language: String): Result<CodeAnalysisResponse> {
        if (code.isBlank()) {
            return Result.failure(IllegalArgumentException("Code snippet cannot be empty"))
        }
        if (code.length > 10_000) {
            return Result.failure(IllegalArgumentException("Code snippet too large (max 10,000 chars)"))
        }
        return apiService.analyzeCode(code, language)
    }
}
