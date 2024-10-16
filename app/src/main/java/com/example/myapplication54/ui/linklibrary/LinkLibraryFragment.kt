package com.example.myapplication54.ui.linklibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.myapplication54.databinding.FragmentLinkLibraryBinding
import com.example.myapplication54.R

class LinkLibraryFragment : Fragment() {

    private var _binding: FragmentLinkLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LinkLibraryViewModel by lazy { ViewModelProvider(requireActivity())[LinkLibraryViewModel::class.java] }
    private val adapter: LinkAdapter by lazy { createAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupAddButton()
        observeLinks()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewLinks.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@LinkLibraryFragment.adapter
        }
    }

    private fun setupAddButton() = with(binding) {
        buttonAddLink.setOnClickListener {
            editTextLink.text.toString().trim().takeIf { it.isNotEmpty() }?.let { link ->
                viewModel.addLink(link)
                editTextLink.text.clear()
            }
        }
    }

    private fun observeLinks() {
        viewModel.links.observe(viewLifecycleOwner, adapter::submitList)
    }

    private fun createAdapter() = LinkAdapter(
        onItemClick = { linkItem ->
            viewModel.links.value?.indexOf(linkItem)?.let { index ->
                findNavController().navigate(
                    R.id.action_nav_link_library_to_readingSpaceFragment,
                    Bundle().apply { putInt("linkItemIndex", index) }
                )
            }
        },
        onRemoveClick = { linkItem ->
            viewModel.removeLink(linkItem)
        }
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
