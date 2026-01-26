package domain.model

/**
 * Represents the result of an import operation.
 */
sealed class ImportResult {
    data class Success(val importedCount: Int) : ImportResult()
    data class Error(val message: String, val cause: Throwable? = null) : ImportResult()
    data object SignInRequired : ImportResult()
    data object NoBackupsFound : ImportResult()
}
