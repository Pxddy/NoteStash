package com.ph.notestash.data.repository

import androidx.paging.PagingSource
import com.ph.core.testing.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.notestash.common.time.TimeProvider
import com.ph.notestash.data.database.dao.NoteDao
import com.ph.notestash.data.model.note.*
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.testutils.TimberExtension
import com.ph.notestash.testutils.runWithoutChildExceptionCancellation
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.EnumSource
import java.time.Instant

@ExtendWith(TimberExtension::class, MockKExtension::class)
class NoteRepositoryTest {

    private val mockNoteDao: NoteDao = mockk(relaxUnitFun = true)
    private val mockTimeProvider: TimeProvider = mockk()

    private val now = Instant.MAX
    private val defaultNote = NoteTestData.testDefaultNote

    private val mockPagingSource: PagingSource<Int, NoteEntity> = mockk(relaxed = true)

    private fun createInstance(scope: TestScope) = createInstance(scope, scope.testScheduler)
    private fun createInstance(
        scope: CoroutineScope,
        testCoroutineScheduler: TestCoroutineScheduler
    ) = NoteRepository(
        appScope = scope,
        dispatcherProvider = TestDispatcherProvider(testCoroutineScheduler),
        noteDaoLazy = { mockNoteDao },
        timeProvider = mockTimeProvider
    )

    @BeforeEach
    fun setup() {
        every { mockTimeProvider.now } returns now

        with(mockNoteDao) {
            coEvery { allNotesOrderByTitleAsc() } returns mockPagingSource
            coEvery { allNotesOrderByTitleDesc() } returns mockPagingSource

            coEvery { allNotesOrderByCreatedAtAsc() } returns mockPagingSource
            coEvery { allNotesOrderByCreatedAtDesc() } returns mockPagingSource

            coEvery { allNotesOrderByModifiedAtAsc() } returns mockPagingSource
            coEvery { allNotesOrderByModifiedAtDesc() } returns mockPagingSource
        }
    }

    @ParameterizedTest
    @ArgumentsSource(NoteMappingTestArgumentProvider::class)
    fun `converts note to entity and calls insert on dao`(note: Note) = runTest {
        val entity = note.toNoteEntity()
        createInstance(scope = this).insertNote(note) shouldBe Result.success(Unit)

        coVerify { mockNoteDao.insertNote(entity) }
    }

    @Test
    fun `catches error and returns insertion failure`() = runTest {
        val error = Exception("Test error")
        val entity = defaultNote.toNoteEntity()

        coEvery { mockNoteDao.insertNote(entity) } throws error

        val result = runWithoutChildExceptionCancellation {
            createInstance(scope = this, testScheduler).insertNote(defaultNote)
        }
        result.exceptionOrNull() shouldBe error

        coVerify { mockNoteDao.insertNote(entity) }
    }

    @Test
    fun `calls delete on dao and returns deleted note`() = runTest {
        val entity = defaultNote.toNoteEntity()
        val id = entity.id

        coEvery { mockNoteDao.deleteNote(id) } returns entity

        createInstance(scope = this).deleteNote(id) shouldBe Result.success(entity)
    }

    @Test
    fun `catches error and returns deletion failure`() = runTest {
        val error = Exception("Test error")
        val entity = defaultNote.toNoteEntity()
        val id = entity.id

        coEvery { mockNoteDao.deleteNote(id) } throws error

        val result = runWithoutChildExceptionCancellation {
            createInstance(scope = this, testScheduler).deleteNote(id)
        }
        result.exceptionOrNull() shouldBe error
    }

    @Test
    fun `note for id`() = runTest {
        val entity = defaultNote.toNoteEntity()
        val id = entity.id
        val instance = createInstance(scope = this)

        coEvery { mockNoteDao.noteForId(id) } returns flowOf(null)
        instance.noteForId(id).first() shouldBe null

        coEvery { mockNoteDao.noteForId(id) } returns flowOf(entity)
        instance.noteForId(id).first() shouldBe entity
    }

    @Test
    fun `update note`() = runTest {
        val entity = defaultNote.toNoteEntity()
        val id = entity.id
        val updatedTitle = "Updated title"
        val updatedContent = "Updated content"
        val updatedCreatedAt = Instant.MIN
        val updateModifiedAt = Instant.EPOCH
        val instance = createInstance(this)
        val slot = slot<suspend (note: NoteEntity) -> NoteEntity>()

        coEvery { mockNoteDao.updateNote(id, capture(slot)) } just Runs
        instance.updateNote(id) {
            title = updatedTitle
            content = updatedContent
            createdAt = updatedCreatedAt
            modifiedAt = updateModifiedAt
        }

        slot.captured(entity) shouldBe entity.copy(
            title = updatedTitle,
            content = updatedContent,
            createdAt = updatedCreatedAt,
            modifiedAt = updateModifiedAt
        )

        slot.clear()

        // Should update modifiedAt if not manually set
        instance.updateNote(id) {
            title = updatedTitle
            content = updatedContent
            createdAt = updatedCreatedAt
        }
        slot.captured(entity) shouldBe entity.copy(
            title = updatedTitle,
            content = updatedContent,
            createdAt = updatedCreatedAt,
            modifiedAt = now
        )

        verify(exactly = 2) { mockTimeProvider.now }
    }

    @Test
    fun `catches error and returns update failure`() = runTest {
        val id = defaultNote.id
        val error = Exception("Test error")

        coEvery { mockNoteDao.updateNote(id, any()) } throws error
        val result = runWithoutChildExceptionCancellation {
            createInstance(scope = this, testScheduler).updateNote(id) {}
        }
        result.exceptionOrNull() shouldBe error
    }

    @ParameterizedTest
    @EnumSource(SortNoteBy::class)
    fun `calls dao method according to`(sortNoteBy: SortNoteBy) = runTest {
        createInstance(this).allNotes(
            sortedBy = sortNoteBy,
            sortOrder = SortOrder.Ascending
        ).first()

        with(mockNoteDao) {
            coVerify {
                when (sortNoteBy) {
                    SortNoteBy.Title -> allNotesOrderByTitleAsc()
                    SortNoteBy.CreatedAt -> allNotesOrderByCreatedAtAsc()
                    SortNoteBy.ModifiedAt -> allNotesOrderByModifiedAtAsc()
                }
            }
        }
    }

    @ParameterizedTest
    @EnumSource(SortOrder::class)
    fun `calls dao method according to`(sortOrder: SortOrder) = runTest {
        createInstance(this).allNotes(
            sortedBy = SortNoteBy.Title,
            sortOrder = sortOrder
        ).first()

        with(mockNoteDao) {
            coVerify {
                when (sortOrder) {
                    SortOrder.Ascending -> allNotesOrderByTitleAsc()
                    SortOrder.Descending -> allNotesOrderByTitleDesc()
                }
            }
        }
    }
}