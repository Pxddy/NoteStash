package com.ph.notestash.data.repository

import androidx.datastore.core.DataStore
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NoteSortingPreferencesRepository @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
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
    ) = withContext(dispatcherProvider.IO) {
        runCatching { dataStore.updateData(transform) }
            .onSuccess { Timber.d("Successfully updated %s", it) }
    }
}