package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateSpreadSheetUseCaseTest {

    @Test
    fun `execute should create spreadsheet when authenticated`() = runTest {
        val mockRepository = TestMockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetResult = BackupResult.Success,
        )
        val useCase = CreateSpreadSheetUseCase(mockRepository)

        val result = useCase.execute()

        assertEquals(BackupResult.Success, result)
        assertEquals(BackupConstants.getBackupSpreadsheetWithTimestamp(), mockRepository.lastCreatedSpreadsheetName)
        assertEquals(BackupConstants.getBackupFolderWithTimestamp(), mockRepository.lastSpreadsheetFolderName)
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() = runTest {
        val mockRepository = TestMockBackupRepository(
            isAuthenticatedResult = false,
            authenticateResult = BackupResult.SignInRequired,
        )
        val useCase = CreateSpreadSheetUseCase(mockRepository)

        val result = useCase.execute()

        assertEquals(BackupResult.SignInRequired, result)
    }

    @Test
    fun `execute should use valid timestamp formats for file and folder names`() = runTest {
        val mockRepository = TestMockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetResult = BackupResult.Success,
        )
        val useCase = CreateSpreadSheetUseCase(mockRepository)

        val result = useCase.execute()

        assertEquals(BackupResult.Success, result)

        // Verifica se os nomes gerados são válidos
        val folderName = mockRepository.lastSpreadsheetFolderName
        val spreadsheetName = mockRepository.lastCreatedSpreadsheetName

        assertNotNull(folderName)
        assertNotNull(spreadsheetName)

        // Verifica se não contém caracteres problemáticos
        assertTrue(
            !folderName.contains("/") && !folderName.contains(":") && !folderName.contains(" "),
            "Nome da pasta contém caracteres inválidos: $folderName"
        )
        assertTrue(
            !spreadsheetName.contains("/") && !spreadsheetName.contains(":") && !spreadsheetName.contains(" "),
            "Nome da planilha contém caracteres inválidos: $spreadsheetName"
        )

        // Verifica se seguem o padrão esperado
        assertTrue(folderName.startsWith("rich-pipi-backup_"))
        assertTrue(spreadsheetName.startsWith("rich-pipi-backup-sheet_"))
    }
}

class TestMockBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val createFolderResult: BackupResult = BackupResult.Success,
    private val createSpreadsheetResult: BackupResult = BackupResult.Success,
) : BackupRepository {

    var authenticateCalled = false
    var lastCreatedFolderName: String? = null

    var lastCreatedSpreadsheetName: String? = null
    var lastSpreadsheetFolderName: String? = null

    override suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult {
        lastCreatedFolderName = folderName
        return createFolderResult
    }

    override suspend fun createSpreadsheet(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
    ): BackupResult {
        lastSpreadsheetFolderName = folderName
        lastCreatedSpreadsheetName = spreadsheetName
        return createSpreadsheetResult
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        authenticateCalled = true
        return authenticateResult
    }
}
