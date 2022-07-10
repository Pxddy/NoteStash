package com.ph.notestash.data.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.ph.notestash.common.result.checkCancellation
import com.squareup.moshi.JsonAdapter
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

abstract class BaseJsonSerializer<T>(private val adapter: JsonAdapter<T>) : Serializer<T> {

    override suspend fun readFrom(input: InputStream): T = runCatching {
        val value = input.source().buffer().use { adapter.fromJson(it) }
        checkNotNull(value) { "Input produced null instance" }
    }
        .checkCancellation()
        .onFailure { throw CorruptionException("Failed to read data with adapter=$adapter", it) }
        .getOrThrow()

    override suspend fun writeTo(t: T, output: OutputStream) {
        runCatching { output.sink().buffer().use { adapter.toJson(it, t) } }
            .checkCancellation()
            .onFailure { Timber.e(it, "Failed to write data=%s", t) }
    }
}