package com.apptivelabs.mypocketdev.data.api

import com.apptivelabs.mypocketdev.data.model.CodeAnalysisResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ClaudeApiService(private val apiKey: String) {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) { json(json) }
    }

    suspend fun analyzeCode(code: String, language: String): Result<CodeAnalysisResponse> {
        return runCatching {
            val response = client.post("https://api.anthropic.com/v1/messages") {
                header("x-api-key", apiKey)
                header("anthropic-version", "2023-06-01")
                contentType(ContentType.Application.Json)
                setBody(
                    ClaudeRequest(
                        model = "claude-sonnet-4-20250514",
                        max_tokens = 2048,
                        system = SYSTEM_PROMPT,
                        messages = listOf(
                            Message(
                                role = "user",
                                content = buildUserPrompt(code, language)
                            )
                        )
                    )
                )
            }
            val claudeResponse = response.body<ClaudeResponse>()

            if (claudeResponse.error != null) {
                throw Exception(claudeResponse.error.message)
            }

            val textContent = claudeResponse.content
                ?.firstOrNull { it.type == "text" && it.text != null }
                ?.text
                ?: throw Exception("No text content in response")

            val cleaned = textContent
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()
            json.decodeFromString<CodeAnalysisResponse>(cleaned)        }
    }

    private fun buildUserPrompt(code: String, language: String): String = """
        Analyze this $language code snippet as a senior pair programmer.
        Respond ONLY with valid JSON, no markdown fences, no preamble.
        
        ```$language
        $code
        ```
    """.trimIndent()

    companion object {
        private val SYSTEM_PROMPT = """
            You are "My Pocket Dev" — a senior AI pair programming engineer.
            You review code for design flaws, suggest refactoring, and propose tests.
            
            ALWAYS respond with ONLY a JSON object (no markdown, no extra text) matching this schema:
            {
              "designFlaws": [
                { "severity": "LOW|MEDIUM|HIGH|CRITICAL", "title": "...", "description": "...", "lineRange": "L5-L12" }
              ],
              "refactoringSuggestions": [
                { "title": "...", "rationale": "...", "before": "code snippet", "after": "refactored snippet" }
              ],
              "proposedTests": [
                { "testName": "...", "testType": "UNIT|INTEGRATION|UI", "description": "...", "pseudoCode": "..." }
              ],
              "summary": "One-line overall assessment"
            }
            
            Rules:
            - Identify 2-4 design flaws ordered by severity (high first)
            - Suggest 2-3 concrete refactorings with before/after code
            - Propose 2-3 tests covering critical paths
            - Be specific, cite line numbers when possible
            - Keep the summary punchy and actionable
        """.trimIndent()
    }
}

@Serializable
data class ClaudeRequest(
    val model: String,
    val max_tokens: Int,
    val system: String,
    val messages: List<Message>
)

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class ClaudeResponse(
    val content: List<ContentBlock>? = null,
    val error: ClaudeError? = null
)

@Serializable
data class ContentBlock(
    val type: String,
    val text: String? = null
)

@Serializable
data class ClaudeError(
    val type: String = "",
    val message: String = ""
)