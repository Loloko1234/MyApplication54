package com.example.myapplication54.ui.linklibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import android.util.Log

data class LinkItem(val url: String, var title: String = "")

class LinkLibraryViewModel : ViewModel() {
    private val _links = MutableLiveData<List<LinkItem>>()
    val links: LiveData<List<LinkItem>> = _links

    private val linkList = mutableListOf<LinkItem>()

    fun addLink(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val linkItem = LinkItem(url)
            try {
                val doc = Jsoup.connect(url).get()
                Log.d("LinkLibraryViewModel", "HTML content: ${doc.outerHtml()}")
                val novelTitleElement = doc.select("a.novel-title").first()
                Log.d("LinkLibraryViewModel", "Novel title element: $novelTitleElement")
                if (novelTitleElement != null) {
                    linkItem.title = novelTitleElement.attr("title")
                    Log.d("LinkLibraryViewModel", "Extracted title: ${linkItem.title}")
                } else {
                    linkItem.title = "Unable to fetch title"
                    Log.d("LinkLibraryViewModel", "Novel title element not found")
                }
            } catch (e: Exception) {
                linkItem.title = "Unable to fetch title"
                Log.e("LinkLibraryViewModel", "Error fetching title", e)
            }
            linkList.add(linkItem)
            _links.postValue(linkList.toList())
        }
    }
}
