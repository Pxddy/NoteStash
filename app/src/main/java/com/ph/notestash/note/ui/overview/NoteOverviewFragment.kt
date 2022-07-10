package com.ph.notestash.note.ui.overview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.ph.notestash.R
import com.ph.notestash.common.fragment.navigateTo
import com.ph.notestash.common.recyclerview.swipe.SimpleSwipeCallback
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteOverviewBinding
import com.ph.notestash.note.ui.overview.list.note.NoteOverviewListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NoteOverviewFragment : Fragment(R.layout.fragment_note_overview) {

    private val binding by viewBinding(FragmentNoteOverviewBinding::bind)
    private val viewModel: NoteOverviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupMenu()
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")

        val adapter = NoteOverviewListAdapter()
        noteList.adapter = adapter

        addNoteButton.setOnClickListener { viewModel.navigateToAddNote() }

        ItemTouchHelper(SimpleSwipeCallback()).attachToRecyclerView(noteList)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { handleUiState(uiState = it, adapter = adapter) }
                    .launchIn(this)

                viewModel.events
                    .onEach { handleEvent(event = it) }
                    .launchIn(this)
            }
        }
    }

    private fun handleUiState(
        uiState: NoteOverviewUiState,
        adapter: NoteOverviewListAdapter
    ) = with(binding) {
        loadingView.root.isVisible = uiState is NoteOverviewUiState.Loading
        errorView.root.isVisible = uiState is NoteOverviewUiState.Failure
        noteList.isVisible = uiState is NoteOverviewUiState.Success

        when (uiState) {
            is NoteOverviewUiState.Success -> {
                adapter.submitList(uiState.notes)
            }
            NoteOverviewUiState.Failure,
            NoteOverviewUiState.Loading -> Unit
        }
    }

    private fun handleEvent(event: NoteOverviewEvent) {
        Timber.d("handleEvent(event=%s)", event)
        when (event) {
            is NoteOverviewEvent.NavigateToNoteEdit -> navigateTo(
                NoteOverviewFragmentDirections.actionNoteOverviewFragmentToNoteEditFragment(
                    noteId = event.id
                )
            )
            is NoteOverviewEvent.RestoreNote -> event.handle()
            NoteOverviewEvent.ShowSortingDialog -> navigateTo(
                NoteOverviewFragmentDirections
                    .actionNoteOverviewFragmentToNoteOverviewSortDialogFragment()
            )
        }
    }

    private fun NoteOverviewEvent.RestoreNote.handle() {
        val (msg, actionText) = when (retry) {
            true -> R.string.fragment_note_overview_restore_retry_message to R.string.fragment_note_overview_restore_retry_action_text
            false -> R.string.fragment_note_overview_restore_message to R.string.fragment_note_overview_restore_action_text
        }
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setAction(actionText) { viewModel.restoreNote(note) }
            .show()
    }

    private fun setupMenu() {
        Timber.d("setupMenu()")
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_sort -> {
                    viewModel.showSortingDialog()
                    true
                }
                else -> false
            }
        }
    }
}