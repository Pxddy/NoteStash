package com.ph.notestash.ui.overview.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ph.notestash.R
import com.ph.notestash.databinding.FragmentNoteOverviewSortDialogBinding
import com.ph.notestash.data.model.sort.SortNoteBy
import com.ph.notestash.data.model.sort.SortOrder
import com.pxddy.simpleviewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NoteOverviewSortDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentNoteOverviewSortDialogBinding::bind)
    private val viewModel by viewModels<NoteOverviewSortDialogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentNoteOverviewSortDialogBinding.inflate(
        inflater,
        container,
        false
    ).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        (dialog as? BottomSheetDialog)?.behavior?.state = STATE_EXPANDED
    }

    private fun bindViewModel() = with(binding) {
        Timber.d("bindViewModel()")
        radioGroupSortBy.setOnCheckedChangeListener { _, checkedId -> setSortedNoteBy(checkedId) }
        radioGroupSortOrder.setOnCheckedChangeListener { _, checkedId -> setSortOrder(checkedId) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { handleUiState(uiState = it) }
                    .launchIn(this)
            }
        }
    }

    private fun FragmentNoteOverviewSortDialogBinding.handleUiState(
        uiState: NoteOverviewSortDialogUiState
    ) = with(uiState) {
        radioGroupSortBy.check(sortNoteBy.radioButtonId)
        radioGroupSortOrder.check(sortOrder.radioButtonId)
    }

    private fun setSortedNoteBy(checkedId: Int) {
        when (checkedId) {
            R.id.radio_button_sort_by_title -> SortNoteBy.Title
            R.id.radio_button_sort_by_created -> SortNoteBy.CreatedAt
            R.id.radio_button_sort_by_modified -> SortNoteBy.ModifiedAt
            else -> null
        }?.let { viewModel.setSortedNoteBy(it) }
    }

    private fun setSortOrder(checkedId: Int) {
        when (checkedId) {
            R.id.radio_button_sort_order_ascending -> SortOrder.Ascending
            R.id.radio_button_sort_order_descending -> SortOrder.Descending
            else -> null
        }?.let { viewModel.setSortOrder(it) }
    }

    private val SortNoteBy.radioButtonId: Int
        get() = when (this) {
            SortNoteBy.Title -> R.id.radio_button_sort_by_title
            SortNoteBy.CreatedAt -> R.id.radio_button_sort_by_created
            SortNoteBy.ModifiedAt -> R.id.radio_button_sort_by_modified
        }

    private val SortOrder.radioButtonId: Int
        get() = when (this) {
            SortOrder.Ascending -> R.id.radio_button_sort_order_ascending
            SortOrder.Descending -> R.id.radio_button_sort_order_descending
        }
}