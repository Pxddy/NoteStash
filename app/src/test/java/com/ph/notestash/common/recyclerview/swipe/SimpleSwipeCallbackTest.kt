package com.ph.notestash.common.recyclerview.swipe

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class SimpleSwipeCallbackTest {

    private val mockRecyclerView: RecyclerView = mockk()
    private val mockVH: RecyclerView.ViewHolder = mockk()
    private lateinit var simpleSwipeCallback: SimpleSwipeCallback

    @BeforeEach
    fun setup() {
        simpleSwipeCallback = SimpleSwipeCallback()
    }

    @Test
    fun `onMove returns false`() {
        simpleSwipeCallback.onMove(
            recyclerView = mockRecyclerView,
            viewHolder = mockVH,
            target = createMockSwipeableVH()
        ) shouldBe false
    }

    @Test
    fun `call onSwiped on Swipeable view holders with specified direction`() {
        val direction = 1
        val swipeableVH = createMockSwipeableVH()

        simpleSwipeCallback.onSwiped(swipeableVH, direction)
        simpleSwipeCallback.onSwiped(mockVH, direction)

        verify {
            swipeableVH.onSwiped(direction)

            mockVH wasNot called
        }
    }

    @Test
    fun `allow movement only for Swipeable view holders which can swipe`() {
        val swipeableVH = createMockSwipeableVH()
        val nonSwipeableVH = createMockSwipeableVH(false)

        val movementFlags = ItemTouchHelper.SimpleCallback.makeMovementFlags(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        )

        simpleSwipeCallback.getMovementFlags(
            recyclerView = mockRecyclerView,
            viewHolder = swipeableVH
        ) shouldBe movementFlags

        simpleSwipeCallback.getMovementFlags(
            recyclerView = mockRecyclerView,
            viewHolder = nonSwipeableVH
        ) shouldBe ItemTouchHelper.ACTION_STATE_IDLE

        simpleSwipeCallback.getMovementFlags(
            recyclerView = mockRecyclerView,
            viewHolder = mockVH
        ) shouldBe ItemTouchHelper.ACTION_STATE_IDLE
    }

    private fun createMockSwipeableVH(swipe: Boolean = true): SwipeableVH = mockk(relaxed = true) {
        every { canSwipe } returns swipe
    }
}

private abstract class SwipeableVH(itemView: View): RecyclerView.ViewHolder(itemView), Swipeable