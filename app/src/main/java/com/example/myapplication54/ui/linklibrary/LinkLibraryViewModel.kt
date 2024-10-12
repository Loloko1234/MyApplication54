package com.example.myapplication54.ui.linklibrary

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class LinkItem(val url: String, var title: String = "", var chapter: Int = 1, var content: String = "")

class LinkLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val _links = MutableLiveData<List<LinkItem>>()
    val links: LiveData<List<LinkItem>> = _links

    private val sharedPreferences = application.getSharedPreferences("LinkLibrary", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        loadLinks()
    }

    private fun loadLinks() {
        Log.d("LinkLibraryViewModel", "loadLinks() called")
        val linksJson = sharedPreferences.getString("links", null)
        Log.d("LinkLibraryViewModel", "Loaded links JSON: $linksJson")
        if (linksJson != null) {
            val type = object : TypeToken<List<LinkItem>>() {}.type
            val loadedLinks = gson.fromJson<List<LinkItem>>(linksJson, type)
            _links.value = loadedLinks
            Log.d("LinkLibraryViewModel", "Loaded links: $loadedLinks")
        } else {
            _links.value = emptyList()
            Log.d("LinkLibraryViewModel", "No links found in SharedPreferences")
        }
    }

    private fun saveLinks() {
        val linksJson = gson.toJson(_links.value)
        Log.d("LinkLibraryViewModel", "Saving links: $linksJson")
        sharedPreferences.edit().putString("links", linksJson).commit()
        Log.d("LinkLibraryViewModel", "Links saved")
    }

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
            val currentLinks = _links.value.orEmpty().toMutableList()
            currentLinks.add(linkItem)
            _links.postValue(currentLinks)
            withContext(Dispatchers.Main) {
                saveLinks()
            }
            Log.d("LinkLibraryViewModel", "Added link: $linkItem")
        }
    }

    fun removeLink(linkItem: LinkItem) {
        val currentLinks = _links.value.orEmpty().toMutableList()
        currentLinks.remove(linkItem)
        _links.value = currentLinks
        saveLinks()
    }

    fun scrapeContent(linkItem: LinkItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("Scraping", "Starting to scrape: ${linkItem.url}")
                val doc = Jsoup.connect(linkItem.url).get()
                val paragraphs = doc.select("p")
                val content = paragraphs.joinToString("\n\n") { it.text() }
                Log.d("Scraping", "Scraped content length: ${content.length}")
                linkItem.content = content
                _links.postValue(_links.value)
            } catch (e: Exception) {
                Log.e("Scraping", "Error scraping content", e)
            }
        }
    }

    fun changeChapter(linkItem: LinkItem, newChapter: Int) {
        viewModelScope.launch {
            val updatedLinks = _links.value?.toMutableList() ?: mutableListOf()
            val index = updatedLinks.indexOfFirst { it.url == linkItem.url }
            if (index != -1) {
                val updatedUrl = updateUrlWithNewChapter(linkItem.url, newChapter)
                val updatedLinkItem = linkItem.copy(chapter = newChapter, content = "", url = updatedUrl)
                updatedLinks[index] = updatedLinkItem
                _links.value = updatedLinks
                saveLinks()
                scrapeContent(updatedLinkItem)
            }
        }
    }

    private fun updateUrlWithNewChapter(currentUrl: String, newChapter: Int): String {
        val regex = Regex("chapter-\\d+")
        return regex.replace(currentUrl, "chapter-$newChapter")
    }
}
