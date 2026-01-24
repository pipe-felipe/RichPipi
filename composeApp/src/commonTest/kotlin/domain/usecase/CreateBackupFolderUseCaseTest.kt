package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateBackupFolderUseCaseTest {

    @Test
    fun `execute should create backup folder when authenticated`() = runTest {
        // Given
        val mockRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
        )
        val useCase = CreateBackupFolderUseCase(mockRepository)

        // When
        val result = useCase.execute()

        // Then
        assertEquals("rich-pipi-backup", mockRepository.lastCreatedFolderName)
        assertEquals(BackupResult.Success, result)
    }

    @Test
    fun `execute should return error when folder creation fails`() = runTest {
        // Given
        val createError = BackupResult.Error("Folder creation failed")
        val mockRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = createError,
        )
        val useCase = CreateBackupFolderUseCase(mockRepository)

        // When
        val result = useCase.execute()

        // Then
        assertTrue(result is BackupResult.Error)
        assertEquals("Folder creation failed", (result as BackupResult.Error).message)
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() = runTest {
        // Given
        val mockRepository = MockBackupRepository(
            isAuthenticatedResult = false,
            authenticateResult = BackupResult.SignInRequired,
        )
        val useCase = CreateBackupFolderUseCase(mockRepository)

        // When
        val result = useCase.execute()

        // Then
        assertEquals(BackupResult.SignInRequired, result)
    }
}

private class MockBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val createFolderResult: BackupResult = BackupResult.Success,
) : BackupRepository {

    var authenticateCalled = false
    var lastCreatedFolderName: String? = null

    override suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult {
        lastCreatedFolderName = folderName
        return createFolderResult
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        authenticateCalled = true
        return authenticateResult
    }
}
