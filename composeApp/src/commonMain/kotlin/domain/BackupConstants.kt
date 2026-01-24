package domain

import kotlin.time.Clock

object BackupConstants {

    const val DEFAULT_FOLDER_NAME = "rich-pipi-backup"
    const val DEFAULT_SPREADSHEET_NAME = "rich-pipi-backup-sheet"

    fun getCurrentDateTime(): String {
        val currentTimeMillis = Clock.System.now().toEpochMilliseconds()

        val totalSeconds = currentTimeMillis / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val totalDays = totalHours / 24

        val year = 1970 + (totalDays / 365).toInt()
        val dayOfYear = (totalDays % 365).toInt()

        val month = (dayOfYear / 30) + 1
        val day = (dayOfYear % 30) + 1

        val hour = (totalHours % 24).toInt()
        val minute = (totalMinutes % 60).toInt()

        return "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    fun getBackupFolderWithTimestamp(): String {
        val timestamp = getCurrentDateTime()
            .replace("/", "-")
            .replace(":", "-")
            .replace(" ", "_")
        return "${DEFAULT_FOLDER_NAME}_$timestamp"
    }

    fun getBackupSpreadsheetWithTimestamp(): String {
        val timestamp = getCurrentDateTime()
            .replace("/", "-")
            .replace(":", "-")
            .replace(" ", "_")
        return "${DEFAULT_SPREADSHEET_NAME}_$timestamp"
    }
}
