package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile
import domain.repository.BackupRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateSpreadSheetUseCaseTest {

    companion object {
        private const val EPOCH_TEST = 1760272740000L
    }

    @Test
    fun `execute should create spreadsheet when authenticated`() = runTest {
        val mockRepository = TestMockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetResult = BackupResult.Success,
        )
        val useCase = CreateSpreadSheetUseCase(mockRepository)

        val result = useCase.execute(EPOCH_TEST)

        assertEquals(BackupResult.Success, result)
        assertEquals(
            BackupConstants.getBackupSpreadsheetWithTimestamp(
                EPOCH_TEST,
            ),
            mockRepository.lastCreatedSpreadsheetName,
        )
        assertEquals(
            BackupConstants.DEFAULT_FOLDER_NAME,
            mockRepository.lastSpreadsheetFolderName,
        )
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() =
        runTest {
            val mockRepository = TestMockBackupRepository(
                isAuthenticatedResult = false,
                authenticateResult = BackupResult.SignInRequired,
            )
            val useCase = CreateSpreadSheetUseCase(mockRepository)

            val result = useCase.execute(EPOCH_TEST)

            assertEquals(BackupResult.SignInRequired, result)
        }

    @Test
    fun `execute should use valid timestamp formats for file and folder names`() =
        runTest {
            val mockRepository = TestMockBackupRepository(
                isAuthenticatedResult = true,
                createFolderResult = BackupResult.Success,
                createSpreadsheetResult = BackupResult.Success,
            )
            val useCase = CreateSpreadSheetUseCase(mockRepository)

            val fixedEpoch = 1760272740000L
            val result = useCase.execute(fixedEpoch)

            assertEquals(BackupResult.Success, result)

            // Verifica se os nomes gerados são válidos
            val folderName = mockRepository.lastSpreadsheetFolderName
            val spreadsheetName = mockRepository.lastCreatedSpreadsheetName

            assertNotNull(folderName)
            assertNotNull(spreadsheetName)

            // Verifica se não contém caracteres problemáticos
            assertTrue(
                !folderName.contains("/") && !folderName.contains(":") && !folderName.contains(" "),
                "Nome da pasta contém caracteres inválidos: $folderName",
            )
            assertTrue(
                !spreadsheetName.contains("/") && !spreadsheetName.contains(":") && !spreadsheetName.contains(" "),
                "Nome da planilha contém caracteres inválidos: $spreadsheetName",
            )

            // Verifica se seguem o padrão esperado
            assertTrue(folderName.startsWith("rich-pipi-backup"))
            assertTrue(spreadsheetName.startsWith("rich-pipi-backup-sheet_"))

            // Verifica se o timestamp está correto para o epoch fornecido
            val expectedTimestamp = "29-10-2025_12-39"
            assertEquals("rich-pipi-backup-sheet_" + expectedTimestamp, spreadsheetName)
        }
}

class TestMockBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val authenticateResult: BackupResult = BackupResult.Success,
    private val createFolderResult: BackupResult = BackupResult.Success,
    private val createSpreadsheetResult: BackupResult = BackupResult.Success,
    private val createSpreadsheetWithDataResult: BackupResult = BackupResult.Success,
) : BackupRepository {

    var authenticateCalled = false
    var lastCreatedFolderName: String? = null

    var lastCreatedSpreadsheetName: String? = null
    var lastSpreadsheetFolderName: String? = null
    var lastSpreadsheetData: SpreadsheetData? = null

    override suspend fun createBackupFolder(
        service: BackupService,
        folderName: String,
    ): BackupResult {
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

    override suspend fun createSpreadsheetWithData(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult {
        lastSpreadsheetFolderName = folderName
        lastCreatedSpreadsheetName = spreadsheetName
        lastSpreadsheetData = data
        return createSpreadsheetWithDataResult
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        authenticateCalled = true
        return authenticateResult
    }

    override suspend fun listBackupSpreadsheets(
        service: BackupService,
        folderName: String,
    ): List<SpreadsheetFile> {
        return emptyList()
    }

    override suspend fun readSpreadsheetData(
        service: BackupService,
        spreadsheetId: String,
    ): SpreadsheetData? {
        return null
    }

    override suspend fun getAuthenticatedUserName(service: BackupService): String? {
        return null
    }
}
