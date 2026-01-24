package di

import data.remote.createGoogleDriveService
import data.repository.BackupRepositoryImpl
import domain.repository.BackupRepository
import domain.usecase.CreateSpreadSheetUseCase

/**
 * Simple dependency injection container for backup functionality.
 */
object BackupModule {

    private val googleDriveService by lazy { createGoogleDriveService() }

    private val backupRepository: BackupRepository by lazy {
        BackupRepositoryImpl(googleDriveService)
    }

    val createSpreadSheetUseCase: CreateSpreadSheetUseCase by lazy {
        CreateSpreadSheetUseCase(backupRepository)
    }
}
