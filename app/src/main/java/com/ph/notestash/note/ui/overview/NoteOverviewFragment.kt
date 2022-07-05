package com.ph.notestash.note.ui.overview

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ph.notestash.R
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteOverviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
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

        addNoteButton.setOnClickListener { viewModel.navigateToAddNote() }

        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .onEach { uiState ->
                    loadingView.root.isVisible = uiState is NoteOverviewUiState.Loading
                    errorView.root.isVisible = uiState is NoteOverviewUiState.Failure
                    noteList.isVisible = uiState is NoteOverviewUiState.Success
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }
}