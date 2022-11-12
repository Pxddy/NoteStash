package com.ph.notestash.ui.overview

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.ph.core.testing.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.core.testing.data.model.sort.SortTestData
import com.ph.notestash.common.time.TimeTestData
import com.ph.notestash.common.time.toLongDateFormat
import com.ph.notestash.data.model.note.NoteTestData
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.data.repository.NoteRepository
import com.ph.notestash.data.repository.NoteSortingPreferencesRepository
import com.ph.notestash.testutils.MainDispatcherExtension
import com.ph.notestash.testutils.TimberExtension
import com.ph.notestash.ui.overview.list.NoteOverviewListItem
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@ExtendWith(MainDispatcherExtension::class, TimberExtension::class, MockKExtension::class)
internal class NoteOverviewViewModelTest {

    private val defaultNote = NoteTestData.testDefaultNote.copy(
        modifiedAt = NoteTestData.testDefaultNote.modifiedAt.plus(5, ChronoUnit.DAYS)
    )

    private val pagingDataFlow = flowOf(PagingData.from(listOf(defaultNote)))

    private val defaultNoteSortingPreferences = SortTestData.noteSortingPreferences
    private val noteSortingPreferencesFlow = MutableStateFlow(defaultNoteSortingPreferences)

    private val testDispatcherProvider = TestDispatcherProvider()
    private val mockNoteSortingPreferencesRepository: NoteSortingPreferencesRepository = mockk()
    private val mockNoteRepository: NoteRepository = mockk()

    private val viewModel: NoteOverviewViewModel
        get() = NoteOverviewViewModel(
            dispatcherProvider = testDispatcherProvider,
            noteSortingPreferencesRepository = mockNoteSortingPreferencesRepository,
            noteRepository = mockNoteRepository
        )

    @BeforeEach
    fun setup() {
        with(mockNoteSortingPreferencesRepository) {
            every { noteSortingPreferences } returns noteSortingPreferencesFlow
            coEvery { setSortedBy(any()) } returns Result.success(defaultNoteSortingPreferences)
            coEvery { setSortOrder(any()) } returns Result.success(defaultNoteSortingPreferences)
        }

        with(mockNoteRepository) {
            every { allNotes(any(), any()) } returns pagingDataFlow
        }
    }

    @ParameterizedTest
    @EnumSource(SortNoteBy::class)
    fun `check data mapping`(sortNoteBy: SortNoteBy) = runTest {
        noteSortingPreferencesFlow.value = defaultNoteSortingPreferences.copy(
            sortedBy = sortNoteBy
        )
        mockkStatic(ZoneId::class) {
            every { ZoneId.systemDefault() } returns TimeTestData.testZoneId

            val data = viewModel.pagingData.first()
            with(defaultNote) {
                itemsFrom(data).first().asClue {
                    it.id shouldBe id
                    it.title shouldBe title
                    it.content shouldBe content

                    val datePair = when (sortNoteBy) {
                        SortNoteBy.Title,
                        SortNoteBy.CreatedAt -> createdAt to modifiedAt
                        SortNoteBy.ModifiedAt -> modifiedAt to createdAt
                    }

                    it.date shouldBe datePair.first.toLongDateFormat()
                    it.date shouldNotBe datePair.second.toLongDateFormat()
                }
            }
        }
    }

    @ParameterizedTest
    @EnumSource(SortNoteBy::class)
    fun `forwards sort note by to repo`(sortNoteBy: SortNoteBy) = runTest {
        noteSortingPreferencesFlow.update { it.copy(sortedBy = sortNoteBy) }
        viewModel.pagingData.first()
        coVerify { mockNoteRepository.allNotes(sortedBy = sortNoteBy, sortOrder = any()) }
    }

    @ParameterizedTest
    @EnumSource(SortOrder::class)
    fun `forwards sort order to repo`(sortOrder: SortOrder) = runTest {
        noteSortingPreferencesFlow.update { it.copy(sortOrder = sortOrder) }
        viewModel.pagingData.first()
        coVerify { mockNoteRepository.allNotes(sortedBy = any(), sortOrder = sortOrder) }
    }

    @Test
    fun `note on click reports navigation event to edit fragment`() = runTest {
        with(viewModel) {
            events.test {
                val data = pagingData.first()
                val noteItem = itemsFrom(data).first()
                noteItem.onclick()
                awaitItem() shouldBe NoteOverviewEvent.NavigateToNoteEdit(noteItem.id)
            }
        }
    }

    @Test
    fun `note on swipe deletes note and offers user to restore the deleted note`() = runTest {
        coEvery { mockNoteRepository.deleteNote(defaultNote.id) } returns Result.success(defaultNote)

        with(viewModel) {
            events.test {
                val data = pagingData.first()
                val noteItem = itemsFrom(data).first()
                noteItem.onSwiped(pos = 5)
                awaitItem() shouldBe NoteOverviewEvent.RestoreNote(defaultNote)
            }
        }
    }

    @Test
    fun `note on swipe deletes note and reports failure`() = runTest {
        coEvery { mockNoteRepository.deleteNote(defaultNote.id) } returns Result.failure(
            Exception("Test error")
        )
        val pos = 5

        with(viewModel) {
            events.test {
                val data = pagingData.first()
                val noteItem = itemsFrom(data).first()
                noteItem.onSwiped(pos)
                awaitItem() shouldBe NoteOverviewEvent.DeletionFailure(pos)
            }
        }
    }

    @Test
    fun `reports events`() = runTest {
        with(viewModel) {
            events.test {
                navigateToAddNote()
                awaitItem() shouldBe NoteOverviewEvent.NavigateToNoteEdit()

                showSortingDialog()
                awaitItem() shouldBe NoteOverviewEvent.ShowSortingDialog
            }
        }
    }

    @Test
    fun `restores note`() {
        coEvery { mockNoteRepository.insertNote(defaultNote) } returns Result.success(Unit)

        viewModel.restoreNote(defaultNote)

        coVerify { mockNoteRepository.insertNote(defaultNote) }
    }

    @Test
    fun `reports failed restoration`() = runTest {
        coEvery { mockNoteRepository.insertNote(defaultNote) } returns Result.failure(
            Exception("Test error")
        )

        with(viewModel) {
            restoreNote(defaultNote)
            events.test {
                awaitItem() shouldBe NoteOverviewEvent.RestoreNote(defaultNote, retry = true)
            }
        }

        coVerify { mockNoteRepository.insertNote(defaultNote) }
    }
}

private suspend fun TestScope.itemsFrom(
    data: PagingData<NoteOverviewListItem>
): List<NoteOverviewListItem> {
    val differ = createDiffer()
    differ.submitData(data)
    advanceUntilIdle()
    return differ.snapshot().items
}

private fun createDiffer() = AsyncPagingDataDiffer(
    diffCallback = TestDiffCallback(),
    updateCallback = NoopListCallback(),
    mainDispatcher = Dispatchers.Main
)

private class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

private class TestDiffCallback : DiffUtil.ItemCallback<NoteOverviewListItem>() {
    override fun areItemsTheSame(
        oldItem: NoteOverviewListItem,
        newItem: NoteOverviewListItem
    ): Boolean = oldItem.stableId == newItem.stableId

    override fun areContentsTheSame(
        oldItem: NoteOverviewListItem,
        newItem: NoteOverviewListItem
    ): Boolean = oldItem == newItem
}