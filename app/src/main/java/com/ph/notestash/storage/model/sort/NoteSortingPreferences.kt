package com.ph.notestash.storage.model.sort

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteSortingPreferences(
    @Json(name = "sortedBy") val sortedBy: SortNoteBy = SortNoteBy.CreatedAt,
    @Json(name = "sortOrder") val sortOrder: SortOrder = SortOrder.Descending
)
