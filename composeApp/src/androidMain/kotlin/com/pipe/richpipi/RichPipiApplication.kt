package com.pipe.richpipi

import android.app.Application
import data.remote.CredentialsInitializer

/**
 * Application class for RichPipi.
 * Initializes secure credential storage on app startup.
 */
class RichPipiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        when (
            val result = CredentialsInitializer.initializeGoogleCredentials(
                context = this,
                webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
            )
        ) {
            is domain.model.BackupResult.Success -> {
                android.util.Log.d("RichPipi", "Credentials initialized successfully")
            }

            is domain.model.BackupResult.Error -> {
                android.util.Log.w(
                    "RichPipi",
                    "Credentials initialization error: ${result.message}",
                )
            }

            is domain.model.BackupResult.SignInRequired -> {
                android.util.Log.d("RichPipi", "Google Sign-In will be required for backup")
            }
        }
    }
}
