package domain.usecase

import domain.model.BackupResult
import domain.model.BackupService
import domain.repository.BackupRepository

/**
 * Result of authentication operation.
 */
sealed class AuthResult {
    data class Success(val userName: String?) : AuthResult()
    data object SignInRequired : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Use case for authenticating with the backup service and getting user info.
 */
class AuthenticateUseCase(
    private val backupRepository: BackupRepository,
) {
    suspend fun execute(): AuthResult {
        val service = BackupService.GoogleDrive

        return try {
            // First check if already authenticated
            if (backupRepository.isAuthenticated(service)) {
                val userName = backupRepository.getAuthenticatedUserName(service)
                return AuthResult.Success(userName)
            }

            // Try to authenticate
            when (val result = backupRepository.authenticate(service)) {
                is BackupResult.Success -> {
                    val userName = backupRepository.getAuthenticatedUserName(service)
                    AuthResult.Success(userName)
                }
                is BackupResult.SignInRequired -> {
                    AuthResult.SignInRequired
                }
                is BackupResult.Error -> {
                    AuthResult.Error(result.message)
                }
            }
        } catch (e: Exception) {
            AuthResult.Error("Falha na autenticação: ${e.message}")
        }
    }

    /**
     * Gets the currently authenticated user's name without triggering authentication.
     */
    suspend fun getCurrentUserName(): String? {
        val service = BackupService.GoogleDrive
        return try {
            backupRepository.getAuthenticatedUserName(service)
        } catch (e: Exception) {
            null
        }
    }
}
