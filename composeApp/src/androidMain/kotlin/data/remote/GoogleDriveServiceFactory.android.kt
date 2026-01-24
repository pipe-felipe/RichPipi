package data.remote

import android.content.Context

/**
 * Android implementation of GoogleDriveService factory.
 */
private lateinit var applicationContext: Context

fun initializeGoogleDriveService(context: Context) {
    applicationContext = context.applicationContext
}

actual fun createGoogleDriveService(): GoogleDriveService {
    return GoogleDriveServiceAndroid(applicationContext)
}
