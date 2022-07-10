package com.ph.notestash.data.model.sort

import com.squareup.moshi.Json

enum class SortOrder {
    @Json(name = "Ascending")
    Ascending,

    @Json(name = "Descending")
    Descending
}