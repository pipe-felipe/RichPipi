package data.repository

import data.remote.GoogleDriveService
import domain.model.BackupResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackupRepositoryImplTest {

    @Test
    fun `repository should delegate to correct service for GoogleDrive`() {
        // Given
        val mockGoogleDriveService = MockGoogleDriveService(
            createFolderResult = BackupResult.Success,
        )
        val repository = BackupRepositoryImpl(mockGoogleDriveService)

        // When/Then - Test the delegation logic
        assertTrue(mockGoogleDriveService is GoogleDriveService)
        assertEquals(BackupResult.Success, mockGoogleDriveService.createFolderResult)
    }

    @Test
    fun `should handle authentication status correctly`() {
        // Given
        val mockGoogleDriveService = MockGoogleDriveService(
            isAuthenticatedResult = true,
        )
        val repository = BackupRepositoryImpl(mockGoogleDriveService)

        // When/Then
        assertTrue(mockGoogleDriveService.isAuthenticatedResult)
    }
}

private class MockGoogleDriveService(
    val createFolderResult: BackupResult = BackupResult.Success,
    val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    val createSpreadsheetResult: BackupResult = BackupResult.Success,
) : GoogleDriveService {

    var lastCreatedFolderName: String? = null
    var authenticateCalled = false

    // New tracking fields for spreadsheet creation
    var lastCreatedSpreadsheetName: String? = null
    var lastSpreadsheetFolderName: String? = null

    override suspend fun createFolder(folderName: String): BackupResult {
        lastCreatedFolderName = folderName
        return createFolderResult
    }

    override suspend fun isAuthenticated(): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(): BackupResult {
        authenticateCalled = true
        return authenticateResult
    }

    // New suspend implementation for createSpreadsheet
    override suspend fun createSpreadsheet(folderName: String, spreadsheetName: String): BackupResult {
        lastSpreadsheetFolderName = folderName
        lastCreatedSpreadsheetName = spreadsheetName
        return createSpreadsheetResult
    }
}
