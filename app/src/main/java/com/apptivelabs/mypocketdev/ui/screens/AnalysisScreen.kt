package com.apptivelabs.mypocketdev.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptivelabs.mypocketdev.data.model.*
import com.apptivelabs.mypocketdev.ui.theme.*

val SUPPORTED_LANGUAGES = listOf("Kotlin", "Java", "Python", "JavaScript", "TypeScript", "Swift", "Go", "Rust")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "My Pocket Dev",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Your Dev Partner",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language selector chips
            LanguageSelector(
                selected = uiState.selectedLanguage,
                onSelected = viewModel::onLanguageSelected
            )

            // Code input
            CodeInputField(
                code = uiState.code,
                onCodeChanged = viewModel::onCodeChanged,
                language = uiState.selectedLanguage
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = viewModel::analyzeCode,
                    modifier = Modifier.weight(1f),
                    enabled = uiState.code.isNotBlank() &&
                            uiState.analysisState !is AnalysisState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Emerald
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.analysisState is AnalysisState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Analyzing...")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Analyze Code")
                    }
                }

                if (uiState.analysisState is AnalysisState.Success) {
                    OutlinedButton(
                        onClick = viewModel::clearResults,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear")
                    }
                }
            }

            // Results section
            AnimatedVisibility(
                visible = uiState.analysisState is AnalysisState.Success ||
                        uiState.analysisState is AnalysisState.Error,
                enter = fadeIn() + slideInVertically()
            ) {
                when (val state = uiState.analysisState) {
                    is AnalysisState.Success -> ResultsSection(
                        response = state.response,
                        activeTab = uiState.activeTab,
                        onTabSelected = viewModel::onTabSelected
                    )
                    is AnalysisState.Error -> ErrorCard(message = state.message)
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun LanguageSelector(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SUPPORTED_LANGUAGES.forEach { lang ->
            FilterChip(
                selected = lang == selected,
                onClick = { onSelected(lang) },
                label = { Text(lang, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Emerald.copy(alpha = 0.2f),
                    selectedLabelColor = Emerald
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun CodeInputField(code: String, onCodeChanged: (String) -> Unit, language: String) {
    OutlinedTextField(
        value = code,
        onValueChange = onCodeChanged,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 300.dp),
        placeholder = {
            Text(
                "Paste your $language code here...",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace
            )
        },
        textStyle = LocalTextStyle.current.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 20.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardBg,
            unfocusedContainerColor = CardBg,
            focusedBorderColor = Emerald,
            unfocusedBorderColor = SurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ResultsSection(
    response: CodeAnalysisResponse,
    activeTab: ResultTab,
    onTabSelected: (ResultTab) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Summary card
        Card(
            colors = CardDefaults.cardColors(containerColor = EmeraldDark.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = response.summary,
                modifier = Modifier.padding(16.dp),
                color = EmeraldLight,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        // Tab row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResultTab.entries.forEach { tab ->
                val isActive = tab == activeTab
                val count = when (tab) {
                    ResultTab.DESIGN_FLAWS -> response.designFlaws.size
                    ResultTab.REFACTORING -> response.refactoringSuggestions.size
                    ResultTab.TESTS -> response.proposedTests.size
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) Emerald.copy(alpha = 0.15f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isActive) Emerald else SurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$count",
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Emerald else TextSecondary,
                            fontSize = 18.sp
                        )
                        Text(
                            tab.label,
                            fontSize = 11.sp,
                            color = if (isActive) TextPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        // Tab content
        when (activeTab) {
            ResultTab.DESIGN_FLAWS -> DesignFlawsList(response.designFlaws)
            ResultTab.REFACTORING -> RefactoringList(response.refactoringSuggestions)
            ResultTab.TESTS -> TestsList(response.proposedTests)
        }
    }
}

@Composable
fun DesignFlawsList(flaws: List<AnalysisItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        flaws.forEach { flaw ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeverityBadge(flaw.severity)
                        Text(flaw.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(flaw.description, color = TextSecondary, fontSize = 13.sp)
                    flaw.lineRange?.let {
                        Text(it, color = Emerald, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun SeverityBadge(severity: Severity) {
    val color = when (severity) {
        Severity.CRITICAL -> SeverityCritical
        Severity.HIGH -> SeverityHigh
        Severity.MEDIUM -> SeverityMedium
        Severity.LOW -> SeverityLow
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = severity.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RefactoringList(suggestions: List<RefactoringItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        suggestions.forEach { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(item.rationale, color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    CodeDiffBlock(label = "Before", code = item.before, color = SeverityHigh)
                    Spacer(Modifier.height(6.dp))
                    CodeDiffBlock(label = "After", code = item.after, color = SeverityLow)
                }
            }
        }
    }
}

@Composable
fun CodeDiffBlock(label: String, code: String, color: Color) {
    Column {
        Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Surface(
            color = Surface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
            Text(
                text = code,
                modifier = Modifier.padding(10.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun TestsList(tests: List<TestItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tests.forEach { test ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TestTypeBadge(test.testType)
                        Text(test.testName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(test.description, color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = Surface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = test.pseudoCode,
                            modifier = Modifier.padding(10.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TestTypeBadge(type: TestType) {
    val color = when (type) {
        TestType.UNIT -> Emerald
        TestType.INTEGRATION -> Color(0xFF42A5F5)
        TestType.UI -> Color(0xFFAB47BC)
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = type.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SeverityCritical.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = SeverityCritical
        )
    }
}
