package com.ph.notestash.data.model.note

import com.ph.notestash.common.time.TimeTestData

object NoteTestData {

    val testDefaultNote = DefaultNote(
        id = "fd0a781d-8516-42b9-978d-b5baeac582ac",
        title = "",
        content = "",
        createdAt = TimeTestData.testInstant,
        modifiedAt = TimeTestData.testInstant
    )
}