package data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages secure storage of sensitive credentials using EncryptedSharedPreferences.
 * All data is automatically encrypted at rest using Android's security library.
 */
class EncryptedCredentialsManager(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /**
     * Stores the Google Web Client ID securely.
     */
    fun storeGoogleWebClientId(clientId: String) {
        encryptedSharedPreferences.edit { putString(KEY_GOOGLE_WEB_CLIENT_ID, clientId) }
    }

    /**
     * Retrieves the Google Web Client ID.
     * @return The stored client ID, or null if not set.
     */
    fun getGoogleWebClientId(): String? {
        return encryptedSharedPreferences.getString(KEY_GOOGLE_WEB_CLIENT_ID, null)
    }

    /**
     * Stores a generic credential securely.
     */
    fun storeCredential(key: String, value: String) {
        encryptedSharedPreferences.edit { putString(key, value) }
    }

    /**
     * Retrieves a generic credential.
     */
    fun getCredential(key: String): String? {
        return encryptedSharedPreferences.getString(key, null)
    }

    /**
     * Removes a credential.
     */
    fun removeCredential(key: String) {
        encryptedSharedPreferences.edit { remove(key) }
    }

    /**
     * Clears all stored credentials.
     */
    fun clearAllCredentials() {
        encryptedSharedPreferences.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "encrypted_credentials"
        private const val KEY_GOOGLE_WEB_CLIENT_ID = "google_web_client_id"
    }
}
