package com.ph.notestash.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ph.notestash.R
import com.ph.notestash.data.model.note.DefaultNote
import com.ph.notestash.data.model.note.Note
import com.ph.notestash.data.model.note.toDefaultNote
import com.ph.notestash.data.model.note.toMutableNote
import com.ph.notestash.testutils.TimberRule
import com.ph.notestash.testutils.hasItemAtPosition
import com.ph.notestash.ui.main.MainActivity
import com.ph.notestash.ui.overview.list.NoteOverviewListItemVH
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@HiltAndroidTest
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainActivityScenario = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule(order = 2)
    val timberRule = TimberRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun addSingleNote() {
        addNote(1).run {
            leaveScreen()

            // Verify that the note list is displayed and contains the newly created note
            onView(withId(R.id.empty_list_view))
                .check(matches(not(isDisplayed())))

            onView(withId(R.id.note_list))
                .check(matches(isDisplayed()))
                .check(matches(hasDescendant(withText(title))))
                .check(matches(hasDescendant(withText(content))))
        }
    }

    @Test
    fun addSingleNote_editNoteAfterwards() {
        val note = addNote(1)

        leaveScreen()

        // Click on note
        onView(withId(R.id.note_list))
            .perform(actionOnItemAtPosition<NoteOverviewListItemVH>(0, click()))

        // Check that the data of the note is displayed
        onView(withId(R.id.title)).check(matches(withText(note.title)))
        onView(withId(R.id.content)).check(matches(withText(note.content)))

        // Edit note and verify that notes list shows the updated data
        editNote(note).run {
            leaveScreen()

            onView(withId(R.id.note_list))
                .check(matches(hasDescendant(withText(title))))
                .check(matches(hasDescendant(withText(content))))
        }

        // Verify previous does not exist anymore
        with(note) {
            onView(withId(R.id.note_list))
                .check(matches(not(hasDescendant(withText(title)))))
                .check(matches(not(hasDescendant(withText(content)))))
        }
    }

    @Test
    fun addNewNotes_swipeNoteToDelete() {
        // Add notes
        val notes = (0..2)
            .map {
                addNote(it.toLong())
                    .also { leaveScreen() }
            }

        // Make sure notes exist
        notes.forEach {
            onView(withId(R.id.note_list))
                .check(matches(hasDescendant(withText(it.title))))
        }

        // Swipe to delete note at position
        val pos = 1
        onView(withId(R.id.note_list))
            .perform(
                actionOnItemAtPosition<NoteOverviewListItemVH>(pos, swipeRight())
            )

        // Verify note does not exist anymore
        onView(withId(R.id.note_list))
            .check(matches(not(hasDescendant(withText(notes[pos].title)))))
    }

    @Test
    fun addNewNotes_swipeNoteToDelete_undoDeletion() {
        // Add notes
        val notes = (0..2)
            .map {
                addNote(it.toLong())
                    .also { leaveScreen() }
            }

        // Make sure notes exist
        notes.forEach {
            onView(withId(R.id.note_list))
                .check(matches(hasDescendant(withText(it.title))))
        }

        // Swipe to delete note at position
        val pos = 1
        onView(withId(R.id.note_list))
            .perform(actionOnItemAtPosition<NoteOverviewListItemVH>(pos, swipeRight()))

        // Verify note does not exist anymore
        onView(withText(notes[pos].title))
            .check(doesNotExist())

        // Check restore option is displayed
        onView(withText(R.string.fragment_note_overview_restore_message))
            .check(matches(isDisplayed()))

        // Click on undo
        onView(withText(R.string.fragment_note_overview_restore_action_text))
            .perform(click())

        // Verify note was restored
        onView(withId(R.id.note_list))
            .check(matches(hasDescendant(withText(notes[pos].title))))
    }

    @Test
    fun discardNote() {
        val note = addNote(0)

        // Click on delete icon
        onView(withId(R.id.action_delete))
            .perform(click())

        // Check deletion confirmation dialog is displayed
        onView(withText(R.string.fragment_note_edit_deletion_confirmation_dialog_msg))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Confirm discarding the note
        onView(
            withText(R.string.fragment_note_edit_deletion_confirmation_dialog_positive_button_text)
        ).perform(click())

        // Verify the note list is empty and the discarded note does not exist
        onView(withId(R.id.empty_list_view)).check(matches(isDisplayed()))
        onView(withText(note.title)).check(doesNotExist())
    }

    @Test
    fun discardNote_denyDeletion() {
        val note = addNote(0)

        // Click on delete icon
        onView(withId(R.id.action_delete))
            .perform(click())

        // Check deletion confirmation dialog is displayed
        onView(withText(R.string.fragment_note_edit_deletion_confirmation_dialog_msg))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Confirm discarding the note
        onView(
            withText(R.string.fragment_note_edit_deletion_confirmation_dialog_negative_button_text)
        ).perform(click())

        leaveScreen()

        // Verify that the note has not been discarded and is in notes list
        onView(withId(R.id.note_list))
            .check(matches(hasDescendant(withText(note.title))))
    }

    @Test
    fun sortNotesByTitleAscending() {
        // Add notes
        val notes = (0..2)
            .map {
                addNote(it.toLong())
                    .also { leaveScreen() }
            }

        // Click sort item
        onView(withId(R.id.action_sort)).perform(click())

        // Check sort dialog is displayed
        onView(withId(R.id.radio_group_sort_by))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Set sort by title checked
        onView(withId(R.id.radio_button_sort_by_title))
            .perform(click())

        // Set sort order ascending checked
        onView(withId(R.id.radio_button_sort_order_ascending)).perform(click())

        // Close sort dialog
        Espresso.pressBack()

        // Verify that the notes are sorted by title in ascending order
        notes.sortedBy { it.title }.forEachIndexed { index, note ->
            onView(withId(R.id.note_list))
                .check(matches(hasItemAtPosition(index, withText(note.title))))
        }
    }

    private fun addNote(number: Long): Note {
        val note = DefaultNote(
            id = number.toString(),
            title = "Espresso Title $number",
            content = "Espresso Content $number",
            Instant.ofEpochSecond(number),
            Instant.ofEpochSecond(number)
        )
        // Open NoteEditScreen
        onView(withId(R.id.add_note_button))
            .perform(click())

        // Add title and content to note
        onView(withId(R.id.title)).perform(typeText(note.title), closeSoftKeyboard())
        onView(withId(R.id.content)).perform(typeText(note.content), closeSoftKeyboard())

        return note
    }

    private fun editNote(note: Note): Note {
        val updatedNote = note.toMutableNote()
            .apply {
                title += " - Updated"
                content += " - Updated"
            }.toDefaultNote()

        // Update title and content
        onView(withId(R.id.title)).perform(replaceText(updatedNote.title), closeSoftKeyboard())
        onView(withId(R.id.content)).perform(replaceText(updatedNote.content), closeSoftKeyboard())

        return updatedNote
    }

    private fun leaveScreen() {
        Espresso.pressBack()
    }
}