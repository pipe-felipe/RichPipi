package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
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
            // First check if authenticated
            if (!backupRepository.isAuthenticated(service)) {
                // Try to authenticate
                val authResult = backupRepository.authenticate(service)
                if (authResult is BackupResult.SignInRequired) {
                    return ListBackupsResult.SignInRequired
                }
                if (authResult is BackupResult.Error) {
                    return ListBackupsResult.Error(authResult.message)
                }
            }

            val spreadsheets = backupRepository.listBackupSpreadsheets(service, folderName)
            ListBackupsResult.Success(spreadsheets)
        } catch (e: Exception) {
            ListBackupsResult.Error("Falha ao listar backups: ${e.message}")
        }
    }
}
