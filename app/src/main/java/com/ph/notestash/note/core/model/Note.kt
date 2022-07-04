package com.ph.notestash.note.core.model

import java.time.Instant

interface Note {
    val id: String
    val title: String
    val createdAt: Instant
    val modifiedAt: Instant
}