package data.remote

import android.content.Context
import data.local.EncryptedCredentialsManager
import domain.model.BackupResult

/**
 * Example of how to initialize credentials at app startup.
 *
 * This should be called once during app initialization (e.g., in Application.onCreate()
 * or during first-time setup).
 */
object CredentialsInitializer {

    /**
     * Initialize Google credentials from secure storage or setup if first time.
     *
     * @param context Android context
     * @param webClientId Optional web client ID to store (if null, attempts to retrieve from storage)
     * @return Result of initialization
     */
    fun initializeGoogleCredentials(
        context: Context,
        webClientId: String? = null,
    ): BackupResult {
        return try {
            val credentialsManager = EncryptedCredentialsManager(context)

            // If web client ID is provided, store it securely
            if (webClientId != null) {
                credentialsManager.storeGoogleWebClientId(webClientId)
                return BackupResult.Success
            }

            // Check if credentials already exist
            val storedClientId = credentialsManager.getGoogleWebClientId()
            if (storedClientId != null) {
                return BackupResult.Success
            }

            // Credentials not configured
            BackupResult.Error(
                "Google Web Client ID not configured. " +
                    "Call CredentialsInitializer.initializeGoogleCredentials(context, clientId) " +
                    "with your Web Client ID from Google Cloud Console.",
            )
        } catch (e: Exception) {
            BackupResult.Error("Failed to initialize credentials: ${e.message}", e)
        }
    }
}
