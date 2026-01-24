package di

import data.remote.createGoogleDriveService
import data.repository.BackupRepositoryImpl
import domain.repository.BackupRepository
import domain.usecase.CreateBackupFolderUseCase

/**
 * Simple dependency injection container for backup functionality.
 */
object BackupModule {

    private val googleDriveService by lazy { createGoogleDriveService() }

    private val backupRepository: BackupRepository by lazy {
        BackupRepositoryImpl(googleDriveService)
    }

    val createBackupFolderUseCase: CreateBackupFolderUseCase by lazy {
        CreateBackupFolderUseCase(backupRepository)
    }
}
