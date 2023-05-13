package com.ph.notestash.ui.edit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ph.core.testing.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.notestash.common.time.TimeProvider
import com.ph.notestash.common.time.TimeTestData
import com.ph.notestash.common.uuid.UUIDProvider
import com.ph.notestash.data.model.note.*
import com.ph.notestash.data.repository.NoteRepository
import com.ph.notestash.testutils.MainDispatcherExtension
import com.ph.notestash.testutils.TimberExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class, TimberExtension::class, MockKExtension::class)
internal class NoteEditViewModelTest {

    private val uuid1 = "fd0a781d-8516-42b9-978d-b5baeac582ac"
    private val uuid2 = "4c9b9344-6017-474b-93f0-f2a49a6f0b3b"
    private val uuid3 = "61ecb68a-06b4-4652-96ac-de0dd02163fb"
    private val defaultNote = NoteTestData.testDefaultNote
    private val noteFlow: MutableStateFlow<Note?> = MutableStateFlow(null)

    private val testDispatcherProvider = TestDispatcherProvider()
    private val noteRepository: NoteRepository = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val uuidProvider: UUIDProvider = mockk()

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) = NoteEditViewModel(
        dispatcherProvider = testDispatcherProvider,
        savedStateHandle = savedStateHandle,
        noteRepository = noteRepository,
        timeProvider = timeProvider,
        uuidProvider = uuidProvider
    )

    @BeforeEach
    fun setup() {
        noteFlow.value = null

        every { timeProvider.now } returns TimeTestData.testInstant
        every { uuidProvider.uuid } returns uuid1

        with(noteRepository) {
            every { noteForId(any()) } returns noteFlow
            coEvery { insertNote(any()) } returns Result.success(Unit)
            coEvery { deleteNote(any()) } returns Result.success(defaultNote)
        }
    }

    @Test
    fun `ui state is Loading if note for id is null`() = runTest {
        createViewModel().uiState.test {
            awaitItem() shouldBe NoteEditUiState.Loading
        }
    }

    @Test
    fun `ui state is Failure if note for id fails`() = runTest {
        coEvery { noteRepository.noteForId(any()) } returns flow { error("Test error") }

        createViewModel().uiState.test {
            awaitItem() shouldBe NoteEditUiState.Failure
        }
    }

    @Test
    fun `ui state is Success if note for ids returns a node and reports updates`() = runTest {
        noteFlow.value = defaultNote
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = defaultNote.id
        }

        createViewModel(savedStateHandle).uiState.test {
            awaitItem() shouldBe defaultNote.toNoteEditUiState()

            val changedTitle = defaultNote.copy(title = "Updated title")
            noteFlow.value = changedTitle
            awaitItem() shouldBe changedTitle.toNoteEditUiState()

            val changedContent = changedTitle.copy(content = "Updated content")
            noteFlow.value = changedContent
            awaitItem() shouldBe changedContent.toNoteEditUiState()
        }
    }

    @Test
    fun `report events`() = runTest {
        with(createViewModel()) {
            events.test { expectNoEvents() }
        }

        with(createViewModel()) {
            goBack()

            events.test {
                awaitItem() shouldBe NoteEditEvent.NavigateBack
                goBack()
                awaitItem() shouldBe NoteEditEvent.NavigateBack

                showDeletionConfirmationDialog()
                awaitItem() shouldBe NoteEditEvent.ShowDeletionConfirmationDialog

                goBack()
                goBack()
                showDeletionConfirmationDialog()
                showDeletionConfirmationDialog()

                awaitItem() shouldBe NoteEditEvent.NavigateBack
                awaitItem() shouldBe NoteEditEvent.NavigateBack
                awaitItem() shouldBe NoteEditEvent.ShowDeletionConfirmationDialog
                awaitItem() shouldBe NoteEditEvent.ShowDeletionConfirmationDialog
            }
        }
    }

    @Test
    fun `delete calls delete on repo with specified id`() = runTest {
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid1
        }
        with(createViewModel(savedStateHandle)) {
            deleteNote()
            events.test { awaitItem() shouldBe NoteEditEvent.NavigateBack }
        }

        savedStateHandle[KEY_NOTE_ID] = uuid2
        with(createViewModel(savedStateHandle)) {
            deleteNote()
            events.test { awaitItem() shouldBe NoteEditEvent.NavigateBack }
        }

        savedStateHandle[KEY_NOTE_ID] = uuid3
        with(createViewModel(savedStateHandle)) {
            deleteNote()
            events.test { awaitItem() shouldBe NoteEditEvent.NavigateBack }
        }

        coVerifyOrder {
            with(noteRepository) {
                deleteNote(uuid1)
                deleteNote(uuid2)
                deleteNote(uuid3)
            }
        }
    }

    @Test
    fun `consumes deletion failure silently`() = runTest {
        coEvery { noteRepository.deleteNote(any()) } returns Result.failure(Exception("Test error"))

        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid2
        }

        with(createViewModel(savedStateHandle)) {
            deleteNote()
            events.test { awaitItem() shouldBe NoteEditEvent.NavigateBack }
        }

        coVerify {
            noteRepository.deleteNote(uuid2)
        }
    }

    @Test
    fun `uses note id from nav args`() {
        val savedStateHandle = SavedStateHandle().apply {
            this["noteId"] = uuid3
        }

        createViewModel(savedStateHandle)

        coVerify { noteRepository.noteForId(uuid3) }
    }

    @Test
    fun `uses note id from saved state handle`() {
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid2
        }

        savedStateHandle.get<String>("noteId") shouldBe null

        createViewModel(savedStateHandle)

        coVerify { noteRepository.noteForId(uuid2) }
    }

    @Test
    fun `creates new note if saved state handle is empty`() {
        val id = defaultNote.id
        val savedStateHandle = SavedStateHandle()

        savedStateHandle.keys().isEmpty() shouldBe true

        createViewModel(savedStateHandle)

        savedStateHandle.get<String>(KEY_NOTE_ID) shouldBe id

        coVerifyOrder {
            with(noteRepository) {
                insertNote(defaultNote)
                noteForId(id)
            }
        }
    }

    @Test
    fun `consumes insertion failure silently`() {
        coEvery { noteRepository.insertNote(any()) } returns Result.failure(Exception("Test error"))

        createViewModel()

        coVerify { noteRepository.insertNote(any()) }
    }

    @Test
    fun `updates title of note`() = runTest{
        val slot = slot<UpdateNoteAction>()
        val updatedTitle = "Updated title"
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid3
        }

        coEvery {
            noteRepository.updateNote(id = any(), update = capture(slot))
        } returns Result.success(Unit)

        createViewModel(savedStateHandle).updateTitle(updatedTitle)
        advanceUntilIdle()

        val mutable = defaultNote.toMutableNote()
        mutable.title shouldNotBe updatedTitle
        slot.captured(mutable)
        mutable.title shouldBe updatedTitle

        coVerify { noteRepository.updateNote(uuid3, update = any()) }
    }

    @Test
    fun `updates content of note`() = runTest{
        val slot = slot<UpdateNoteAction>()
        val updatedContent = "Updated content"
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid1
        }

        coEvery {
            noteRepository.updateNote(id = any(), update = capture(slot))
        } returns Result.success(Unit)

        createViewModel(savedStateHandle).updateContent(updatedContent)
        advanceUntilIdle()

        val mutable = defaultNote.toMutableNote()
        mutable.content shouldNotBe updatedContent
        slot.captured(mutable)
        mutable.content shouldBe updatedContent

        coVerify { noteRepository.updateNote(uuid1, update = any()) }
    }

    @Test
    fun `consumes note update silently`() = runTest{
        val slot = slot<UpdateNoteAction>()
        val updatedContent = "Updated content"
        val savedStateHandle = SavedStateHandle().apply {
            this[KEY_NOTE_ID] = uuid1
        }
        val viewModel = createViewModel(savedStateHandle)

        coEvery {
            noteRepository.updateNote(id = any(), update = any())
        } returns Result.failure(Exception("Test error"))

        viewModel.updateContent("failed update")
        advanceUntilIdle()

        coEvery {
            noteRepository.updateNote(id = any(), update = capture(slot))
        } returns Result.success(Unit)

        viewModel.updateContent(updatedContent)
        advanceUntilIdle()

        val mutable = defaultNote.toMutableNote()
        mutable.content shouldNotBe updatedContent
        slot.captured(mutable)
        mutable.content shouldBe updatedContent

        coVerify(exactly = 2) { noteRepository.updateNote(uuid1, update = any()) }
    }
}

private fun Note.toNoteEditUiState() = NoteEditUiState.Success(
    title = title,
    content = content
)