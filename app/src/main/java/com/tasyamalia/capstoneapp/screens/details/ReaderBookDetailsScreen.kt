package com.tasyamalia.capstoneapp.screens.details

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tasyamalia.capstoneapp.components.ReaderAppBar
import com.tasyamalia.capstoneapp.components.RoundedButton
import com.tasyamalia.capstoneapp.data.Resource
import com.tasyamalia.capstoneapp.model.Item
import com.tasyamalia.capstoneapp.model.MBook

@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ReaderAppBar(
                title = "Book Detail",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                navController = navController,
                showProfile = false
            ) {
                navController.popBackStack()
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = viewModel.getBookInfo(bookId)
                }.value
                Log.d("TESS", "DISINII ${bookInfo.data?.volumeInfo?.title}")
                Log.d("TESS", "DISINIIy ${bookId}")
                if (bookInfo.data == null) {
                    LinearProgressIndicator()
                    Text(text = "Loading...")
                } else {
                    ShowBookDetails(bookInfo, navController)
                }

            }
        }
    }
}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id

    Card(
        modifier = Modifier.padding(34.dp),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(bookData?.imageLinks?.thumbnail),
            contentDescription = "Book Image",
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .padding(1.dp)
        )
    }
    Text(
        text = bookData?.title.toString(),
        style = MaterialTheme.typography.labelLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 19,
    )
    Text(text = "Authors: ${bookData?.authors.toString()}")
    Text(text = "Page Count: ${bookData?.pageCount.toString()}")
    Text(
        text = "Categories: ${bookData?.categories.toString()}",
        style = MaterialTheme.typography.labelMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = "Published: ${bookData?.publishedDate.toString()}",
        style = MaterialTheme.typography.labelMedium
    )
    Spacer(modifier = Modifier.height(5.dp))
    val cleanDescription =
        HtmlCompat.fromHtml(bookData?.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
    val localDims = LocalContext.current.resources.displayMetrics
    Surface(
        modifier = Modifier
            .height(localDims.heightPixels.dp.times(0.09f))
            .padding(4.dp), shape = RectangleShape, border = BorderStroke(1.dp, Color.Gray)
    ) {
        LazyColumn(modifier = Modifier.padding(3.dp)) {
            item {
                Text(text = cleanDescription)
            }
        }
    }

    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        RoundedButton(label = "Save") {
            val book = MBook(
                title = bookData?.title,
                authors = bookData?.authors.toString(),
                description = bookData?.description,
                categories = bookData?.categories.toString(),
                notes = "",
                photoUrl = bookData?.imageLinks?.thumbnail,
                publishedDate = bookData?.publishedDate,
                pageCount = bookData?.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            saveToFirebase(book, navController)

        }
        Spacer(modifier = Modifier.width(25.dp))
        RoundedButton(label = "Cancel") {
            navController.popBackStack()
        }
    }
}

fun saveToFirebase(book: MBook, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")
    if (book.toString().isNotEmpty()) {
        dbCollection.add(book).addOnSuccessListener { documentRef ->
            val docId = documentRef.id
            dbCollection.document(docId).update(hashMapOf("id" to docId) as Map<String, Any>)
        }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.popBackStack()
                }
            }
            .addOnFailureListener {
                Log.d("Add Firebase", "saveToFirebase: Error updating doc $it")
            }
    }
}
