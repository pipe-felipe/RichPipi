package data.remote

import android.content.Context

/**
 * Android implementation of GoogleDriveService factory.
 */
private lateinit var applicationContext: Context
private var googleDriveServiceInstance: GoogleDriveServiceAndroid? = null

fun initializeGoogleDriveService(context: Context) {
    applicationContext = context.applicationContext
    // Pre-create and initialize the service using Application context to avoid leaks
    val service = GoogleDriveServiceAndroid(applicationContext)
    googleDriveServiceInstance = service
    GoogleSignInHandler.initialize(service)
}

actual fun createGoogleDriveService(): GoogleDriveService {
    return googleDriveServiceInstance ?: GoogleDriveServiceAndroid(applicationContext).also {
        googleDriveServiceInstance = it
        GoogleSignInHandler.initialize(it)
    }
}
