package com.ph.notestash.ui.overview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.ph.notestash.R
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.flow.collectLatestIn
import com.ph.notestash.common.fragment.navigateTo
import com.ph.notestash.common.recyclerview.swipe.SimpleSwipeCallback
import com.ph.notestash.databinding.FragmentNoteOverviewBinding
import com.ph.notestash.ui.overview.list.NoteOverviewListAdapter
import com.pxddy.simpleviewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NoteOverviewFragment : Fragment(R.layout.fragment_note_overview) {

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider
    private val binding by viewBinding(FragmentNoteOverviewBinding::bind)
    private val viewModel: NoteOverviewViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupMenu()
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")

        val adapter = NoteOverviewListAdapter(dispatcherProvider)
        noteList.adapter = adapter

        addNoteButton.setOnClickListener { viewModel.navigateToAddNote() }

        ItemTouchHelper(SimpleSwipeCallback()).attachToRecyclerView(noteList)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagingData
                    .collectLatestIn(this) { adapter.submitData(it) }

                adapter.loadStateFlow
                    .onEach { loadState ->
                        val refresh = loadState.refresh
                        val isLoading = refresh is LoadState.Loading
                        val isNotLoading = refresh is LoadState.NotLoading
                        val isError = refresh is LoadState.Error
                        val isEmpty = adapter.itemCount == 0

                        loadingView.root.isVisible = isLoading && isEmpty

                        emptyListView.isVisible = isNotLoading && isEmpty

                        errorView.root.isVisible = isError && isEmpty
                    }
                    .launchIn(this)

                viewModel.events
                    .onEach { handleEvent(event = it, adapter) }
                    .launchIn(this)
            }
        }
    }

    private fun handleEvent(event: NoteOverviewEvent, adapter: NoteOverviewListAdapter) {
        Timber.d("handleEvent(event=%s)", event)
        when (event) {
            is NoteOverviewEvent.NavigateToNoteEdit -> navigateTo(
                NoteOverviewFragmentDirections.actionNoteOverviewFragmentToNoteEditFragment(
                    noteId = event.id
                )
            )
            is NoteOverviewEvent.RestoreNote -> event.handle()
            is NoteOverviewEvent.DeletionFailure -> {
                adapter.notifyItemChanged(event.pos)
                Snackbar.make(
                    binding.root,
                    R.string.fragment_note_overview_deletion_failure_message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
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