package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.model.ImportResult
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile
import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.BackupRepository
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImportDataFromSpreadsheetUseCaseTest {

    @Test
    fun `execute should import transactions when authenticated`() = runTest {
        val spreadsheetData = SpreadsheetData(
            headers = listOf("ID", "Valor (centavos)", "Tipo", "Categoria", "Descrição", "Data", "Recorrente", "Criado em", "Mês Destino", "Ano Destino"),
            rows = listOf(
                listOf("1", "10000", "Receita", "Salário", "Pagamento", "01/01/2025", "Sim", "1704067200000", "1", "2025"),
                listOf("2", "5000", "Despesa", "Alimentação", "Mercado", "02/01/2025", "Não", "1704153600000", "1", "2025"),
            ),
        )

        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = true,
            readSpreadsheetDataResult = spreadsheetData,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute("test-spreadsheet-id")

        assertTrue(result is ImportResult.Success)
        assertEquals(2, (result as ImportResult.Success).importedCount)
        assertTrue(mockTransactionRepository.deleteAllCalled)
        assertEquals(2, mockTransactionRepository.insertedTransactions.size)
    }

    @Test
    fun `execute should return SignInRequired when not authenticated`() = runTest {
        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = false,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute("test-spreadsheet-id")

        assertEquals(ImportResult.SignInRequired, result)
    }

    @Test
    fun `execute should return error when reading spreadsheet fails`() = runTest {
        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = true,
            readSpreadsheetDataResult = null,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute("test-spreadsheet-id")

        assertTrue(result is ImportResult.Error)
    }

    @Test
    fun `execute should return success with zero count when no rows`() = runTest {
        val spreadsheetData = SpreadsheetData(
            headers = listOf("ID", "Valor (centavos)", "Tipo", "Categoria", "Descrição", "Data", "Recorrente", "Criado em"),
            rows = emptyList(),
        )

        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = true,
            readSpreadsheetDataResult = spreadsheetData,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute("test-spreadsheet-id")

        assertTrue(result is ImportResult.Success)
        assertEquals(0, (result as ImportResult.Success).importedCount)
    }

    @Test
    fun `execute should parse transaction types correctly`() = runTest {
        val spreadsheetData = SpreadsheetData(
            headers = listOf("ID", "Valor (centavos)", "Tipo", "Categoria", "Descrição", "Data", "Recorrente", "Criado em", "Mês Destino", "Ano Destino"),
            rows = listOf(
                listOf("1", "10000", "Receita", "Salário", "Pagamento", "01/01/2025", "Não", "1704067200000", "1", "2025"),
                listOf("2", "5000", "Despesa", "Alimentação", "Mercado", "02/01/2025", "Não", "1704153600000", "1", "2025"),
            ),
        )

        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = true,
            readSpreadsheetDataResult = spreadsheetData,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        useCase.execute("test-spreadsheet-id")

        assertEquals(TransactionType.INCOME, mockTransactionRepository.insertedTransactions[0].type)
        assertEquals(TransactionType.EXPENSE, mockTransactionRepository.insertedTransactions[1].type)
    }

    @Test
    fun `execute should skip invalid rows`() = runTest {
        val spreadsheetData = SpreadsheetData(
            headers = listOf("ID", "Valor (centavos)", "Tipo", "Categoria", "Descrição", "Data", "Recorrente", "Criado em", "Mês Destino", "Ano Destino"),
            rows = listOf(
                listOf("1", "10000", "Receita", "Salário", "Pagamento", "01/01/2025", "Sim", "1704067200000", "1", "2025"),
                listOf("2", "invalid", "Despesa", "Alimentação", "Mercado", "02/01/2025", "Não", "1704153600000", "1", "2025"),
                listOf("3", "3000", "InvalidType", "Alimentação", "Mercado", "03/01/2025", "Não", "1704240000000", "1", "2025"),
            ),
        )

        val mockBackupRepository = ImportTestBackupRepository(
            isAuthenticatedResult = true,
            readSpreadsheetDataResult = spreadsheetData,
        )
        val mockTransactionRepository = ImportTestTransactionRepository()
        val useCase = ImportDataFromSpreadsheetUseCase(mockBackupRepository, mockTransactionRepository)

        val result = useCase.execute("test-spreadsheet-id")

        assertTrue(result is ImportResult.Success)
        assertEquals(1, result.importedCount)
    }
}

private class ImportTestBackupRepository(
    private val isAuthenticatedResult: Boolean = false,
    private val readSpreadsheetDataResult: SpreadsheetData? = null,
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
        return emptyList()
    }

    override suspend fun readSpreadsheetData(service: BackupService, spreadsheetId: String): SpreadsheetData? {
        return readSpreadsheetDataResult
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return isAuthenticatedResult
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        return BackupResult.Success
    }

    override suspend fun getAuthenticatedUserName(service: BackupService): String? {
        return null
    }
}

private class ImportTestTransactionRepository : TransactionRepository {

    var deleteAllCalled = false
    var insertedTransactions: List<Transaction> = emptyList()

    override fun getTransactions(): Flow<List<Transaction>> {
        return flowOf(emptyList())
    }

    override fun getTransactionsForMonth(monthStartMillis: Long, monthEndExclusiveMillis: Long): Flow<List<Transaction>> {
        return flowOf(emptyList())
    }

    override suspend fun makeTransaction(transaction: Transaction): Long {
        return 1L
    }

    override suspend fun deleteTransaction(id: Int): Int {
        return 1
    }

    override suspend fun deleteAllTransactions(): Int {
        deleteAllCalled = true
        return 0
    }

    override suspend fun insertTransactions(transactions: List<Transaction>): List<Long> {
        insertedTransactions = transactions
        return transactions.mapIndexed { index, _ -> index.toLong() + 1 }
    }
}
