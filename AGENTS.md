# AGENTS.md

AI agent instructions for the RichPipi project.

## Project Overview

RichPipi is a Kotlin Multiplatform project using Jetpack Compose for UI. The project follows Clean Architecture principles for all external integrations and maintains a strict separation between domain logic and framework-specific implementations.

## Architecture

### UI Layer (View & ViewModel)

**Framework**: Jetpack Compose

**Pattern**: View–ViewModel architecture

**State Management**:
- Model UI state explicitly and keep it immutable
- Follow unidirectional data flow
- Use dependency injection
- Depend on abstractions, not implementations

### Clean Architecture Layers

#### Domain Layer (Pure Kotlin, Framework-Agnostic)

**Contains**:
- Entities
- Value objects
- Use cases
- Repository interfaces

**Rules**:
- Must be pure Kotlin with NO framework dependencies
- Must be platform-independent
- All business logic lives here
- Only depends on other domain layer code

#### Data/Infrastructure Layer

**Contains**:
- Repository implementations
- Data sources (local/remote)
- Framework-specific code

**Handles**:
- Databases
- Google Authentication
- Network APIs
- Local storage
- Platform-specific services

**Rules**:
- Implements domain interfaces
- Depends on frameworks, SDKs, and third-party libraries
- Frameworks and libraries are treated as implementation details

#### Dependency Direction

**⚠️ Critical Rule**: Dependency direction must ALWAYS point inward (toward the domain layer)

```
[Data/Infrastructure] ──depends on──> [Domain]
[ViewModel] ──depends on──> [Domain]
```

### Kotlin Multiplatform Structure

**Shared Code** (`commonMain`):
- Place all shared business logic here
- Domain layer code
- Shared ViewModels
- Common utilities

**Platform-Specific Code**:
- `androidMain`: Android-specific implementations
- `iosMain`: iOS-specific implementations
- Other platform source sets as needed

**Rules**:
- Avoid platform APIs in `commonMain`
- Use `expect`/`actual` mechanism ONLY when necessary
- Business logic must be shared whenever possible
- Keep platform-specific code minimal

## Code Style

### Language & Conventions

- Write idiomatic Kotlin
- Follow existing project structure and naming conventions
- Prioritize readability and maintainability over cleverness
- Avoid unnecessary dependencies
- Do NOT tightly couple code to Jetpack Compose, Android SDK, or third-party libraries

### Strings & Localization

**⚠️ Important**: All user-facing strings must be in Portuguese (pt-BR)

**Rules**:
- Variable names: English
- String values: Portuguese
- All strings go in `strings.xml`

**Example**:
```kotlin
// ✅ Correct
val welcomeMessage = getString(R.string.welcome_message)

// strings.xml
<string name="welcome_message">Bem-vindo ao RichPipi</string>

// ❌ Wrong
val welcomeMessage = "Welcome to RichPipi"
```

### Naming Conventions

- **Variables/Functions**: camelCase, English
- **Classes**: PascalCase, English
- **Constants**: UPPER_SNAKE_CASE, English
- **String resources**: snake_case, Portuguese values

## Testing Requirements

### ⚠️ MANDATORY: All New Implementations MUST Include Tests

**No exceptions.** No new feature, integration, or business logic should be added without corresponding tests.

### Domain Layer Tests

**Requirements**:
- Unit tests for ALL use cases and business rules
- Must be platform-independent
- Place in `commonTest`

**Coverage**:
- All use case scenarios (success/failure paths)
- Business rule validation
- Edge cases and error handling

**Example Structure**:
```
commonTest/
  domain/
    usecase/
      GetUserBalanceUseCaseTest.kt
      CreateTransactionUseCaseTest.kt
```

### ViewModel Tests

**Requirements**:
- Unit tests covering ALL state changes and user interactions
- Test in isolation using fakes/mocks for dependencies

**Coverage**:
- Initial state
- User actions and their effects
- State transitions
- Error handling
- Loading states

### Data Layer Tests

**Requirements**:
- Tests for repositories and data sources
- Use fakes or stubs for external services
- Avoid testing framework details directly

**Coverage**:
- Repository operations (CRUD)
- Data mapping/transformation
- Caching logic
- Error handling

### Kotlin Multiplatform Testing

**Test Placement**:
- **Prefer**: `commonTest` whenever possible
- **Platform-specific**: Only use `androidTest` or `iosTest` when testing platform-specific behavior

**Test Quality**:
- Tests must be deterministic and repeatable
- No flaky tests
- No reliance on real network calls
- No reliance on system state
- Use fakes, stubs, or mocks for external dependencies

### Testing Structure Example

```
commonTest/
  domain/
    usecase/
      ...Test.kt
  data/
    repository/
      ...RepositoryTest.kt
  presentation/
    viewmodel/
      ...ViewModelTest.kt

androidTest/
  infrastructure/
    database/
      ...DatabaseTest.kt

iosTest/
  infrastructure/
    storage/
      ...StorageTest.kt
```

## Development Environment

**Operating System**: Windows (Microsoft)

**Terminal**: Git Bash-compatible commands required

**Path Format**:
- ✅ Use: `/c/Users/pipea/Projects/RichPipi`
- ❌ Avoid: `C:\Users\pipea\Projects\RichPipi`

**Command Style**:
- Prefer PowerShell-native syntax when applicable
- Avoid Bash-specific commands that won't work in Git Bash
- Test commands work in Git Bash on Windows

## Code Generation Rules

### When Creating New Features

