package domain.model

/**
 * Domain model representing a cloud backup service.
 */
sealed class BackupService {
    data object GoogleDrive : BackupService()
}
