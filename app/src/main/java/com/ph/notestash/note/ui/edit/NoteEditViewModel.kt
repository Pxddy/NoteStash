package com.ph.notestash.note.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    init {
        Timber.d("id=%s", NoteEditFragmentArgs.fromSavedStateHandle(savedStateHandle))
    }
}