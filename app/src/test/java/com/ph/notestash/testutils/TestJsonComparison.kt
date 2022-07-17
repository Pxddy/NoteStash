package com.ph.notestash.testutils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.kotest.matchers.shouldBe
import okio.Buffer

internal infix fun String.shouldMatchJson(expectedJson: String) {
    val prettyPrintAdapter = Moshi.Builder().build().adapter(Any::class.java).indent("    ")

    val actualJsonPretty = transform(prettyPrintAdapter)
    val expectedJsonPretty = expectedJson.transform(prettyPrintAdapter)

    actualJsonPretty shouldBe expectedJsonPretty
}

private fun String.transform(jsonAdapter: JsonAdapter<Any>): String = try {
    val buffer = Buffer().writeUtf8(this)
    val any = JsonReader.of(buffer).readJsonValue()
    jsonAdapter.toJson(any)
} catch (e: Exception) {
    throw IllegalArgumentException("require valid json as in but got $this", e)
}