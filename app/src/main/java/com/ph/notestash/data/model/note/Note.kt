package com.ph.notestash.data.model.note

import java.time.Instant

interface Note {
    val id: String
    val title: String
    val content: String
    val createdAt: Instant
    val modifiedAt: Instant
}