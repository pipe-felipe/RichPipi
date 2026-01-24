package data.remote

/**
 * Factory interface for creating platform-specific GoogleDriveService instances.
 */
expect fun createGoogleDriveService(): GoogleDriveService
