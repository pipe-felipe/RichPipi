package domain.repository

import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData

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
     * Creates a Spreadsheet in Google Drive.
     * @param service The backup service to check
     * @param folderName The name of the folder to create the spreadsheet in
     * @param spreadsheetName The name of the spreadsheet to create
     * @return BackupResult indicating success or failure
     */
    suspend fun createSpreadsheet(service: BackupService, folderName: String, spreadsheetName: String): BackupResult

    /**
     * Creates a Spreadsheet in Google Drive and writes data to it.
     * @param service The backup service to use
     * @param folderName The name of the folder to create the spreadsheet in
     * @param spreadsheetName The name of the spreadsheet to create
     * @param data The data to write to the spreadsheet
     * @return BackupResult indicating success or failure
     */
    suspend fun createSpreadsheetWithData(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult

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
