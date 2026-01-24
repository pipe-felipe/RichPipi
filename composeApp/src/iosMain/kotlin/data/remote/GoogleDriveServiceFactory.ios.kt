package data.remote

/**
 * iOS implementation of GoogleDriveService factory.
 */
actual fun createGoogleDriveService(): GoogleDriveService {
    return GoogleDriveServiceIOS()
}
