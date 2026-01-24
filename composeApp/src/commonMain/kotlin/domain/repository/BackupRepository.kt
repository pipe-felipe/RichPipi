package domain.repository

import domain.model.BackupResult
import domain.model.BackupService

/**
 * Repository interface for backup operations.
 * Following Clean Architecture, this interface is defined in the domain layer
 * and implemented in the data layer.
 */
interface BackupRepository {
    /**
     * Creates a backup folder in the specified service.
     * @param service The backup service to use
     * @param folderName Name of the folder to create
     * @return Result of the backup operation
     */
    suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult

    /**
     * Checks if the user is authenticated with the backup service.
     * @param service The backup service to check
     * @return true if authenticated, false otherwise
     */
    suspend fun isAuthenticated(service: BackupService): Boolean

    /**
     * Authenticates with the backup service.
     * @param service The backup service to authenticate with
     * @return Result of the authentication operation
     */
    suspend fun authenticate(service: BackupService): BackupResult
}