1. ✅ **Start with Domain Layer**
   - Define entities/value objects
   - Create repository interfaces
   - Implement use cases
   - Write unit tests

2. ✅ **Then Data/Infrastructure Layer**
   - Implement repository interfaces
   - Create data sources
   - Add framework-specific code
   - Write repository tests

3. ✅ **Finally UI Layer**
   - Create ViewModel with state
   - Implement Compose UI
   - Wire up dependency injection
   - Write ViewModel tests

### When Adding External Integrations

Follow Clean Architecture:
- Define interface in domain layer
- Implement in data/infrastructure layer
- Inject through dependency injection
- Write tests with fakes/stubs

### Dependency Injection

- Use constructor injection
- Depend on interfaces, not concrete implementations
- Keep dependencies explicit
- Make classes testable

## What TO Do

- ✅ Follow View–ViewModel pattern for UI
- ✅ Apply Clean Architecture for ALL external integrations
- ✅ Keep domain layer pure and framework-agnostic
- ✅ Write tests alongside ALL new implementations
- ✅ Use Portuguese for user-facing strings in `strings.xml`
- ✅ Maintain unidirectional data flow
- ✅ Keep state immutable
- ✅ Use dependency injection
- ✅ Follow existing project structure
- ✅ Share business logic in `commonMain`
- ✅ Write idiomatic Kotlin
- ✅ Make code readable and maintainable

## What NOT to Do

- ❌ Add features without tests
- ❌ Use platform APIs in `commonMain`
- ❌ Tightly couple to frameworks (Compose, Android SDK, etc.)
- ❌ Write user-facing strings in English
- ❌ Create mutable state
- ❌ Violate dependency direction (never depend outward)
- ❌ Add unnecessary dependencies
- ❌ Skip writing tests "for now"
- ❌ Put business logic in ViewModels or UI layer
- ❌ Use framework-specific code in domain layer
- ❌ Create flaky or non-deterministic tests
- ❌ Test framework implementation details

## Quick Reference

### File Placement

| Type | Location |
|------|----------|
| Entities, Value Objects | `commonMain/domain/model/` |
| Use Cases | `commonMain/domain/usecase/` |
| Repository Interfaces | `commonMain/domain/repository/` |
| Repository Implementations | `commonMain/data/repository/` or `androidMain/data/repository/` |
| Data Sources | `commonMain/data/source/` or platform-specific |
| ViewModels | `commonMain/presentation/viewmodel/` |
| UI Screens | `androidMain/presentation/ui/` |
| Domain Tests | `commonTest/domain/` |
| ViewModel Tests | `commonTest/presentation/viewmodel/` |
| Data Tests | `commonTest/data/` or platform-specific test folders |

### Dependency Flow

```
UI (Compose) ──> ViewModel ──> Use Case ──> Repository Interface
                                                    ↑
                                                    |
                                          Repository Implementation ──> Data Source
```

### Test Coverage Checklist

- [ ] Domain layer use cases tested
- [ ] ViewModels tested (all states)
- [ ] Repositories tested (with fakes)
- [ ] Tests are in `commonTest` when possible
- [ ] Tests are deterministic
- [ ] No real network/database calls in tests

## Examples

### Creating a New Feature: Transaction History

1. **Domain Layer** (`commonMain/domain/`)
   ```kotlin
   // model/Transaction.kt
   data class Transaction(val id: String, val amount: Double, val date: LocalDateTime)

   // repository/TransactionRepository.kt
   interface TransactionRepository {
       suspend fun getTransactions(): Result<List<Transaction>>
   }

   // usecase/GetTransactionsUseCase.kt
   class GetTransactionsUseCase(private val repository: TransactionRepository) {
       suspend operator fun invoke(): Result<List<Transaction>> {
           return repository.getTransactions()
       }
   }
   ```

2. **Data Layer** (`commonMain/data/` or `androidMain/data/`)
   ```kotlin
   // repository/TransactionRepositoryImpl.kt
   class TransactionRepositoryImpl(
       private val localDataSource: TransactionLocalDataSource
   ) : TransactionRepository {
       override suspend fun getTransactions(): Result<List<Transaction>> {
           return localDataSource.getTransactions()
       }
   }
   ```

3. **ViewModel** (`commonMain/presentation/viewmodel/`)
   ```kotlin
   data class TransactionHistoryState(
       val transactions: List<Transaction> = emptyList(),
       val isLoading: Boolean = false,
       val error: String? = null
   )

   class TransactionHistoryViewModel(
       private val getTransactionsUseCase: GetTransactionsUseCase
   ) : ViewModel() {
       // Implementation with unidirectional data flow
   }
   ```

4. **Tests** (`commonTest/`)
   ```kotlin
   // domain/usecase/GetTransactionsUseCaseTest.kt
   class GetTransactionsUseCaseTest {
       @Test
       fun `should return transactions when repository succeeds`() {
           // Test implementation with fake repository
       }
   }

   // presentation/viewmodel/TransactionHistoryViewModelTest.kt
   class TransactionHistoryViewModelTest {
       @Test
       fun `should update state when loading transactions`() {
           // Test implementation
       }
   }
   ```

5. **Strings** (`androidMain/res/values/strings.xml`)
   ```xml
   <string name="transaction_history_title">Histórico de Transações</string>
   <string name="transaction_history_empty">Nenhuma transação encontrada</string>
   <string name="transaction_history_error">Erro ao carregar transações</string>
   ```

---

**Remember**: Clean Architecture + Tests + Portuguese Strings = Success! 🎯
