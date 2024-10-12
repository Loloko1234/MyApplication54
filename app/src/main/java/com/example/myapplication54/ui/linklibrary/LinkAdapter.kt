package com.example.myapplication54.ui.linklibrary

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication54.databinding.ItemLinkBinding

class LinkAdapter : ListAdapter<LinkItem, LinkAdapter.LinkViewHolder>(LinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding = ItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LinkViewHolder(private val binding: ItemLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(linkItem: LinkItem) {
            binding.textViewTitle.text = linkItem.title
            binding.textViewUrl.text = linkItem.url
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkItem.url))
                it.context.startActivity(intent)
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
