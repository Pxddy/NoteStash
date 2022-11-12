package com.ph.notestash.ui.overview.dialog

import com.ph.core.testing.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.data.repository.NoteSortingPreferencesRepository
import com.ph.notestash.testutils.MainDispatcherExtension
import com.ph.notestash.testutils.TimberExtension
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(MainDispatcherExtension::class, TimberExtension::class, MockKExtension::class)
internal class NoteOverviewSortDialogViewModelTest {

    private val defaultNoteSortingPreferences = NoteSortingPreferences()
    private val noteSortingPreferencesFlow = MutableStateFlow(defaultNoteSortingPreferences)
    private val mockNoteSortingPreferencesRepository: NoteSortingPreferencesRepository = mockk()
    private val testDispatcherProvider = TestDispatcherProvider()
    private lateinit var viewModel: NoteOverviewSortDialogViewModel

    @BeforeEach
    fun setup() {
        noteSortingPreferencesFlow.value = defaultNoteSortingPreferences

        with(mockNoteSortingPreferencesRepository) {
            every { noteSortingPreferences } returns noteSortingPreferencesFlow
            coEvery { setSortOrder(any()) } returns Result.success(defaultNoteSortingPreferences)
            coEvery { setSortedBy(any()) } returns Result.success(defaultNoteSortingPreferences)
        }

        viewModel = NoteOverviewSortDialogViewModel(
            dispatcherProvider = testDispatcherProvider,
            noteSortingPreferencesRepository = mockNoteSortingPreferencesRepository
        )
    }

    @ParameterizedTest
    @EnumSource(SortNoteBy::class)
    fun `check ui state mapping for`(sortNoteBy: SortNoteBy) = runTest {
        val newPrefs = defaultNoteSortingPreferences.copy(sortedBy = sortNoteBy)
        noteSortingPreferencesFlow.value = newPrefs
        viewModel.uiState.first() shouldBe newPrefs.toNoteOverviewSortDialogUiState()
    }

    @ParameterizedTest
    @EnumSource(SortOrder::class)
    fun `check ui state mapping for`(sortOrder: SortOrder) = runTest {
        val newPrefs = defaultNoteSortingPreferences.copy(sortOrder = sortOrder)
        noteSortingPreferencesFlow.value = newPrefs
        viewModel.uiState.first() shouldBe newPrefs.toNoteOverviewSortDialogUiState()
    }

    @Test
    fun `setSortedNoteBy forwards sortNoteBy to repo`() {
        with(viewModel) {
            setSortedNoteBy(SortNoteBy.Title)
            setSortedNoteBy(SortNoteBy.CreatedAt)
            setSortedNoteBy(SortNoteBy.ModifiedAt)
        }

        coVerifyOrder {
            with(mockNoteSortingPreferencesRepository) {
                setSortedBy(SortNoteBy.Title)
                setSortedBy(SortNoteBy.CreatedAt)
                setSortedBy(SortNoteBy.ModifiedAt)
            }
        }
    }

    @Test
    fun `setSortOrder forwards sortOrder to repo`() {
        with(viewModel) {
            setSortOrder(SortOrder.Ascending)
            setSortOrder(SortOrder.Descending)
        }

        coVerifyOrder {
            with(mockNoteSortingPreferencesRepository) {
                setSortOrder(SortOrder.Ascending)
                setSortOrder(SortOrder.Descending)
            }
        }
    }

    @Test
    fun `viewModel does not throw on failure`() {
        val testError = Exception("Test error")
        with(mockNoteSortingPreferencesRepository) {
            coEvery { setSortOrder(any()) } returns Result.failure(testError)
            coEvery { setSortedBy(any()) } returns Result.failure(testError)
        }

        with(viewModel) {
            shouldNotThrowAny { setSortOrder(SortOrder.Ascending) }
            shouldNotThrowAny { setSortedNoteBy(SortNoteBy.Title) }
        }

        coVerify {
            with(mockNoteSortingPreferencesRepository) {
                setSortOrder(SortOrder.Ascending)
                setSortedBy(SortNoteBy.Title)
            }
        }
    }
}

private fun NoteSortingPreferences.toNoteOverviewSortDialogUiState() =
    NoteOverviewSortDialogUiState(
        sortNoteBy = sortedBy,
        sortOrder = sortOrder
    )