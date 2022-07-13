package com.ph.notestash.common.coroutines

import com.ph.notestash.common.coroutines.flow.collectLatestIn
import com.ph.notestash.common.coroutines.flow.shareLatest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

internal class FlowExtensionsTest {

    @Test
    fun `share latest`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)

        val flow = MutableSharedFlow<Int>()
        val shared = flow.shareLatest(CoroutineScope(dispatcher))

        val collector1 = mutableListOf<Int>()
        val collector2 = mutableListOf<Int>()

        val collectJob1 = launch(dispatcher) {
            shared.collect(collector1::add)
        }
        collector1.isEmpty() shouldBe true

        flow.emit(0)
        flow.emit(1)
        flow.emit(2)

        shared.first() shouldBe 2

        val collectJob2 = launch(dispatcher) {
            shared.collect(collector2::add)
        }

        flow.emit(3)
        flow.emit(4)
        flow.emit(5)

        shared.first() shouldBe 5

        collector1 shouldBe listOf(0, 1, 2, 3, 4, 5)
        collector2 shouldBe listOf(2, 3, 4, 5)

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun `collect latest`() = runTest {
        val beforeDelay = mutableListOf<Int>()
        val afterDelay = mutableListOf<Int>()

        flow {
            emit(0)
            emit(1)
            emit(2)
        }.collectLatestIn(this) {
            beforeDelay += it
            delay(1.seconds)
            afterDelay += it
        }

        advanceUntilIdle()

        beforeDelay shouldBe listOf(0, 1, 2)
        afterDelay shouldBe listOf(2)
    }
}