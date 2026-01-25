package data.remote

import domain.model.BackupResult
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile

/**
 * iOS implementation of GoogleDriveService.
 * This is a placeholder implementation that can be extended with iOS-specific Google Drive SDK.
 */
class GoogleDriveServiceIOS : GoogleDriveService {

    override suspend fun createFolder(folderName: String): BackupResult {
        return BackupResult.Error("Google Drive not yet implemented for iOS")
    }

    override suspend fun createSpreadsheet(
        folderName: String,
        spreadsheetName: String,
    ): BackupResult {
        return BackupResult.Error("Google Drive not yet implemented for iOS")
    }

    override suspend fun createSpreadsheetWithData(
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult {
        return BackupResult.Error("Google Drive not yet implemented for iOS")
    }

    override suspend fun listSpreadsheets(folderName: String): List<SpreadsheetFile> {
        return emptyList()
    }

    override suspend fun readSpreadsheetData(spreadsheetId: String): SpreadsheetData? {
        return null
    }

    override suspend fun isAuthenticated(): Boolean {
        return false
    }

    override suspend fun authenticate(): BackupResult {
        return BackupResult.Error("Google Drive authentication not yet implemented for iOS")
    }
}
