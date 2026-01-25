package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile
import domain.repository.BackupRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListBackupSpreadsheetsUseCaseTest {

    @Test
    fun `execute should return spreadsheets when authenticated`() = runTest {
        val expectedSpreadsheets = listOf(
            SpreadsheetFile("id1", "backup-2025-01-01", "2025-01-01T10:00:00Z"),
            SpreadsheetFile("id2", "backup-2025-01-02", "2025-01-02T10:00:00Z"),
        )

        val mockRepository = ListTestBackupRepository(
            isAuthenticatedResult = true,
            listSpreadsheetsResult = expectedSpreadsheets,
        )
        val useCase = ListBackupSpreadsheetsUseCase(mockRepository)

        val result = useCase.execute()

        assertTrue(result is ListBackupsResult.Success)
        assertEquals(2, (result as ListBackupsResult.Success).spreadsheets.size)
        assertEquals("id1", result.spreadsheets[0].id)
        assertEquals("id2", result.spreadsheets[1].id)
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() = runTest {
        val mockRepository = ListTestBackupRepository(
            isAuthenticatedResult = false,
            authenticateResult = BackupResult.SignInRequired,
        )
        val useCase = ListBackupSpreadsheetsUseCase(mockRepository)

        val result = useCase.execute()

        assertTrue(result is ListBackupsResult.SignInRequired)
    }

    @Test
    fun `execute should return empty list when no backups exist`() = runTest {
        val mockRepository = ListTestBackupRepository(
            isAuthenticatedResult = true,
            listSpreadsheetsResult = emptyList(),
        )
        val useCase = ListBackupSpreadsheetsUseCase(mockRepository)

        val result = useCase.execute()

        assertTrue(result is ListBackupsResult.Success)
        assertTrue((result as ListBackupsResult.Success).spreadsheets.isEmpty())
    }
}

private class ListTestBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val listSpreadsheetsResult: List<SpreadsheetFile> = emptyList(),
) : BackupRepository {

    override suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult {
        return BackupResult.Success
    }

    override suspend fun createSpreadsheet(service: BackupService, folderName: String, spreadsheetName: String): BackupResult {
        return BackupResult.Success
    }

    override suspend fun createSpreadsheetWithData(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult {
        return BackupResult.Success
    }

    override suspend fun listBackupSpreadsheets(service: BackupService, folderName: String): List<SpreadsheetFile> {
        return listSpreadsheetsResult
    }

    override suspend fun readSpreadsheetData(service: BackupService, spreadsheetId: String): SpreadsheetData? {
        return null
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        return authenticateResult
    }

    override suspend fun getAuthenticatedUserName(service: BackupService): String? {
        return null
    }
}
