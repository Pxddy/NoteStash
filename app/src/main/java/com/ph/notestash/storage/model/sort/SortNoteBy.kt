package com.ph.notestash.storage.model.sort

import com.squareup.moshi.Json

enum class SortNoteBy {
    @Json(name = "Title")
    Title,

    @Json(name = "CreatedAt")
    CreatedAt,

    @Json(name = "ModifiedAt")
    ModifiedAt
}