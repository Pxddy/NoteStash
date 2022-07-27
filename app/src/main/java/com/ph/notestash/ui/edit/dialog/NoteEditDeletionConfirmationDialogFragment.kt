package com.ph.notestash.ui.edit.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ph.notestash.R
import com.ph.notestash.common.fragmentTag
import com.ph.notestash.ui.edit.NoteEditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteEditDeletionConfirmationDialogFragment : DialogFragment() {

    private val viewModel by viewModels<NoteEditViewModel>({ requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.fragment_note_edit_deletion_confirmation_dialog_title)
            .setMessage(R.string.fragment_note_edit_deletion_confirmation_dialog_msg)
            .setPositiveButton(R.string.fragment_note_edit_deletion_confirmation_dialog_positive_button_text) { _, _ -> viewModel.deleteNote() }
            .setNegativeButton(R.string.fragment_note_edit_deletion_confirmation_dialog_negative_button_text) { _, _ -> }
            .create()
    }

    companion object {
        val TAG = fragmentTag<NoteEditDeletionConfirmationDialogFragment>()

        fun newInstance() = NoteEditDeletionConfirmationDialogFragment()
    }
}