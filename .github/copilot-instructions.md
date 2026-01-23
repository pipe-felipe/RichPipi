# Copilot Instructions

This document defines the architectural and development guidelines that must be followed when generating or modifying code in this project.

## 🏗️ Project Architecture

### View & ViewModel (Jetpack Compose)

The project uses Jetpack Compose for UI.

UI layers must follow the View–ViewModel architecture.

**Composable functions are responsible only for:**
- Rendering UI
- Emitting UI events

**Composable functions must not contain:**
- Business logic
- Data access logic
- Direct calls to repositories, APIs, or databases

**ViewModels:**
- Hold UI state
- Handle user actions
- Communicate with domain use cases
- Expose immutable state to the UI
- Must not contain platform-specific code when avoidable

## 🧼 Clean Architecture for Integrations

All external integrations must follow Clean Architecture principles, including:
- Databases
- Google Authentication
- Network APIs
- Local storage
- Platform-specific services

### Clean Architecture Rules

**Domain layer must be pure Kotlin and framework-agnostic.**

**Domain layer contains:**
- Entities
- Value objects
- Use cases
- Repository interfaces

**Data / Infrastructure layers:**
- Implement domain interfaces
- Depend on frameworks, SDKs, and third-party libraries

**Additional Rules:**
- Dependency direction must always point inward
- Frameworks and libraries are treated as implementation details

## 🌍 Kotlin Multiplatform Guidelines

**Shared code placement:**
- Place shared code in `commonMain`
- Platform-specific code belongs to:
  - `androidMain`
  - `iosMain`
  - Other platform source sets

**General rules:**
- Avoid platform APIs in `commonMain`
- Use `expect`/`actual` only when necessary
- Business logic must be shared whenever possible

## 🧱 Dependency & State Management

- Use dependency injection
- Depend on abstractions, not implementations
- Model UI state explicitly and keep it immutable
- Follow unidirectional data flow

## 🧪 Testing (Mandatory for All New Implementations)

**All new implementations must include tests.**

No new feature, integration, or business logic should be added without corresponding tests.

### Testing Rules

**Domain layer:**
- Must have unit tests for all use cases and business rules
- Tests must be platform-independent

**ViewModels:**
- Must be covered by unit tests
- Test state changes and user interactions

**Data layer:**
- Must include tests for repositories and data sources
- External services should be tested using fakes or stubs

**General guidelines:**
- Avoid testing framework details directly

### Kotlin Multiplatform Testing Guidelines

**Test placement:**
- Prefer tests in `commonTest` whenever possible
- Platform-specific behavior must be tested in:
  - `androidTest`
  - `iosTest`

**Test quality:**
- Tests must be deterministic and repeatable
- Avoid flaky tests and reliance on real network or system state

## 💻 Terminal & Environment Rules

- Development is done on Windows (Microsoft)
- All terminal commands must be git bash-compatible
- Example: Use cd /c/User ... instead of cd C:\\
- Avoid Bash-specific commands
- Prefer PowerShell-native syntax

## ✅ General Code Generation Rules

- Follow the existing project structure and naming conventions
- Write idiomatic Kotlin
- Prefer readability and maintainability
- Do not introduce unnecessary dependencies
- Do not tightly couple code to Jetpack Compose, Android SDK, or third-party libraries
- If tests are missing, they must be added alongside the implementation

## Code Style
- For every string, you should put in portuguese in the strings.xml
- The variable name should be in english, but the string value should be in portuguese.

