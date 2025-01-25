package com.tasyamalia.capstoneapp.repository

import android.util.Log
import com.tasyamalia.capstoneapp.data.Resource
import com.tasyamalia.capstoneapp.model.Item
import com.tasyamalia.capstoneapp.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {
    //    private val dataOrException = DataOrException<List<Item>, Boolean, Exception>()
//    private val bookInfoDataOrException = DataOrException<Item, Boolean, Exception>()
//    suspend fun getBooks(searchQuery: String): DataOrException<List<Item>, Boolean, Exception> {
//        try {
//            dataOrException.loading = true
//            dataOrException.data = api.getAllBooks(searchQuery).items
//            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
//        } catch (e: Exception) {
//            dataOrException.e = e
//        }
//        return dataOrException
//    }
//
//    suspend fun getBookInfo(bookId: String): DataOrException<Item, Boolean, Exception> {
//        val response = try {
//            bookInfoDataOrException.loading = true
//            bookInfoDataOrException.data = api.getBookInfo(bookId = bookId)
//            if (bookInfoDataOrException.data!!.toString()
//                    .isNotEmpty()
//            ) bookInfoDataOrException.loading = false
//            else {
//            }
//        } catch (e: Exception) {
//            bookInfoDataOrException.e = e
//        }
//        return bookInfoDataOrException
//    }
    suspend fun getBooks(searchQuery: String): Resource<List<Item>> {
        return try {
            Resource.Loading(data = true)
            val itemList = api.getAllBooks(searchQuery).items
            if (itemList.isNotEmpty()) {
                Resource.Loading(data = false)
            }
            Resource.Success(data = itemList)
        } catch (exception: Exception) {
            Resource.Error(message = exception.message.toString())
        }
    }

    suspend fun getBookInfo(bookId: String): Resource<Item> {
        val response = try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId)

        } catch (e: Exception) {
            Log.d("F", "APPAAA ${e.message.toString()}")
            return Resource.Error(message = "An error occurred ${e.message.toString()}")
        }
        Resource.Loading(data = false)
        Log.d("A", "APPAAA INI ${response.volumeInfo.title}")
        return Resource.Success(data = response)
    }
}