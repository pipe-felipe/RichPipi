package data.local.entity

import domain.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `transaction type converters round trip`() {
        for (type in TransactionType.entries) {
            val encoded = converters.fromTransactionType(type)
            val decoded = converters.toTransactionType(encoded)
            assertEquals(type, decoded)
        }
    }

    @Test
    fun `transaction type converters handle null`() {
        assertNull(converters.fromTransactionType(null))
        assertNull(converters.toTransactionType(null))
    }
}
