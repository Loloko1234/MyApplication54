package com.example.myapplication54.ui.linklibrary

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication54.databinding.ItemLinkBinding
import android.view.View

class LinkAdapter(
    private val onItemClick: (LinkItem) -> Unit,
    private val onRemoveClick: (LinkItem) -> Unit
) : ListAdapter<LinkItem, LinkAdapter.LinkViewHolder>(LinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding = ItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LinkViewHolder(private val binding: ItemLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(linkItem: LinkItem) {
            binding.textViewTitle.text = linkItem.title
            binding.textViewChapter.text = "Chapter ${linkItem.chapter}"
            binding.textViewChapter.visibility = View.VISIBLE
            binding.textViewUrl.visibility = View.GONE
            binding.root.setOnClickListener {
                onItemClick(linkItem)
            }
            binding.buttonRemove.setOnClickListener {
                onRemoveClick(linkItem)
            }
        }
    }

    class LinkDiffCallback : DiffUtil.ItemCallback<LinkItem>() {
        override fun areItemsTheSame(oldItem: LinkItem, newItem: LinkItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: LinkItem, newItem: LinkItem): Boolean {
            return oldItem == newItem
        }
    }
}
