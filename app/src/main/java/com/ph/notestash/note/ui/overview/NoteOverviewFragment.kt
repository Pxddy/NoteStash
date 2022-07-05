package com.ph.notestash.note.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ph.notestash.R
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteOverviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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
        binding.text.text = "It worked!"
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")
        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .onEach { uiState ->
                    Timber.d("uiState=%s", uiState)
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }
}