package com.example.myapplication54.ui.linklibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LinkLibraryViewModel : ViewModel() {
    private val _links = MutableLiveData<List<String>>()
    val links: LiveData<List<String>> = _links

    private val linkList = mutableListOf<String>()

    fun addLink(link: String) {
        linkList.add(link)
        _links.value = linkList.toList()
    }
}