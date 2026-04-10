# My Pocket Dev — AI Pair Engineer

An Android app built with **Kotlin + Jetpack Compose** that acts as an AI pair programming assistant. Paste any code snippet and get instant analysis: design flaws, refactoring suggestions with before/after diffs, and proposed test cases.

Built as a Careem Senior Android Engineer application challenge (Challenge 2: The AI Pair Engineer).

## Architecture

```
┌─────────────────────────────────────────────┐
│                    UI Layer                  │
│  AnalysisScreen (Compose) ← AnalysisUiState │
│         ↕ events / state                    │
│       AnalysisViewModel (StateFlow)         │
├─────────────────────────────────────────────┤
│                 Data Layer                   │
│   CodeAnalysisRepository (validation)       │
│         ↓                                   │
│   ClaudeApiService (Ktor + Serialization)   │
│         ↓                                   │
│   Anthropic Messages API (claude-sonnet-4)  │
└─────────────────────────────────────────────┘
```

**Key decisions:**
- **MVVM + StateFlow** — single source of truth via `AnalysisUiState`, unidirectional data flow
- **Manual DI** — no Hilt/Dagger overhead for a focused demo; dependencies wired in `MainActivity`
- **Ktor** — lightweight, Kotlin-native HTTP client; cleaner than Retrofit for a single-endpoint API
- **Structured JSON output** — system prompt enforces strict JSON schema, parsed with kotlinx.serialization
- **Sealed interface for state** — `AnalysisState` covers Idle/Loading/Success/Error exhaustively

## Setup

1. Clone this project into Android Studio (Ladybug+)
2. Add your Claude API key in `local.properties`:
   ```
   CLAUDE_API_KEY=sk-ant-your-key-here
   ```
3. Sync Gradle & run on device/emulator (API 26+)

## Features

- **8 language support** — Kotlin, Java, Python, JS, TS, Swift, Go, Rust
- **Design Flaws panel** — severity-tagged issues (Critical → Low) with line references
- **Refactoring panel** — before/after code diffs with rationale
- **Tests panel** — unit/integration/UI test proposals with pseudo-code
- **Dark theme** — Careem-inspired emerald palette, code-editor feel
- **Smooth animations** — fade/slide transitions on results

## Project Structure

```
com.apptivelabs.mypocketdev/
├── data/
│   ├── api/          ClaudeApiService — Ktor client + prompt engineering
│   ├── model/        Data classes (CodeAnalysisResponse, Severity, etc.)
│   └── repository/   CodeAnalysisRepository — validation + API delegation
├── ui/
│   ├── screens/      AnalysisScreen + AnalysisViewModel
│   └── theme/        Material3 dark theme definition
└── MainActivity.kt   Entry point, manual DI wiring
```

## Why This Approach

As a Senior Android Engineer submission, this demonstrates:
- **Production architecture** — not a script, but a properly layered app
- **Compose proficiency** — custom components, animations, Material3 theming
- **AI integration** — structured prompt engineering, JSON parsing, error handling
- **Clean code** — small files, clear responsibilities, exhaustive state modeling

---
*Built by Abdul · apptivelabs · 2026*
