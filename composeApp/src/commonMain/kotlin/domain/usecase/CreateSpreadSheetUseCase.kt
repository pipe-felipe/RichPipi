package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository

class CreateSpreadSheetUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend fun execute(): BackupResult {
        val service = BackupService.GoogleDrive
        val folderName = BackupConstants.getBackupFolderWithTimestamp()
        val spreadsheetName = BackupConstants.getBackupSpreadsheetWithTimestamp()

        return try {
            if (!backupRepository.isAuthenticated(service)) {
                val authResult = backupRepository.authenticate(service)
                if (authResult is BackupResult.Error || authResult is BackupResult.SignInRequired) {
                    return authResult
                }
            }

            // Ensure folder exists
            val folderResult = backupRepository.createBackupFolder(service, folderName)
            if (folderResult is BackupResult.Error) {
                return folderResult
            }

            // Create the spreadsheet inside the folder
            backupRepository.createSpreadsheet(service, folderName, spreadsheetName)
        } catch (e: Exception) {
            BackupResult.Error("Failed to create spreadsheet", e)
        }
    }
}
