package com.ph.notestash.ui.overview.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.flow.shareLatest
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.ph.notestash.data.repository.NoteSortingPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteOverviewSortDialogViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val noteSortingPreferencesRepository: NoteSortingPreferencesRepository
) : ViewModel() {

    val uiState: Flow<NoteOverviewSortDialogUiState> = noteSortingPreferencesRepository
        .noteSortingPreferences
        .map { it.toUiState() }
        .shareLatest(
            scope = viewModelScope + dispatcherProvider.Default,
            started = SharingStarted.WhileSubscribed(stopTimeout = 5.seconds)
        )

    fun setSortedNoteBy(sortNoteBy: SortNoteBy) = viewModelScope.launch {
        Timber.d("setSortedNoteBy(sortNoteBy=%s)", sortNoteBy)
        noteSortingPreferencesRepository.setSortedBy(sortNoteBy)
            .onFailure { Timber.e(it, "Failed to set sortNoteBy=%s", sortNoteBy) }
    }

    fun setSortOrder(sortOrder: SortOrder) = viewModelScope.launch {
        Timber.d("setSortOrder(sortOrder=%s)", sortOrder)
        noteSortingPreferencesRepository.setSortOrder(sortOrder)
            .onFailure { Timber.e(it, "Failed to set sortOrder=%s", sortOrder) }
    }

    private fun NoteSortingPreferences.toUiState() = NoteOverviewSortDialogUiState(
        sortNoteBy = sortedBy,
        sortOrder = sortOrder
    )
}