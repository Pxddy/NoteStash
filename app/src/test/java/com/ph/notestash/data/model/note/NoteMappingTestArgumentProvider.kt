package com.ph.notestash.data.model.note

import com.ph.notestash.common.uuid.UUIDTestData
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.time.Instant
import java.util.stream.Stream
import kotlin.time.Duration.Companion.days

class NoteMappingTestArgumentProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = listOf(
        NoteTestData.testDefaultNote.copy(
            title = "DefaultNote title",
            content = "DefaultNote title"
        ),
        NoteEntity(
            id = UUIDTestData.uuidString2,
            title = "NoteEntity title",
            content = "NoteEntity content",
            createdAt = Instant.MIN,
            modifiedAt = Instant.MAX
        ),
        NoteTestData.testDefaultNote.toMutableNote().apply {
            title = "MutableNote title"
            content = "MutableNote content"
            createdAt = createdAt.plusMillis(10.days.inWholeMilliseconds)
            modifiedAt = modifiedAt.plusMillis(100.days.inWholeMilliseconds)
        }
    ).map { Arguments.of(it) }.stream()
}