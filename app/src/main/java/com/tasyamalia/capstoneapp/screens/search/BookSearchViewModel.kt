package com.tasyamalia.capstoneapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasyamalia.capstoneapp.data.Resource
import com.tasyamalia.capstoneapp.model.Item
import com.tasyamalia.capstoneapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(private val repository: BookRepository) :
    ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBook("android")
    }

    fun searchBook(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            isLoading = true
            if (query.isEmpty()) {
                return@launch
            }
            try {
                when (val response = repository.getBooks(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                    }

                    is Resource.Error -> {
                        Log.d("searchBook", "searchBook: failed")
                    }

                    else -> {}
                }
                isLoading = false
            } catch (e: Exception) {
                Log.d("searchBook", "searchBook exception: ${e.message.toString()}")
                isLoading = false
            }
        }
    }
}