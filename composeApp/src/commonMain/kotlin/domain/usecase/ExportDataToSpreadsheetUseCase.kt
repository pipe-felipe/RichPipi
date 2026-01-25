package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData
import domain.model.Transaction
import domain.model.TransactionType
import domain.repository.BackupRepository
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first

/**
 * Use case for exporting all database content to a Google Spreadsheet.
 * This follows Clean Architecture principles - business logic in the domain layer.
 */
class ExportDataToSpreadsheetUseCase(
    private val backupRepository: BackupRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun execute(nowEpochDate: Long): BackupResult {
        val service = BackupService.GoogleDrive
        val folderName = BackupConstants.DEFAULT_FOLDER_NAME
        val spreadsheetName = BackupConstants.getBackupSpreadsheetWithTimestamp(nowEpochDate)

        return try {
            if (!backupRepository.isAuthenticated(service)) {
                return BackupResult.SignInRequired
            }

            // Get all transactions from database
            val transactions = transactionRepository.getTransactions().first()

            // Convert transactions to spreadsheet data
            val spreadsheetData = convertToSpreadsheetData(transactions)

            // Ensure folder exists
            val folderResult = backupRepository.createBackupFolder(service, folderName)
            if (folderResult is BackupResult.Error) {
                return folderResult
            }

            // Create the spreadsheet with data
            backupRepository.createSpreadsheetWithData(service, folderName, spreadsheetName, spreadsheetData)
        } catch (e: Exception) {
            BackupResult.Error("Falha ao exportar dados para planilha", e)
        }
    }

    private fun convertToSpreadsheetData(transactions: List<Transaction>): SpreadsheetData {
        val headers = listOf(
            "ID",
            "Valor (centavos)",
            "Tipo",
            "Categoria",
            "Descrição",
            "Data",
            "Recorrente",
            "Criado em",
            "Mês Destino",
            "Ano Destino",
        )

        val rows = transactions.map { transaction ->
            listOf(
                transaction.id.toString(),
                transaction.amountCents.toString(),
                when (transaction.type) {
                    TransactionType.INCOME -> "Receita"
                    TransactionType.EXPENSE -> "Despesa"
                },
                transaction.category ?: "",
                transaction.description ?: "",
                transaction.humanDate,
                if (transaction.isRecurring) "Sim" else "Não",
                transaction.createdAt.toString(),
                transaction.targetMonth.toString(),
                transaction.targetYear.toString(),
            )
        }

        return SpreadsheetData(headers = headers, rows = rows)
    }
}
