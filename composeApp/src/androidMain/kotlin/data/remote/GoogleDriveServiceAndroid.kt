package data.remote

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import data.local.EncryptedCredentialsManager
import domain.model.BackupResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of GoogleDriveService using Google Drive API v3.
 * Credentials are stored securely using EncryptedSharedPreferences.
 */
class GoogleDriveServiceAndroid(
    private val context: Context,
) : GoogleDriveService {

    private val credentialsManager = EncryptedCredentialsManager(context)
    private var driveService: Drive? = null
    private var isAuthenticatedFlag = false
    private var googleSignInClient: GoogleSignInClient

    init {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, signInOptions)
    }

    /**
     * Returns the sign-in intent for launching the Google Sign-In flow.
     * The calling Activity should use this with startActivityForResult.
     */
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    /**
     * Handles the result from the Google Sign-In flow.
     * Call this from onActivityResult with the data Intent.
     */
    suspend fun handleSignInResult(data: Intent?): BackupResult {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.result
            if (account != null) {
                setupDriveService(account)
                BackupResult.Success
            } else {
                BackupResult.Error("Failed to get Google account from sign-in result")
            }
        } catch (e: Exception) {
            BackupResult.Error("Google Sign-In failed: ${e.message}", e)
        }
    }

    private suspend fun setupDriveService(account: GoogleSignInAccount) {
        withContext(Dispatchers.IO) {
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                listOf(DriveScopes.DRIVE_FILE),
            )
            credential.selectedAccount = account.account

            driveService = Drive.Builder(
                NetHttpTransport(),
                GsonFactory(),
                credential,
            )
                .setApplicationName("RichPipi")
                .build()

            isAuthenticatedFlag = true
        }
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
            // Check if user is already signed in
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null && GoogleSignIn.hasPermissions(account, Scope(DriveScopes.DRIVE_FILE))) {
                setupDriveService(account)
                BackupResult.Success
            } else {
                // User needs to sign in - return SignInRequired
                // The UI layer should call getSignInIntent() and launch the sign-in flow
                BackupResult.SignInRequired
            }
        } catch (e: Exception) {
            BackupResult.Error("Failed to authenticate with Google Drive: ${e.message}", e)
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut(): BackupResult {
        return try {
            googleSignInClient.signOut()
            driveService = null
            isAuthenticatedFlag = false
            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error("Failed to sign out: ${e.message}", e)
        }
    }
}
