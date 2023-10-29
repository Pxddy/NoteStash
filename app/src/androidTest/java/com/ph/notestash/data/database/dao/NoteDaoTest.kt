package com.ph.notestash.data.database.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.paging.PagingSource
import com.ph.notestash.data.database.BaseDatabaseTest
import com.ph.notestash.data.model.note.NoteEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

@HiltAndroidTest
internal class NoteDaoTest : BaseDatabaseTest() {

    override val hiltRule = HiltAndroidRule(this)

    private val dao: NoteDao
        get() = db.noteDao()

    private val entity1 = createNoteEntity(1)
    private val entity2 = createNoteEntity(2)
    private val entity3 = createNoteEntity(3)

    @Test
    fun insertNote() = runTest {
        with(dao) {
            noteForId(entity1.id).first() shouldBe null
            insertNote(entity1)
            noteForId(entity1.id).first() shouldBe entity1

            shouldThrow<SQLiteConstraintException> { insertNote(entity1) }
        }
    }

    @Test
    fun deleteNote() = runTest {
        with(dao) {
            insertAllNotes()
            noteForId(entity1.id).first() shouldBe entity1
            deleteNote(entity1)
            noteForId(entity1.id).first() shouldBe null
            noteForId(entity2.id).first() shouldBe entity2
            noteForId(entity3.id).first() shouldBe entity3
        }
    }

    @Test
    fun deleteNoteById() = runTest {
        with(dao) {
            insertNote(entity1)

            shouldThrow<IllegalArgumentException> { deleteNote(entity2.id) }

            noteForId(entity1.id).first() shouldBe entity1

            insertNote(entity2)
            deleteNote(entity1.id)

            noteForId(entity1.id).first() shouldBe null
            noteForId(entity2.id).first() shouldBe entity2
        }
    }

    @Test
    fun updateNote() = runTest {
        with(dao) {
            insertAllNotes()

            val updatedEntity1 = entity1.copy(
                title = "1 - Updated title",
                content = "1 - Updated content",
                createdAt = Instant.MAX,
                modifiedAt = Instant.MIN
            )
            updateNote(updatedEntity1)

            noteForId(entity1.id).first() shouldBe updatedEntity1
            noteForId(entity2.id).first() shouldBe entity2
            noteForId(entity3.id).first() shouldBe entity3
        }
    }

    @Test
    fun updateNoteById() = runTest {
        with(dao) {
            shouldThrow<IllegalArgumentException> { updateNote(entity1.id) { it } }

            insertNote(entity1)

            shouldThrow<UnsupportedOperationException> { updateNote(entity1.id) { entity2 } }

            insertNote(entity2)

            val updatedEntity1 = entity1.copy(
                title = "1 - Updated title",
                content = "1 - Updated content",
                createdAt = Instant.MAX,
                modifiedAt = Instant.MIN
            )
            updateNote(entity1.id) { updatedEntity1 }

            noteForId(entity1.id).first() shouldBe updatedEntity1
            noteForId(entity2.id).first() shouldBe entity2
        }
    }

    @Test
    fun allNotesSortedByTitle() = runTest {
        with(dao) {
            insertAllNotes()

            allNotesOrderByTitleAsc().getData() shouldBe listOf(entity1, entity2, entity3)
            allNotesOrderByTitleDesc().getData() shouldBe listOf(entity3, entity2, entity1)
        }
    }

    @Test
    fun allNotesSortedByCreatedAt() = runTest {
        with(dao) {
            insertAllNotes()

            allNotesOrderByCreatedAtAsc().getData() shouldBe listOf(entity1, entity2, entity3)
            allNotesOrderByCreatedAtDesc().getData() shouldBe listOf(entity3, entity2, entity1)
        }
    }

    @Test
    fun allNotesSortedByModifiedAt() = runTest {
        with(dao) {
            insertAllNotes()

            allNotesOrderByModifiedAtAsc().getData() shouldBe listOf(entity1, entity2, entity3)
            allNotesOrderByModifiedAtDesc().getData() shouldBe listOf(entity3, entity2, entity1)
        }
    }

    private suspend fun NoteDao.insertAllNotes() {
        insertNote(entity1)
        insertNote(entity2)
        insertNote(entity3)
    }
}

private fun createNoteEntity(i: Int): NoteEntity {
    val number = i.toString()
    val instant = Instant.ofEpochSecond(i.toLong())
    return NoteEntity(
        id = number,
        title = number,
        content = number,
        createdAt = instant,
        modifiedAt = instant
    )
}

private suspend fun PagingSource<Int, NoteEntity>.getData(): List<NoteEntity> {
    val refresh = PagingSource.LoadParams.Refresh<Int>(
        null,
        loadSize = 3,
        false
    )

    return when (val result = load(refresh)) {
        is PagingSource.LoadResult.Error -> throw result.throwable
        is PagingSource.LoadResult.Invalid -> error("PagingSource is not longer valid anymore")
        is PagingSource.LoadResult.Page -> result.data
    }
}