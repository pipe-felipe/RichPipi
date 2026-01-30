package domain.usecase

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.emptyFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTransactionsForMonthTest {

    @Test
    fun `invoke delegates parameters to repository`() {
        val startCapture = Capture.slot<Long>()
        val endCapture = Capture.slot<Long>()

        val repo = mock<TransactionRepository> {
            everySuspend { getTransactionsForMonth(capture(startCapture), capture(endCapture)) } returns emptyFlow()
        }

        val usecase = GetTransactionsForMonth(repo)

        val result = usecase(monthStartMillis = 100L, monthEndExclusiveMillis = 200L)
        // Touch the value so the compiler doesn't complain about an unused Flow.
        result.hashCode()

        assertEquals(100L, startCapture.get())
        assertEquals(200L, endCapture.get())
    }
}
