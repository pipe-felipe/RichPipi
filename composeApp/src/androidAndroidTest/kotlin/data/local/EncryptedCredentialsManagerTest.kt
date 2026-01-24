package data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for EncryptedCredentialsManager.
 * Tests secure storage and retrieval of credentials.
 */
class EncryptedCredentialsManagerTest {

    private lateinit var context: Context
    private lateinit var credentialsManager: EncryptedCredentialsManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        credentialsManager = EncryptedCredentialsManager(context)
        // Clear credentials before each test
        credentialsManager.clearAllCredentials()
    }

    @Test
    fun testStoreAndRetrieveGoogleWebClientId() {
        // Arrange
        val clientId = "123456789-abc.apps.googleusercontent.com"

        // Act
        credentialsManager.storeGoogleWebClientId(clientId)
        val retrieved = credentialsManager.getGoogleWebClientId()

        // Assert
        assertEquals(clientId, retrieved)
    }

    @Test
    fun testRetrieveNonExistentWebClientId() {
        // Act
        val retrieved = credentialsManager.getGoogleWebClientId()

        // Assert
        assertNull(retrieved)
    }

    @Test
    fun testStoreAndRetrieveGenericCredential() {
        // Arrange
        val key = "api_token"
        val value = "secret_token_123"

        // Act
        credentialsManager.storeCredential(key, value)
        val retrieved = credentialsManager.getCredential(key)

        // Assert
        assertEquals(value, retrieved)
    }

    @Test
    fun testUpdateCredential() {
        // Arrange
        val key = "api_token"
        val value1 = "token_v1"
        val value2 = "token_v2"

        // Act
        credentialsManager.storeCredential(key, value1)
        credentialsManager.storeCredential(key, value2)
        val retrieved = credentialsManager.getCredential(key)

        // Assert
        assertEquals(value2, retrieved)
    }

    @Test
    fun testRemoveCredential() {
        // Arrange
        val key = "api_token"
        val value = "secret_token_123"
        credentialsManager.storeCredential(key, value)

        // Act
        credentialsManager.removeCredential(key)
        val retrieved = credentialsManager.getCredential(key)

        // Assert
        assertNull(retrieved)
    }

    @Test
    fun testClearAllCredentials() {
        // Arrange
        credentialsManager.storeGoogleWebClientId("client_id")
        credentialsManager.storeCredential("key1", "value1")
        credentialsManager.storeCredential("key2", "value2")

        // Act
        credentialsManager.clearAllCredentials()

        // Assert
        assertNull(credentialsManager.getGoogleWebClientId())
        assertNull(credentialsManager.getCredential("key1"))
        assertNull(credentialsManager.getCredential("key2"))
    }

    @Test
    fun testMultipleCredentialsIndependence() {
        // Arrange
        val key1 = "token1"
        val value1 = "value1"
        val key2 = "token2"
        val value2 = "value2"

        // Act
        credentialsManager.storeCredential(key1, value1)
        credentialsManager.storeCredential(key2, value2)

        // Assert
        assertEquals(value1, credentialsManager.getCredential(key1))
        assertEquals(value2, credentialsManager.getCredential(key2))
    }

    @Test
    fun testCredentialsArePersisted() {
        // Arrange
        val clientId = "123456789-abc.apps.googleusercontent.com"
        credentialsManager.storeGoogleWebClientId(clientId)

        // Act - Create a new instance to verify persistence
        val newManager = EncryptedCredentialsManager(context)
        val retrieved = newManager.getGoogleWebClientId()

        // Assert
        assertEquals(clientId, retrieved)
    }
}
