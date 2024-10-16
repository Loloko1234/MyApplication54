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
        val linksJson = sharedPreferences.getString("links", null)
        if (linksJson != null) {
            val type = object : TypeToken<List<LinkItem>>() {}.type
            _links.value = gson.fromJson(linksJson, type)
        } else {
            _links.value = emptyList()
        }
    }

    private fun saveLinks() {
        val linksJson = gson.toJson(_links.value)
        sharedPreferences.edit().putString("links", linksJson).apply()
    }

    fun addLink(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val linkItem = LinkItem(url)
            try {
                val doc = Jsoup.connect(url).get()
                linkItem.title = doc.select("a.novel-title").first()?.attr("title") ?: "Unable to fetch title"
            } catch (e: Exception) {
                linkItem.title = "Unable to fetch title"
            }
            _links.postValue(_links.value.orEmpty() + linkItem)
            withContext(Dispatchers.Main) {
                saveLinks()
            }
        }
    }

    fun removeLink(linkItem: LinkItem) {
        _links.value = _links.value.orEmpty() - linkItem
        saveLinks()
    }

    fun scrapeContent(linkItem: LinkItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(linkItem.url).get()
                linkItem.content = doc.select("p").joinToString("\n\n") { it.text() }
                _links.postValue(_links.value)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun changeChapter(linkItem: LinkItem, newChapter: Int) {
        viewModelScope.launch {
            val updatedLinks = _links.value?.toMutableList() ?: mutableListOf()
            val index = updatedLinks.indexOfFirst { it.url == linkItem.url }
            if (index != -1) {
                val updatedUrl = linkItem.url.replace(Regex("chapter-\\d+"), "chapter-$newChapter")
                val updatedLinkItem = linkItem.copy(chapter = newChapter, content = "", url = updatedUrl)
                updatedLinks[index] = updatedLinkItem
                _links.value = updatedLinks
                saveLinks()
                scrapeContent(updatedLinkItem)
            }
        }
    }
}
