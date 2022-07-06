package com.ph.notestash.note.ui.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.textfield.TextInputEditText
import com.ph.notestash.R
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteEditBinding
import com.ph.notestash.note.core.model.MutableNote
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .filterIsInstance<NoteEditUiState.Success>()
                    .onEach { uiState ->
                        if (!title.isInputMethodTarget) title.setText(uiState.title)
                        if (!content.isInputMethodTarget) content.setText(uiState.content)
                    }
                    .launchIn(this)

                title.updateNote(scope = this) { title = it }

                content.updateNote(scope = this) { content = it }
            }
        }
    }

    private fun TextInputEditText.updateNote(
        scope: CoroutineScope,
        update: MutableNote.(String) -> Unit
    ) = afterTextChangedFlow()
        .debounce(300.milliseconds)
        .onEach { viewModel.updateNote { update(it) } }
        .launchIn(scope)
}

private fun TextInputEditText.afterTextChangedFlow() = callbackFlow {
    val watcher = doAfterTextChanged { trySend(it?.toString() ?: "") }
    awaitClose { removeTextChangedListener(watcher) }
}
