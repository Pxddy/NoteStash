package com.ph.notestash.data.repository

import com.ph.core.testing.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.core.testing.data.datastore.InMemoryDataStore
import com.ph.core.testing.data.model.sort.SortTestData
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.testutils.TimberExtension
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(TimberExtension::class)
class NoteSortingPreferencesRepositoryTest {

    private val defaultNoteSortingPreferences = SortTestData.noteSortingPreferences

    private val testDispatcherProvider = TestDispatcherProvider()
    private val testDataStore = InMemoryDataStore(defaultNoteSortingPreferences)
    private val instance = NoteSortingPreferencesRepository(
        dispatcherProvider = testDispatcherProvider,
        dataStore = testDataStore
    )

    @BeforeEach
    fun setup() {
        testDataStore.reset()
    }

    @ParameterizedTest
    @EnumSource(SortNoteBy::class)
    fun `updates sort by pref`(sortNoteBy: SortNoteBy) = runTest {
        val expected = defaultNoteSortingPreferences.copy(sortedBy = sortNoteBy)

        testDataStore.data.first() shouldBe defaultNoteSortingPreferences
        with(instance) {
            noteSortingPreferences.first() shouldBe defaultNoteSortingPreferences
            setSortedBy(sortNoteBy) shouldBe Result.success(expected)
            noteSortingPreferences.first() shouldBe expected
        }
        testDataStore.data.first() shouldBe expected
    }

    @ParameterizedTest
    @EnumSource(SortOrder::class)
    fun `updates sort order pref`(sortOrder: SortOrder) = runTest {
        val expected = defaultNoteSortingPreferences.copy(sortOrder = sortOrder)

        testDataStore.data.first() shouldBe defaultNoteSortingPreferences
        with(instance) {
            noteSortingPreferences.first() shouldBe defaultNoteSortingPreferences
            setSortOrder(sortOrder) shouldBe Result.success(expected)
            noteSortingPreferences.first() shouldBe expected
        }
        testDataStore.data.first() shouldBe expected
    }
}