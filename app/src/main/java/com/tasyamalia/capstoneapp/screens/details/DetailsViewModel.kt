package com.tasyamalia.capstoneapp.screens.details

import androidx.lifecycle.ViewModel
import com.tasyamalia.capstoneapp.data.Resource
import com.tasyamalia.capstoneapp.model.Item
import com.tasyamalia.capstoneapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    suspend fun getBookInfo(bookId: String): Resource<Item> {
        return repository.getBookInfo(bookId)
    }
}