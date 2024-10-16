package com.example.myapplication54.ui.readingspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication54.databinding.FragmentReadingSpaceBinding
import com.example.myapplication54.ui.linklibrary.LinkLibraryViewModel

class ReadingSpaceFragment : Fragment() {

    private var _binding: FragmentReadingSpaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LinkLibraryViewModel by activityViewModels()
    private var currentLinkItemIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadingSpaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentLinkItemIndex = arguments?.getInt("linkItemIndex", -1) ?: -1
        if (currentLinkItemIndex != -1) {
            updateUI()
        }

        binding.buttonPreviousChapter.setOnClickListener { changeChapter(-1) }
        binding.buttonNextChapter.setOnClickListener { changeChapter(1) }
    }

    private fun updateUI() {
        viewModel.links.observe(viewLifecycleOwner) { links ->
            links.getOrNull(currentLinkItemIndex)?.let { linkItem ->
                binding.textViewTitle.text = "${linkItem.title} - Chapter ${linkItem.chapter}"
                if (linkItem.content.isBlank()) {
                    binding.textViewContent.text = "Loading content..."
                    viewModel.scrapeContent(linkItem)
                } else {
                    binding.textViewContent.text = linkItem.content
                }
            }
        }
    }

    private fun changeChapter(delta: Int) {
        viewModel.links.value?.getOrNull(currentLinkItemIndex)?.let { linkItem ->
            val newChapter = linkItem.chapter + delta
            if (newChapter > 0) {
                viewModel.changeChapter(linkItem, newChapter)
                updateUI()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
