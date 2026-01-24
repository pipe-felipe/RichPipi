package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository

/**
 * Use case for creating a backup folder in Google Drive.
 * This use case handles the business logic for backup operations.
 */
class CreateBackupFolderUseCase(
    private val backupRepository: BackupRepository,
) {
    /**
     * Creates a "rich-pipi-backup" folder in Google Drive.
     * If the user is not authenticated, it will attempt to authenticate first.
     *
     * @return BackupResult indicating success or failure
     */
    suspend fun execute(): BackupResult {
        val service = BackupService.GoogleDrive
        val folderName = "rich-pipi-backup"

        return try {
            // Check if user is authenticated
            if (!backupRepository.isAuthenticated(service)) {
                // Attempt to authenticate first
                val authResult = backupRepository.authenticate(service)
                if (authResult is BackupResult.Error || authResult is BackupResult.SignInRequired) {
                    return authResult
                }
            }

            // Create the backup folder
            backupRepository.createBackupFolder(service, folderName)
        } catch (e: Exception) {
            BackupResult.Error("Failed to create backup folder", e)
        }
    }
}
