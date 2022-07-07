package com.ph.notestash.note.ui.overview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ph.notestash.R
import com.ph.notestash.common.fragment.navigateTo
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteOverviewBinding
import com.ph.notestash.note.ui.overview.list.NoteOverviewListAdapter
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
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")

        val adapter = NoteOverviewListAdapter()
        noteList.adapter = adapter

        addNoteButton.setOnClickListener { viewModel.navigateToAddNote() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState ->
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
                    .launchIn(this)

                viewModel.events
                    .onEach { handleEvent(event = it) }
                    .launchIn(this)
            }
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
        }
    }
}