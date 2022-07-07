package com.ph.notestash.note.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.time.TimeProvider
import com.ph.notestash.note.core.model.DefaultNote
import com.ph.notestash.note.core.model.Note
import com.ph.notestash.note.core.model.UpdateNoteAction
import com.ph.notestash.note.core.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val eventChannel = Channel<NoteEditEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiState: StateFlow<NoteEditUiState> = noteRepository.noteForId(id)
        .filterNotNull()
        .map { it.toUiState() }
        .catch {
            Timber.e(it, "Failed to load note")
            emit(NoteEditUiState.Failure)
        }
        .stateIn(
            scope = viewModelScope + dispatcherProvider.Default,
            started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds),
            initialValue = NoteEditUiState.Loading
        )

    fun updateNote(update: UpdateNoteAction) = viewModelScope.launch {
        noteRepository.updateNote(id = id, update = update)
            .onFailure { Timber.e(it, "Failed to update note") }
    }

    fun goBack() = viewModelScope.launch {
        Timber.d("goBack()")
        eventChannel.send(NoteEditEvent.NavigateBack)
    }

    private fun Note.toUiState(): NoteEditUiState = NoteEditUiState.Success(
        title = title,
        content = content
    )

    private val id: String
        get() = NoteEditFragmentArgs.fromSavedStateHandle(savedStateHandle).noteId
            ?: savedStateHandle.get<String>(KEY_NOTE_ID) ?: createNewNote().id

    private fun createNewNote(): Note {
        Timber.d("createNewNote()")
        val now = timeProvider.now
        return DefaultNote(
            id = UUID.randomUUID().toString(),
            title = "",
            content = "",
            createdAt = now,
            modifiedAt = now
        ).also { note ->
            savedStateHandle[KEY_NOTE_ID] = note.id
            viewModelScope.launch {
                noteRepository.insertNote(note)
                    .onSuccess { Timber.d("Successfully added note=%s to db", note) }
                    .onFailure { Timber.e(it, "Failed to add note=%s to db") }
            }
        }
    }
}

private const val KEY_NOTE_ID = "key_note_id"