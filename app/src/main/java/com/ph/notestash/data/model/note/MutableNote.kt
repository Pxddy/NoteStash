package com.ph.notestash.data.model.note

import java.time.Instant

class MutableNote(note: Note) : Note {
    override val id: String = note.id
    override var title: String = note.title
    override var content: String = note.content
    override var createdAt: Instant = note.createdAt
    override var modifiedAt: Instant = note.modifiedAt
}

fun Note.toMutableNote() = when (this is MutableNote) {
    true -> this
    false -> MutableNote(note = this)
}

typealias UpdateNoteAction = MutableNote.() -> Unit