package data.remote

import domain.model.BackupResult

/**
 * Interface for Google Drive operations.
 * This interface is implemented differently on each platform (Android, iOS).
 */
interface GoogleDriveService {
    /**
     * Creates a folder in Google Drive.
     * @param folderName The name of the folder to create
     * @return BackupResult indicating success or failure
     */
    suspend fun createFolder(folderName: String): BackupResult

    /**
     * Creates a Spreadsheet in Google Drive.
     * @param folderName The name of the folder to create the spreadsheet in
     * @param spreadsheetName The name of the spreadsheet to create
     * @return BackupResult indicating success or failure
     */
    suspend fun createSpreadsheet(folderName: String, spreadsheetName: String): BackupResult

    /**
     * Checks if the user is authenticated with Google Drive.
     * @return true if authenticated, false otherwise
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Authenticates the user with Google Drive.
     * @return BackupResult indicating success or failure
     */
    suspend fun authenticate(): BackupResult
}
