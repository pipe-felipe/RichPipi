package di

import data.remote.createGoogleDriveService
import data.repository.BackupRepositoryImpl
import domain.repository.BackupRepository
import domain.repository.TransactionRepository
import domain.usecase.CreateSpreadSheetUseCase
import domain.usecase.ExportDataToSpreadsheetUseCase

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

    /**
     * Creates an ExportDataToSpreadsheetUseCase with the given TransactionRepository.
     * This must be called with the repository instance from the data layer.
     */
    fun createExportDataToSpreadsheetUseCase(
        transactionRepository: TransactionRepository,
    ): ExportDataToSpreadsheetUseCase {
        return ExportDataToSpreadsheetUseCase(backupRepository, transactionRepository)
    }
}
