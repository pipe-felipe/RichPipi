package domain.usecase

import domain.BackupConstants
import domain.model.BackupService
import domain.model.SpreadsheetFile
import domain.repository.BackupRepository

/**
 * Result of listing backup spreadsheets.
 */
sealed class ListBackupsResult {
    data class Success(val spreadsheets: List<SpreadsheetFile>) : ListBackupsResult()
    data object SignInRequired : ListBackupsResult()
    data class Error(val message: String) : ListBackupsResult()
}

/**
 * Use case for listing available backup spreadsheets from Google Drive.
 */
class ListBackupSpreadsheetsUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend fun execute(): ListBackupsResult {
        val service = BackupService.GoogleDrive
        val folderName = BackupConstants.DEFAULT_FOLDER_NAME

        return try {
            // Check if authenticated - if not, return error immediately
            if (!backupRepository.isAuthenticated(service)) {
                return ListBackupsResult.SignInRequired
            }

            val spreadsheets = backupRepository.listBackupSpreadsheets(service, folderName)
            ListBackupsResult.Success(spreadsheets)
        } catch (e: Exception) {
            ListBackupsResult.Error("Falha ao listar backups: ${e.message}")
        }
    }
}
