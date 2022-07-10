package com.ph.notestash.ui.edit

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.ph.notestash.R
import com.ph.notestash.common.fragment.popBackStack
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteEditBinding
import com.ph.notestash.data.model.note.MutableNote
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class NoteEditFragment : Fragment(R.layout.fragment_note_edit) {

    private val binding by viewBinding(FragmentNoteEditBinding::bind)
    private val viewModel: NoteEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")

        toolbar.setNavigationOnClickListener { viewModel.goBack() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { handleUiState(uiState = it) }
                    .launchIn(this)

                viewModel.events
                    .onEach { handleEvent(event = it) }
                    .launchIn(this)

                title.updateNote(scope = this) { title = it }

                content.updateNote(scope = this) { content = it }
            }
        }
    }

    private fun handleUiState(uiState: NoteEditUiState) = with(binding) {
        loadingView.root.isVisible = uiState is NoteEditUiState.Loading
        errorView.root.isVisible = uiState is NoteEditUiState.Failure
        flow.isVisible = uiState is NoteEditUiState.Success

        when (uiState) {
            is NoteEditUiState.Success -> {
                if (!title.isInputMethodTarget) title.setText(uiState.title)
                if (!content.isInputMethodTarget) content.setText(uiState.content)
            }
            NoteEditUiState.Failure,
            NoteEditUiState.Loading -> Unit
        }
    }

    private fun handleEvent(event: NoteEditEvent) {
        Timber.d("handleEvent(event=%s)", event)
        when (event) {
            NoteEditEvent.NavigateBack -> popBackStack()
        }
    }

    private fun TextInputEditText.updateNote(
        scope: CoroutineScope,
        update: MutableNote.(String) -> Unit
    ) = afterTextChangedFlow()
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .onEach { viewModel.updateNote { update(it) } }
        .launchIn(scope)
}

private fun TextInputEditText.afterTextChangedFlow() = callbackFlow {
    val watcher = doAfterTextChanged { trySend(it?.toString() ?: "") }
    awaitClose { removeTextChangedListener(watcher) }
}
