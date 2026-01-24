package domain.model

/**
 * Represents the result of a backup operation.
 */
sealed class BackupResult {
    data object Success : BackupResult()
    data class Error(val message: String, val cause: Throwable? = null) : BackupResult()
}
