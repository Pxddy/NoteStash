package com.ph.notestash.common

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class TagHelperTest {

    @Test
    fun `creates fragment tag from class getName`() {
        fragmentTag<TagHelperTest>() shouldBe "com.ph.notestash.common.TagHelperTest"
        fragmentTag<TestClassA>() shouldBe "com.ph.notestash.common.TagHelperTest\$TestClassA"
    }

    private class TestClassA
}

