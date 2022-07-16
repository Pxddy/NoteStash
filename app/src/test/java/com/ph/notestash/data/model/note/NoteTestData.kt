package com.ph.notestash.data.model.note

import com.ph.notestash.common.time.TimeTestData
import com.ph.notestash.common.uuid.UUIDTestData

object NoteTestData {

    val testDefaultNote = DefaultNote(
        id = UUIDTestData.uuidString1,
        title = "",
        content = "",
        createdAt = TimeTestData.testInstant,
        modifiedAt = TimeTestData.testInstant
    )
}