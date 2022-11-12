package com.ph.core.testing.data.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.updateAndGet

class InMemoryDataStore<T>(
    private val defaultValue: T
) : DataStore<T> {

    private val internalData = MutableStateFlow(defaultValue)

    override val data = internalData.asStateFlow()
        .onEach { println("emitting $it") }

    override suspend fun updateData(
        transform: suspend (t: T) -> T
    ): T = internalData.updateAndGet { oldData ->
        transform(oldData).also { println("Updated data $oldData -> $it") }
    }

    fun reset() {
        println("resetting internal flow to defaultValue=$defaultValue")
        internalData.value = defaultValue
    }
}