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
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import data.local.EncryptedCredentialsManager
import domain.model.BackupResult
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile
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
    private var sheetsService: Sheets? = null
    private var isAuthenticatedFlag = false
    private var googleSignInClient: GoogleSignInClient

    init {
        val signInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(
                    Scope(DriveScopes.DRIVE_FILE),
                    Scope(SheetsScopes.SPREADSHEETS)
                )
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
                listOf(DriveScopes.DRIVE_FILE, SheetsScopes.SPREADSHEETS),
            )
            credential.selectedAccount = account.account

            val httpTransport = NetHttpTransport()
            val jsonFactory = GsonFactory()

            driveService = Drive.Builder(
                httpTransport,
                jsonFactory,
                credential,
            )
                .setApplicationName("RichPipi")
                .build()

            sheetsService = Sheets.Builder(
                httpTransport,
                jsonFactory,
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
                val service = driveService
                    ?: return@withContext BackupResult.Error("Not authenticated")

                val existing = service.files().list()
                    .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .execute()

                if (existing.files.isNotEmpty()) {
                    return@withContext BackupResult.Success
                }

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

    override suspend fun createSpreadsheet(
        folderName: String,
        spreadsheetName: String,
    ): BackupResult {
        return try {
            withContext(Dispatchers.IO) {
                val service = driveService
                    ?: return@withContext BackupResult.Error("Not authenticated")

                val existing = service.files().list()
                    .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .setFields("files(id)")
                    .execute()

                val folderId = if (existing.files.isNotEmpty()) {
                    existing.files[0].id
                } else {
                    val folder = File().apply {
                        name = folderName
                        mimeType = "application/vnd.google-apps.folder"
                    }
                    service.files().create(folder).setFields("id").execute().id
                }
                    ?: return@withContext BackupResult.Error("Failed to obtain folder id")

                val spreadsheet = File().apply {
                    name = spreadsheetName
                    mimeType = "application/vnd.google-apps.spreadsheet"
                    parents = listOf(folderId)
                }

                service.files().create(spreadsheet).execute()
                BackupResult.Success
            }
        } catch (e: Exception) {
            BackupResult.Error(
                "Failed to create spreadsheet in Google Drive",
                e
            )
        }
    }

    override suspend fun createSpreadsheetWithData(
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult {
        return try {
            withContext(Dispatchers.IO) {
                val driveApi = driveService
                    ?: return@withContext BackupResult.Error("Not authenticated")
                val sheetsApi = sheetsService
                    ?: return@withContext BackupResult.Error("Sheets service not initialized")

                val existing = driveApi.files().list()
                    .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .setFields("files(id)")
                    .execute()

                val folderId = if (existing.files.isNotEmpty()) {
                    existing.files[0].id
                } else {
                    val folder = File().apply {
                        name = folderName
                        mimeType = "application/vnd.google-apps.folder"
                    }
                    driveApi.files().create(folder).setFields("id").execute().id
                }
                    ?: return@withContext BackupResult.Error("Failed to obtain folder id")

                val spreadsheetProperties =
                    SpreadsheetProperties().setTitle(spreadsheetName)
                val spreadsheet =
                    Spreadsheet().setProperties(spreadsheetProperties)
                val createdSpreadsheet =
                    sheetsApi.spreadsheets().create(spreadsheet).execute()
                val spreadsheetId = createdSpreadsheet.spreadsheetId
                    ?: return@withContext BackupResult.Error("Failed to create spreadsheet")

                val file =
                    driveApi.files().get(spreadsheetId).setFields("parents")
                        .execute()
                val previousParents = file.parents?.joinToString(",") ?: ""
                driveApi.files().update(spreadsheetId, null)
                    .setAddParents(folderId)
                    .setRemoveParents(previousParents)
                    .setFields("id, parents")
                    .execute()

                val allData = mutableListOf<List<Any>>()
                allData.add(data.headers)
                data.rows.forEach { row -> allData.add(row) }

                val body = ValueRange().setValues(allData)
                sheetsApi.spreadsheets().values()
                    .update(spreadsheetId, "A1", body)
                    .setValueInputOption("RAW")
                    .execute()

                BackupResult.Success
            }
        } catch (e: Exception) {
            BackupResult.Error(
                "Failed to create spreadsheet with data in Google Drive",
                e
            )
        }
    }

    override suspend fun listSpreadsheets(folderName: String): List<SpreadsheetFile> {
        return try {
            withContext(Dispatchers.IO) {
                val driveApi = driveService ?: return@withContext emptyList()

                val folderResult = driveApi.files().list()
                    .setQ("name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                    .setFields("files(id)")
                    .execute()

                if (folderResult.files.isNullOrEmpty()) {
                    return@withContext emptyList()
                }

                val folderId = folderResult.files[0].id

                val spreadsheetResult = driveApi.files().list()
                    .setQ("'$folderId' in parents and mimeType='application/vnd.google-apps.spreadsheet' and trashed=false")
                    .setFields("files(id, name, createdTime)")
                    .setOrderBy("createdTime desc")
                    .execute()

                spreadsheetResult.files?.map { file ->
                    SpreadsheetFile(
                        id = file.id,
                        name = file.name,
                        createdTime = file.createdTime?.toString(),
                    )
                } ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun readSpreadsheetData(spreadsheetId: String): SpreadsheetData? {
        return try {
            withContext(Dispatchers.IO) {
                val sheetsApi = sheetsService ?: return@withContext null

                val response = sheetsApi.spreadsheets().values()
                    .get(spreadsheetId, "A:Z")
                    .execute()

                val values = response.getValues() ?: return@withContext null

                if (values.isEmpty()) {
                    return@withContext SpreadsheetData(
                        headers = emptyList(),
                        rows = emptyList()
                    )
                }

                val headers = values[0].map { it?.toString() ?: "" }

                val rows = if (values.size > 1) {
                    values.drop(1).map { row ->
                        row.map { it?.toString() ?: "" }
                    }
                } else {
                    emptyList()
                }

                SpreadsheetData(headers = headers, rows = rows)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        if (isAuthenticatedFlag && driveService != null && sheetsService != null) {
            return true
        }

        return try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                try {
                    setupDriveService(account)
                    true
                } catch (setupError: Exception) {
                    println("Failed to setup drive service: ${setupError.message}")
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            println("isAuthenticated check failed: ${e.message}")
            false
        }
    }

    override suspend fun authenticate(): BackupResult {
        return try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null && GoogleSignIn.hasPermissions(
                    account,
                    Scope(DriveScopes.DRIVE_FILE),
                    Scope(SheetsScopes.SPREADSHEETS),
                )
            ) {
                setupDriveService(account)
                BackupResult.Success
            } else {
                BackupResult.SignInRequired
            }
        } catch (e: Exception) {
            BackupResult.Error(
                "Failed to authenticate with Google Drive: ${e.message}",
                e
            )
        }
    }

    override suspend fun getAuthenticatedUserName(): String? {
        return try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            account?.displayName ?: account?.email
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut(): BackupResult {
        return try {
            googleSignInClient.signOut()
            driveService = null
            sheetsService = null
            isAuthenticatedFlag = false
            BackupResult.Success
        } catch (e: Exception) {
            BackupResult.Error("Failed to sign out: ${e.message}", e)
        }
    }
}
