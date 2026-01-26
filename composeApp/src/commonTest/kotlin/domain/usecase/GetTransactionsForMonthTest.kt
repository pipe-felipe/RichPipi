package domain.usecase

import domain.model.Transaction
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTransactionsForMonthTest {

    private class CapturingRepository : TransactionRepository {
        var lastStart: Long? = null
        var lastEndExclusive: Long? = null

        override fun getTransactions(): Flow<List<Transaction>> = emptyFlow()

        override fun getTransactionsForMonth(
            monthStartMillis: Long,
            monthEndExclusiveMillis: Long,
        ): Flow<List<Transaction>> {
            lastStart = monthStartMillis
            lastEndExclusive = monthEndExclusiveMillis
            return emptyFlow()
        }

        override suspend fun makeTransaction(transaction: Transaction): Long = 0

        override suspend fun deleteTransaction(id: Int): Int = 0

        override suspend fun deleteAllTransactions(): Int = 0

        override suspend fun insertTransactions(transactions: List<Transaction>): List<Long> = emptyList()
    }

    @Test
    fun `invoke delegates parameters to repository`() {
        val repo = CapturingRepository()
        val usecase = GetTransactionsForMonth(repo)

        val result = usecase(monthStartMillis = 100L, monthEndExclusiveMillis = 200L)
        // Touch the value so the compiler doesn't complain about an unused Flow.
        result.hashCode()

        assertEquals(100L, repo.lastStart)
        assertEquals(200L, repo.lastEndExclusive)
    }
}
