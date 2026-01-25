package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData
import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.BackupRepository
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ExportDataToSpreadsheetUseCaseTest {

    companion object {
        private const val EPOCH_TEST = 1760272740000L
    }

    @Test
    fun `execute should export transactions to spreadsheet when authenticated`() = runTest {
        val transactions = listOf(
            Transaction(
                id = 1,
                amountCents = 10000,
                type = TransactionType.INCOME,
                category = "Salário",
                description = "Pagamento mensal",
                humanDate = "01/01/2025",
                isRecurring = true,
                createdAt = 1704067200000L,
            ),
            Transaction(
                id = 2,
                amountCents = 5000,
                type = TransactionType.EXPENSE,
                category = "Alimentação",
                description = "Mercado",
                humanDate = "02/01/2025",
                isRecurring = false,
                createdAt = 1704153600000L,
            ),
        )

        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetWithDataResult = BackupResult.Success,
        )
        val mockTransactionRepository = MockTransactionRepository(transactions)
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute(EPOCH_TEST)

        assertEquals(BackupResult.Success, result)
        assertNotNull(mockBackupRepository.lastSpreadsheetData)
        assertEquals(2, mockBackupRepository.lastSpreadsheetData?.rows?.size)
        assertEquals(
            BackupConstants.getBackupSpreadsheetWithTimestamp(EPOCH_TEST),
            mockBackupRepository.lastCreatedSpreadsheetName,
        )
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() = runTest {
        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = false,
        )
        val mockTransactionRepository = MockTransactionRepository(emptyList())
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute(EPOCH_TEST)

        assertEquals(BackupResult.SignInRequired, result)
    }

    @Test
    fun `execute should return error when folder creation fails`() = runTest {
        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Error("Folder creation failed"),
        )
        val mockTransactionRepository = MockTransactionRepository(emptyList())
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute(EPOCH_TEST)

        assertTrue(result is BackupResult.Error)
        assertEquals("Folder creation failed", (result as BackupResult.Error).message)
    }

    @Test
    fun `execute should export empty spreadsheet when no transactions`() = runTest {
        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetWithDataResult = BackupResult.Success,
        )
        val mockTransactionRepository = MockTransactionRepository(emptyList())
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute(EPOCH_TEST)

        assertEquals(BackupResult.Success, result)
        assertNotNull(mockBackupRepository.lastSpreadsheetData)
        assertEquals(0, mockBackupRepository.lastSpreadsheetData?.rows?.size)
        assertEquals(8, mockBackupRepository.lastSpreadsheetData?.headers?.size)
    }

    @Test
    fun `execute should correctly convert transaction types to Portuguese`() = runTest {
        val transactions = listOf(
            Transaction(
                id = 1,
                amountCents = 10000,
                type = TransactionType.INCOME,
                category = "Salário",
                description = "Pagamento",
                humanDate = "01/01/2025",
                isRecurring = false,
                createdAt = 1704067200000L,
            ),
            Transaction(
                id = 2,
                amountCents = 5000,
                type = TransactionType.EXPENSE,
                category = "Alimentação",
                description = "Mercado",
                humanDate = "02/01/2025",
                isRecurring = false,
                createdAt = 1704153600000L,
            ),
        )

        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetWithDataResult = BackupResult.Success,
        )
        val mockTransactionRepository = MockTransactionRepository(transactions)
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        useCase.execute(EPOCH_TEST)

        val data = mockBackupRepository.lastSpreadsheetData
        assertNotNull(data)
        assertEquals("Receita", data.rows[0][2])
        assertEquals("Despesa", data.rows[1][2])
    }

    @Test
    fun `execute should correctly convert recurring field to Portuguese`() = runTest {
        val transactions = listOf(
            Transaction(
                id = 1,
                amountCents = 10000,
                type = TransactionType.INCOME,
                category = "Salário",
                description = "Pagamento",
                humanDate = "01/01/2025",
                isRecurring = true,
                createdAt = 1704067200000L,
            ),
            Transaction(
                id = 2,
                amountCents = 5000,
                type = TransactionType.EXPENSE,
                category = "Alimentação",
                description = "Mercado",
                humanDate = "02/01/2025",
                isRecurring = false,
                createdAt = 1704153600000L,
            ),
        )

        val mockBackupRepository = MockBackupRepository(
            isAuthenticatedResult = true,
            createFolderResult = BackupResult.Success,
            createSpreadsheetWithDataResult = BackupResult.Success,
        )
        val mockTransactionRepository = MockTransactionRepository(transactions)
        val useCase = ExportDataToSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        useCase.execute(EPOCH_TEST)

        val data = mockBackupRepository.lastSpreadsheetData
        assertNotNull(data)
        assertEquals("Sim", data.rows[0][6])
        assertEquals("Não", data.rows[1][6])
    }
}

private class MockBackupRepository(
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
}

private class MockTransactionRepository(
    private val transactions: List<Transaction>,
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return flowOf(transactions)
    }

    override fun getTransactionsForMonth(
        monthStartMillis: Long,
        monthEndExclusiveMillis: Long,
    ): Flow<List<Transaction>> {
        return flowOf(transactions.filter { it.createdAt in monthStartMillis until monthEndExclusiveMillis })
    }

    override suspend fun makeTransaction(transaction: Transaction): Long {
        return 1L
    }

    override suspend fun deleteTransaction(id: Int): Int {
        return 1
    }
}
