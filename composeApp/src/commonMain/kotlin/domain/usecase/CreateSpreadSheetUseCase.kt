package domain.usecase

import domain.BackupConstants
import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository

class CreateSpreadSheetUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend fun execute(nowEpochDate: Long): BackupResult {
        val service = BackupService.GoogleDrive
        val folderName = BackupConstants.DEFAULT_FOLDER_NAME
        val spreadsheetName = BackupConstants.getBackupSpreadsheetWithTimestamp(nowEpochDate)

        return try {
            if (!backupRepository.isAuthenticated(service)) {
                return BackupResult.SignInRequired
            }

            // Ensure folder exists
            val folderResult = backupRepository.createBackupFolder(service, folderName)
            if (folderResult is BackupResult.Error) {
                return folderResult
            }

            // Create the spreadsheet inside the folder
            backupRepository.createSpreadsheet(service, folderName, spreadsheetName)
        } catch (e: Exception) {
            BackupResult.Error("Falha ao criar planilha", e)
        }
    }
}
