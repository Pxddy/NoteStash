package com.ph.notestash.data.datastore.serializer

import androidx.datastore.core.CorruptionException
import com.ph.notestash.common.moshi.MoshiModule
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.ph.notestash.testutils.shouldMatchJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

internal class NoteSortingPreferencesSerializerTest {

    private val moshi = MoshiModule.provideMoshi()
    private val instance = NoteSortingPreferencesSerializer(moshi)

    private val defaultNoteSortingPreferences = NoteSortingPreferences()
    private val defaultNoteSortingPreferencesJson = """
        {
          "sortedBy": "CreatedAt",
          "sortOrder": "Descending"
        }
    """.trimIndent()

    @Test
    fun `check default value`() {
        instance.defaultValue shouldBe defaultNoteSortingPreferences
    }

    @Test
    fun `read value`() = runTest {
        defaultNoteSortingPreferencesJson
            .byteInputStream()
            .use { instance.readFrom(it) } shouldBe defaultNoteSortingPreferences
    }

    @Test
    fun `write value`() = runTest {
        ByteArrayOutputStream()
            .apply { use { instance.writeTo(defaultNoteSortingPreferences, output = it) } }
            .toString() shouldMatchJson defaultNoteSortingPreferencesJson
    }

    @Test
    fun `throws CorruptionException for invalid data`() = runTest {
        shouldThrow<CorruptionException> {
            "Invalid Data".byteInputStream().use { instance.readFrom(it) }
        }
    }
}