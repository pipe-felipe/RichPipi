package data.remote

import android.content.Context
import java.lang.ref.WeakReference

/**
 * Android implementation of GoogleDriveService factory.
 */
private lateinit var applicationContext: Context
private var googleDriveServiceInstanceRef: WeakReference<GoogleDriveServiceAndroid>? = null

fun initializeGoogleDriveService(context: Context) {
    applicationContext = context.applicationContext
    // Pre-create and initialize the service using Application context to avoid leaks
    val service = GoogleDriveServiceAndroid(applicationContext)
    googleDriveServiceInstanceRef = WeakReference(service)
    GoogleSignInHandler.initialize(service)
}

actual fun createGoogleDriveService(): GoogleDriveService {
    val existing = googleDriveServiceInstanceRef?.get()
    return existing ?: GoogleDriveServiceAndroid(applicationContext).also {
        googleDriveServiceInstanceRef = WeakReference(it)
        GoogleSignInHandler.initialize(it)
    }
}
