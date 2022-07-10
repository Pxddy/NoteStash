package com.ph.notestash.data.model.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey @ColumnInfo(name = "id") override val id: String,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "content") override val content: String,
    @ColumnInfo(name = "created_at") override val createdAt: Instant,
    @ColumnInfo(name = "modified_at") override val modifiedAt: Instant
) : Note

fun Note.toNoteEntity() = when (this is NoteEntity) {
    true -> this
    false -> NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}
