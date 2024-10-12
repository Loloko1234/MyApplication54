package com.example.myapplication54.ui.linklibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication54.databinding.FragmentLinkLibraryBinding

class LinkLibraryFragment : Fragment() {

    private var _binding: FragmentLinkLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LinkLibraryViewModel
    private lateinit var adapter: LinkAdapter

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
        viewModel = ViewModelProvider(this).get(LinkLibraryViewModel::class.java)

        setupRecyclerView()
        setupAddButton()
        observeLinks()
    }

    private fun setupRecyclerView() {
        adapter = LinkAdapter(
            onItemClick = { linkItem ->
                // Handle item click (e.g., open the link)
            },
            onRemoveClick = { linkItem ->
                viewModel.removeLink(linkItem)
            }
        )
        binding.recyclerViewLinks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewLinks.adapter = adapter
    }

    private fun setupAddButton() {
        binding.buttonAddLink.setOnClickListener {
            val link = binding.editTextLink.text.toString().trim()
            if (link.isNotEmpty()) {
                viewModel.addLink(link)
                binding.editTextLink.text.clear()
            }
        }
    }

    private fun observeLinks() {
        viewModel.links.observe(viewLifecycleOwner) { links ->
            adapter.submitList(links)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
