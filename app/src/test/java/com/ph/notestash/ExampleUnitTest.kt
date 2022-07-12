package com.ph.notestash

import com.ph.notestash.testutils.TimberLog
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import timber.log.Timber


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@TimberLog
class ExampleUnitTest {
    @Test
    fun `addition isCorrect`() {
        Timber.d("Test")
        2 + 2 shouldBe 4
    }
}