package domain

object BackupConstants {

    const val DEFAULT_FOLDER_NAME = "rich-pipi-backup"
    private const val DEFAULT_SPREADSHEET_NAME = "rich-pipi-backup-sheet"

    fun getCurrentDateTime(clockEpoch: Long): String {
        val totalSeconds = clockEpoch / 1000
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

    fun getBackupSpreadsheetWithTimestamp(clockEpoch: Long): String {
        val timestamp = getCurrentDateTime(clockEpoch)
            .replace("/", "-")
            .replace(":", "-")
            .replace(" ", "_")
        return "${DEFAULT_SPREADSHEET_NAME}_$timestamp"
    }
}
