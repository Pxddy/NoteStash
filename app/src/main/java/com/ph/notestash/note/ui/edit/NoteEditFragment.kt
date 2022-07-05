package com.ph.notestash.note.ui.edit

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ph.notestash.R
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteEditFragment : Fragment(R.layout.fragment_note_edit) {

    private val binding by viewBinding(FragmentNoteEditBinding::bind)
    private val viewModel: NoteEditViewModel by viewModels()
}