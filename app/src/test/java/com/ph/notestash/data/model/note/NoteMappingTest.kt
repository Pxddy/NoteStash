package com.ph.notestash.data.model.note

import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

class NoteMappingTest {

    @ParameterizedTest
    @ArgumentsSource(NoteMappingTestArgumentProvider::class)
    fun `check note mapping`(note: Note) {
        note.check(note is DefaultNote) { it.toDefaultNote() }
        note.check(note is NoteEntity) { it.toNoteEntity() }
        note.check(note is MutableNote) { it.toMutableNote() }
    }

    private fun Note.check(checkIsSameInstance: Boolean, map: (Note) -> Note) = map(this).asClue {
        println("$this - $it")
        it.id shouldBe id
        it.title shouldBe title
        it.content shouldBe content
        it.createdAt shouldBe createdAt
        it.modifiedAt shouldBe modifiedAt

        println("checkIsSameInstance=$checkIsSameInstance")
        if (checkIsSameInstance) it shouldBeSameInstanceAs this
    }
}