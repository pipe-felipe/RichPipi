package domain.model

/**
 * Represents data to be written to a spreadsheet.
 * This is a domain model that abstracts the data format.
 */
data class SpreadsheetData(
    val headers: List<String>,
    val rows: List<List<String>>,
)
