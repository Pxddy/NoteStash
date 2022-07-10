package com.ph.notestash.storage.repository

import androidx.datastore.core.DataStore
import com.ph.notestash.storage.model.sort.NoteSortingPreferences
import com.ph.notestash.storage.model.sort.SortNoteBy
import com.ph.notestash.storage.model.sort.SortOrder
import timber.log.Timber
import javax.inject.Inject

class NoteSortingPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<NoteSortingPreferences>
) {

    val noteSortingPreferences = dataStore.data

    suspend fun setSortedBy(sortBy: SortNoteBy) = update { prefs ->
        Timber.d("setSortedBy(sortBy=%s)", sortBy)
        prefs.copy(sortedBy = sortBy)
    }

    suspend fun setSortOrder(sortOrder: SortOrder) = update { prefs ->
        Timber.d("setSortOrder(sortOrder=%s)", sortOrder)
        prefs.copy(sortOrder = sortOrder)
    }

    private suspend fun update(
        transform: (NoteSortingPreferences) -> NoteSortingPreferences
    ) = runCatching {
        dataStore.updateData(transform)
    }.onSuccess { Timber.d("Successfully updated %s", it) }
}