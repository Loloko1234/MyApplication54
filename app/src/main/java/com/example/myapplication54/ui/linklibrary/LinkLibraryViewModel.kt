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

data class LinkItem(val url: String, var title: String = "")

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
}
