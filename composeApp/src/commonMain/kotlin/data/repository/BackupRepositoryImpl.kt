package data.repository

import data.remote.GoogleDriveService
import domain.model.BackupResult
import domain.model.BackupService
import domain.model.SpreadsheetData
import domain.model.SpreadsheetFile
import domain.repository.BackupRepository

/**
 * Implementation of BackupRepository.
 * This class coordinates between different backup services.
 */
class BackupRepositoryImpl(
    private val googleDriveService: GoogleDriveService,
) : BackupRepository {

    override suspend fun createBackupFolder(service: BackupService, folderName: String): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.createFolder(folderName)
            }
        }
    }

    override suspend fun createSpreadsheet(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
    ): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.createSpreadsheet(folderName, spreadsheetName)
            }
        }
    }

    override suspend fun createSpreadsheetWithData(
        service: BackupService,
        folderName: String,
        spreadsheetName: String,
        data: SpreadsheetData,
    ): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.createSpreadsheetWithData(folderName, spreadsheetName, data)
            }
        }
    }

    override suspend fun listBackupSpreadsheets(
        service: BackupService,
        folderName: String,
    ): List<SpreadsheetFile> {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.listSpreadsheets(folderName)
            }
        }
    }

    override suspend fun readSpreadsheetData(
        service: BackupService,
        spreadsheetId: String,
    ): SpreadsheetData? {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.readSpreadsheetData(spreadsheetId)
            }
        }
    }

    override suspend fun isAuthenticated(service: BackupService): Boolean {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.isAuthenticated()
            }
        }
    }

    override suspend fun authenticate(service: BackupService): BackupResult {
        return when (service) {
            is BackupService.GoogleDrive -> {
                googleDriveService.authenticate()
            }
        }
    }
}
