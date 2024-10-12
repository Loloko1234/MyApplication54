package com.example.myapplication54.ui

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
            val linkItemIndex = it.getInt("linkItemIndex", -1)
            if (linkItemIndex != -1) {
                viewModel.links.observe(viewLifecycleOwner) { links ->
                    val linkItem = links[linkItemIndex]
                    binding.textViewTitle.text = "${linkItem.title} - Chapter ${linkItem.chapter}"
                    binding.textViewContent.text = linkItem.content
                    if (linkItem.content.isBlank()) {
                        binding.textViewContent.text = "Loading content..."
                        viewModel.scrapeContent(linkItem)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
