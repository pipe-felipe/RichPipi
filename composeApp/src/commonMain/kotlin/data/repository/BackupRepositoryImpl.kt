package data.repository

import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository
import data.remote.GoogleDriveService

/**
 * Implementation of BackupRepository.
 * This class coordinates between different backup services.
 */
class BackupRepositoryImpl(
    private val googleDriveService: GoogleDriveService
) : BackupRepository {

    override suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.createFolder(folderName)
            }
        }
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.isAuthenticated()
            }
        }
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.authenticate()
            }
        }
    }
}
