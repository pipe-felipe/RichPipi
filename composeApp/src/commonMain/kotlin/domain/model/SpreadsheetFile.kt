package domain.model

/**
 * Represents a spreadsheet file in Google Drive.
 */
data class SpreadsheetFile(
    val id: String,
    val name: String,
    val createdTime: String? = null,
)
