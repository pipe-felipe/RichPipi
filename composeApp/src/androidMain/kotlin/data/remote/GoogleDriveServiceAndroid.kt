package data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import domain.model.BackupResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of GoogleDriveService using Google Drive API v3.
 */
class GoogleDriveServiceAndroid(
    private val context: Context
) : GoogleDriveService {

    private val credentialManager = CredentialManager.create(context)
    private var driveService: Drive? = null
    private var isAuthenticatedFlag = false

    companion object {
        private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID" // TODO: Replace with your actual web client ID
    }

    override suspend fun createFolder(folderName: String): BackupResult {
        return try {
            withContext(Dispatchers.IO) {
                val service = driveService ?: return@withContext BackupResult.Error("Not authenticated")

                // Check if folder already exists
                val existing = service.files().list()
                    .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .execute()

                if (existing.files.isNotEmpty()) {
                    return@withContext BackupResult.Success
                }

                // Create new folder
                val folder = File().apply {
                    name = folderName
                    mimeType = "application/vnd.google-apps.folder"
                }

                service.files().create(folder).execute()
                BackupResult.Success
            }
        } catch (e: Exception) {
            BackupResult.Error("Failed to create folder in Google Drive", e)
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return isAuthenticatedFlag && driveService != null
    }

    override suspend fun authenticate(): BackupResult {
        return try {
            withContext(Dispatchers.Main) {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response: GetCredentialResponse = credentialManager.getCredential(context, request)
                val credential = GoogleIdTokenCredential.createFrom(response.credential.data)

                // Configure Google Drive service with proper OAuth2 scope
                val googleCredential = GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(DriveScopes.DRIVE_FILE)
                )

                // Note: You'll need to implement token exchange here
                // For now, we'll use a simplified approach
                driveService = Drive.Builder(
                    NetHttpTransport(),
                    GsonFactory(),
                    googleCredential
                )
                    .setApplicationName("RichPipi")
                    .build()

                isAuthenticatedFlag = true
                BackupResult.Success
            }
        } catch (e: Exception) {
            BackupResult.Error("Failed to authenticate with Google Drive: ${e.message}", e)
        }
    }
}
