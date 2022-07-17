package com.ph.notestash.testutils

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.*
import timber.log.Timber

internal class InMemoryDataStore<T>(
    private val defaultValue: T
) : DataStore<T> {

    private val internalData = MutableStateFlow(defaultValue)

    override val data = internalData.asStateFlow()
        .onEach { Timber.v("emitting %s", it) }

    override suspend fun updateData(
        transform: suspend (t: T) -> T
    ): T = internalData.updateAndGet { oldData ->
        transform(oldData).also { Timber.v("Updated data %s -> %s", oldData, it) }
    }

    fun reset() {
        Timber.v("resetting internal flow to defaultValue=%s", defaultValue)
        internalData.value = defaultValue
    }
}