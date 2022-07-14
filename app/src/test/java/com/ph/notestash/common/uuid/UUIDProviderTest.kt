package com.ph.notestash.common.uuid

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import java.util.*

internal class UUIDProviderTest {

    @Test
    fun `UUIDProvider returns UUID randomUUID`() {
        val uuidProvider = UUIDProvider()

        mockkStatic(UUID::class) {
            every { UUID.randomUUID() } returns UUIDTestData.uuid1
            uuidProvider.uuid shouldBe UUIDTestData.uuid1

            every { UUID.randomUUID() } returns UUIDTestData.uuid2
            uuidProvider.uuid shouldBe UUIDTestData.uuid2

            every { UUID.randomUUID() } returns UUIDTestData.uuid3
            uuidProvider.uuid shouldBe UUIDTestData.uuid3
        }
    }
}