# Todo list

This class class TransactionalViewModelTest already the new mock lib
I need for each tests that has in this project and the test need mock
you should use this mokkery.

todo
[x] List all the tests in this project
[x] List all the tests that needs a mock
[x] replace the old mock in the test with the mokkery

## All Test Files in Project (13 files)

### Tests Already Using Mokkery ✅
1. `TransactionalViewModelTest.kt` - Already migrated to mokkery

### Tests Migrated to Mokkery ✅
2. `GetTransactionsForMonthTest.kt` - ✅ Migrated (CapturingRepository → mokkery mock with capture)
3. `TransactionRepositoryMappingTest.kt` - ✅ Migrated (FakeDao → mokkery mock)
4. `TransactionRepositoryDeleteTest.kt` - ✅ Migrated (FakeDao → mokkery mock)
5. `ItemRepositoryImplTest.kt` - ✅ Migrated (FakeDao → mokkery mock with capture)
6. `GetAllItemsUseCaseTest.kt` - ✅ Migrated (FakeRepo → mokkery mock)
7. `DeleteItemUseCaseTest.kt` - ✅ Migrated (FakeRepo → mokkery mock with capture)
8. `AddItemUseCaseTest.kt` - ✅ Migrated (FakeRepo → mokkery mock)
9. `TransactionalViewModelAndroidTest.kt` - ✅ Migrated (CapturingRepository → mokkery mock with calls)

### Tests Without Mocking (No Changes Needed) ✓
10. `MainScreenViewModelTest.kt` - Uses MutableStateFlow only, no custom mocks
11. `ConvertersTest.kt` - Pure unit test, no mocking
12. `ComposeAppCommonTest.kt` - Simple example test
13. `FinancialUtilsTest.kt` - Pure function tests, no mocking

## Migration Complete! 🎉

All 9 test files that used custom fakes/mocks have been successfully migrated to use Mokkery.

### Summary of Changes

**Key Mokkery Features Used:**
- `mock<T>` - Create mock instances with configuration blocks
- `every { }` - Stub non-suspend functions
- `everySuspend { }` - Stub suspend functions
- `returns` - Return values from stubs
- `calls` - Execute custom code when a function is called
- `capture()` - Capture arguments passed to functions
- `verifySuspend { }` - Verify suspend function calls
- `any()` - Match any argument

**All tests passing:** ✅ 28/28 tests successful

### Migration Patterns Applied

1. **Simple Repository Mocks**: Replaced custom fake repositories with mokkery mocks
2. **Capturing Arguments**: Used `Capture.slot<T>()` and `capture()` for parameter verification
3. **Async Testing**: Used `calls` with lambda for capturing values in async operations
4. **Flow Mocking**: Used `flowOf()` with `returns` for Flow-based functions

