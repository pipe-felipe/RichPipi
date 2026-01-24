package data.remote

import domain.model.BackupResult

/**
 * iOS implementation of GoogleDriveService.
 * This is a placeholder implementation that can be extended with iOS-specific Google Drive SDK.
 */
class GoogleDriveServiceIOS : GoogleDriveService {

    override suspend fun createFolder(folderName: String): BackupResult {
        // TODO: Implement iOS Google Drive integration
        return BackupResult.Error("Google Drive not yet implemented for iOS")
    }

    override suspend fun isAuthenticated(): Boolean {
        // TODO: Implement iOS authentication check
        return false
    }

    override suspend fun authenticate(): BackupResult {
        // TODO: Implement iOS authentication
        return BackupResult.Error("Google Drive authentication not yet implemented for iOS")
    }
}
