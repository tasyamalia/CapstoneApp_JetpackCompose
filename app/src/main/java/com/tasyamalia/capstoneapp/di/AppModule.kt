package com.tasyamalia.capstoneapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.tasyamalia.capstoneapp.network.BooksApi
import com.tasyamalia.capstoneapp.repository.BookRepository
import com.tasyamalia.capstoneapp.repository.FireRepository
import com.tasyamalia.capstoneapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideBookRepository(api: BooksApi) = BookRepository(api)

    @Singleton
    @Provides
    fun provideFireRepository() =
        FireRepository(queryBook = FirebaseFirestore.getInstance().collection("books"))

    @Singleton
    @Provides
    fun provideBookApi(okHttpClient: OkHttpClient): BooksApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        // Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logs request and response body
        }

        // Custom Interceptor to log the full URL
        val customInterceptor = Interceptor { chain ->
            val request = chain.request()
            val url = request.url.toString()
            println("Request URL: $url") // Log the full URL
            chain.proceed(request)
        }

        // Build OkHttpClient with interceptors
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Optional: Log request/response details
            .addInterceptor(customInterceptor)  // Log the actual URL
            .build()
    }
}