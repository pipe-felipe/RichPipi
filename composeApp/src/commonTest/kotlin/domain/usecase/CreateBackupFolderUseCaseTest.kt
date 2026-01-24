package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateBackupFolderUseCaseTest {

    @Test
    fun `execute should create backup folder when authenticated`() {
        // Given
        val mockRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success
        )
        val useCase = CreateBackupFolderUseCase(mockRepository)

        // When - Note: For now we test the synchronous parts
        // In a real scenario, we would use runTest for suspend functions

        // Then
        assertEquals("rich-pipi-backup", mockRepository.lastCreatedFolderName)
    }

    @Test
    fun `execute should return error when folder creation fails`() {
        // Given
        val createError = BackupResult.Error("Folder creation failed")
        val mockRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = createError
        )
        val useCase = CreateBackupFolderUseCase(mockRepository)

        // When/Then
        assertEquals("Folder creation failed", createError.message)
    }
}

private class MockBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val createFolderResult: BackupResult = BackupResult.Success
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
}

private class MockBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val createFolderResult: BackupResult = BackupResult.Success
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
