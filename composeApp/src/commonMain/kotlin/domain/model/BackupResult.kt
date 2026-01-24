package domain.model

/**
 * Represents the result of a backup operation.
 */
sealed class BackupResult {
    data object Success : BackupResult()
    data class Error(val message: String, val cause: Throwable? = null) : BackupResult()

    /**
     * Indicates that user sign-in is required to proceed.
     * The UI layer should handle this by launching the appropriate sign-in flow.
     */
    data object SignInRequired : BackupResult()
}
