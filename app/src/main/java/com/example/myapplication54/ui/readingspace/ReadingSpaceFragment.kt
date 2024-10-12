package com.example.myapplication54.ui.readingspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication54.databinding.FragmentReadingSpaceBinding
import com.example.myapplication54.ui.linklibrary.LinkLibraryViewModel

class ReadingSpaceFragment : Fragment() {

    private var _binding: FragmentReadingSpaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LinkLibraryViewModel
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
        viewModel = ViewModelProvider(requireActivity()).get(LinkLibraryViewModel::class.java)

        arguments?.let {
            currentLinkItemIndex = it.getInt("linkItemIndex", -1)
            if (currentLinkItemIndex != -1) {
                updateUI()
            }
        }

        binding.buttonPreviousChapter.setOnClickListener {
            changeChapter(-1)
        }

        binding.buttonNextChapter.setOnClickListener {
            changeChapter(1)
        }
    }

    private fun updateUI() {
        viewModel.links.observe(viewLifecycleOwner) { links ->
            val linkItem = links.getOrNull(currentLinkItemIndex)
            if (linkItem != null) {
                binding.textViewTitle.text = "${linkItem.title} - Chapter ${linkItem.chapter}"
                binding.textViewContent.text = linkItem.content
                if (linkItem.content.isBlank()) {
                    binding.textViewContent.text = "Loading content..."
                    viewModel.scrapeContent(linkItem)
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
