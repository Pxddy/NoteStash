package com.ph.notestash.common

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TagHelperTest {

    @Test
    fun `creates tag from class name`() {
        tag<TagHelperTest>() shouldBe "TagHelperTest"
        tag<TestClassA>() shouldBe "TestClassA"
    }

    private class TestClassA
}

