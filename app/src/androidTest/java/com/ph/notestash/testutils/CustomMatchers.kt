package com.ph.notestash.testutils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Returns a matcher that matches [RecyclerView] whose [ViewHolder][RecyclerView.ViewHolder] at the
 * specified [pos] has descendants that match the specified [matcher].
 */
fun hasItemAtPosition(pos: Int, matcher: Matcher<View>): Matcher<View> {
    val hasDescendantMatcher = ViewMatchers.hasDescendant(matcher)
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("has item at position $pos: ")
            hasDescendantMatcher.describeTo(description)
        }

        override fun matchesSafely(item: RecyclerView?): Boolean {
            val viewHolder = item?.findViewHolderForAdapterPosition(pos)
            return hasDescendantMatcher.matches(viewHolder?.itemView)
        }
    }
}