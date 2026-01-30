package domain.usecase

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import domain.repository.TransactionRepository
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteItemUseCaseTest {

    @Test
    fun `delete usecase delegates to repository`() = runBlocking {
        val deleteCapture = Capture.slot<Int>()

        val repo = mock<TransactionRepository> {
            everySuspend { deleteTransaction(capture(deleteCapture)) } returns 1
        }

        val uc = DeleteTransactionUseCase(repo)
        val result = uc(42)

        assertEquals(1, result)
        assertEquals(42, deleteCapture.get())
    }
}
