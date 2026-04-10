package com.apptivelabs.mypocketdev.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CodeAnalysisResponse(
    val designFlaws: List<AnalysisItem>,
    val refactoringSuggestions: List<RefactoringItem>,
    val proposedTests: List<TestItem>,
    val summary: String
)

@Serializable
data class AnalysisItem(
    val severity: Severity,
    val title: String,
    val description: String,
    val lineRange: String? = null
)

@Serializable
enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

@Serializable
data class RefactoringItem(
    val title: String,
    val rationale: String,
    val before: String,
    val after: String
)

@Serializable
data class TestItem(
    val testName: String,
    val testType: TestType,
    val description: String,
    val pseudoCode: String
)

@Serializable
enum class TestType { UNIT, INTEGRATION, UI }
