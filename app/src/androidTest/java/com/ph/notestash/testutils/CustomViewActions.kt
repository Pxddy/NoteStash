package com.ph.notestash.testutils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

/**
 * Delays ui interaction for the specified [delayMillis].
 *
 * @param delayMillis - time in milliseconds
 */
fun waitFor(delayMillis: Long) = object : ViewAction {
    override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayed()

    override fun getDescription(): String = "wait for $delayMillis ms"

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.loopMainThreadForAtLeast(delayMillis)
    }
}