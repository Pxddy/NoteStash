package com.ph.notestash.note.core.model

import java.time.Instant

data class DefaultNote(
    override val id: String,
    override val title: String,
    override val createdAt: Instant,
    override val modifiedAt: Instant
) : Note

fun Note.toDefaultNote() = when (this is DefaultNote) {
    true -> this
    false -> DefaultNote(
        id = id,
        title = title,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}
