package com.ph.notestash.ui.overview.dialog

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.data.repository.NoteSortingPreferencesRepository
import com.ph.notestash.testutils.MainDispatcherExtension
import com.ph.notestash.testutils.TestDispatcherProvider
import com.ph.notestash.testutils.TimberExtension
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TimberExtension::class)
@ExtendWith(MainDispatcherExtension::class)
internal class NoteOverviewSortDialogViewModelTest {

    private val defaultNoteSortingPreferences = NoteSortingPreferences()
    private val noteSortingPreferencesFlow = MutableStateFlow(defaultNoteSortingPreferences)
    private val mockNoteSortingPreferencesRepository: NoteSortingPreferencesRepository = mockk()
    private val testDispatcherProvider = TestDispatcherProvider()
    private lateinit var viewModel: NoteOverviewSortDialogViewModel

    @BeforeEach
    fun setup() {
        clearAllMocks()
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

    @Test
    fun `check uiState mapping`() = runTest {
        viewModel.uiState.test {
            awaitItem() shouldBe NoteOverviewSortDialogUiState(
                sortNoteBy = defaultNoteSortingPreferences.sortedBy,
                sortOrder = defaultNoteSortingPreferences.sortOrder
            )

            checkUiState(SortNoteBy.Title, SortOrder.Ascending)
            checkUiState(SortNoteBy.Title, SortOrder.Descending)

            checkUiState(SortNoteBy.Title, SortOrder.Ascending)
            checkUiState(SortNoteBy.CreatedAt, SortOrder.Ascending)
            checkUiState(SortNoteBy.ModifiedAt, SortOrder.Descending)
        }
    }

    private suspend fun FlowTurbine<NoteOverviewSortDialogUiState>.checkUiState(
        sortNoteBy: SortNoteBy,
        sortOrder: SortOrder
    ) {
        noteSortingPreferencesFlow.value = NoteSortingPreferences(
            sortedBy = sortNoteBy,
            sortOrder = sortOrder
        )

        awaitItem() shouldBe NoteOverviewSortDialogUiState(
            sortNoteBy = sortNoteBy,
            sortOrder = sortOrder
        )
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