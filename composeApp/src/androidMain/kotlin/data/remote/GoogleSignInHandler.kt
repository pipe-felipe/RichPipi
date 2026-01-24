package data.remote

import android.content.Intent
import domain.model.BackupResult
import java.lang.ref.WeakReference

/**
 * Singleton handler for Google Sign-In operations.
 * This allows the UI layer to trigger sign-in and receive results.
 *
 * NOTE: We avoid holding a strong static reference to a platform object
 * (GoogleDriveServiceAndroid) because it may contain an Activity Context and
 * cause memory leaks. We keep a WeakReference and expose a clearService()
 * helper. Prefer providing a service backed by the Application context.
 */
object GoogleSignInHandler {

    private var driveServiceRef: WeakReference<GoogleDriveServiceAndroid>? = null
    private var signInResultCallback: ((BackupResult) -> Unit)? = null

    /**
     * Initializes the handler with the GoogleDriveServiceAndroid instance.
     * Prefer passing a service that uses the Application context to avoid leaks.
     */
    fun initialize(service: GoogleDriveServiceAndroid) {
        driveServiceRef = WeakReference(service)
    }

    /**
     * Clears any stored reference to the service. Call this when the service is
     * no longer needed (for example on sign-out or when the hosting UI is
     * destroyed) to help the GC reclaim any referenced Context.
     */
    @Suppress("unused")
    fun clearService() {
        driveServiceRef?.clear()
        driveServiceRef = null
    }

    /**
     * Returns the sign-in intent to launch.
     * Returns null if the service is not initialized.
     */
    fun getSignInIntent(): Intent? {
        return driveServiceRef?.get()?.getSignInIntent()
    }

    /**
     * Sets a callback to receive the sign-in result.
     */
    @Suppress("unused")
    fun setSignInResultCallback(callback: (BackupResult) -> Unit) {
        signInResultCallback = callback
    }

    /**
     * Handles the result from the Google Sign-In activity.
     * Call this from the Activity's onActivityResult or ActivityResultLauncher callback.
     */
    suspend fun handleSignInResult(data: Intent?): BackupResult {
        val result = driveServiceRef?.get()?.handleSignInResult(data)
            ?: BackupResult.Error("Google Drive service not initialized")
        signInResultCallback?.invoke(result)
        signInResultCallback = null
        return result
    }

    /**
     * Clears the callback (useful when the UI is dismissed).
     */
    @Suppress("unused")
    fun clearCallback() {
        signInResultCallback = null
    }
}
