package com.ph.notestash.common.recyclerview.adapter

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class BindingViewHolderTest {

    private val mockContext: Context = mockk()
    private val mockRoot: View = mockk{
        every { context } returns mockContext
    }
    private val mockBinding: ViewBinding = mockk(relaxed = true) {
        every { root } returns mockRoot
    }

    @Test
    fun `gets context from root`() {
        val vh = object : BindingViewHolder<TestItem, ViewBinding>(mockBinding) {
            override fun ViewBinding.bind(item: TestItem) {
                context shouldBe mockContext
            }
        }

        vh.bind(TestItem())

        verify { mockRoot.context }
    }
}

private data class TestItem(val test: String = "test")