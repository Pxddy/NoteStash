package com.ph.notestash.note.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ph.notestash.R
import com.ph.notestash.common.viewbinding.viewBinding
import com.ph.notestash.databinding.FragmentNoteOverviewBinding

class NoteOverviewFragment : Fragment(R.layout.fragment_note_overview) {

    private val binding by viewBinding(FragmentNoteOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.text.text = "It worked!"
    }
}