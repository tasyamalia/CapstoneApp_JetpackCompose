package com.tasyamalia.capstoneapp.screens.update

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.tasyamalia.capstoneapp.R
import com.tasyamalia.capstoneapp.components.InputField
import com.tasyamalia.capstoneapp.components.RatingBar
import com.tasyamalia.capstoneapp.components.ReaderAppBar
import com.tasyamalia.capstoneapp.components.RoundedButton
import com.tasyamalia.capstoneapp.components.ShowToast
import com.tasyamalia.capstoneapp.data.DataOrException
import com.tasyamalia.capstoneapp.model.MBook
import com.tasyamalia.capstoneapp.navigation.ReaderScreens
import com.tasyamalia.capstoneapp.screens.home.HomeScreenViewModel
import com.tasyamalia.capstoneapp.utils.formatDate

@Composable
fun UpdateScreen(
    navController: NavController,
    bookItemId: String,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    // viewModel.getAllBooksFromDatabase()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ReaderAppBar(
                title = "Update Book",
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
                val bookInfo =
                    produceState<DataOrException<List<MBook>, Boolean, Exception>>(
                        initialValue = DataOrException(
                            data = emptyList(), true, Exception("")
                        )
                    ) {
                        value = viewModel.data.value
                    }.value
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(top = 3.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (bookInfo.loading == true) {
                            LinearProgressIndicator()
                            bookInfo.loading = false
                        } else {
                            Surface(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .fillMaxWidth()
                                    .shadow(4.dp, CircleShape),
                                shape = CircleShape,
                                color = Color.White
                            ) {
                                ShowBookUpdate(
                                    bookInfo = viewModel.data.value,
                                    bookItemId = bookItemId
                                )


                            }
                            ShowSimpleForm(
                                book = viewModel.data.value.data?.first { mBook ->
                                    mBook.googleBookId == bookItemId
                                },
                                navController = navController
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ShowSimpleForm(book: MBook?, navController: NavController) {
    val context = LocalContext.current
    val notesText = remember {
        mutableStateOf(book?.notes ?: "")
    }
    val isStartedReading = remember {
        mutableStateOf(false)
    }
    val isFinishedReading = remember {
        mutableStateOf(false)
    }
    val ratingVal = remember {
        mutableIntStateOf(book?.rating?.toInt() ?: 0)
    }
    SimpleForm(
        defaultValue = notesText.value
    ) { note ->
        notesText.value = note
    }
    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        TextButton(
            modifier = Modifier
                .weight(1f),

            onClick = {
                isStartedReading.value = true
            },
            enabled = book?.startedReading == null
        ) {
            if (book?.startedReading == null) {
                if (!isStartedReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Start Reading!", modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started On: ${formatDate(book.startedReading!!)}")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                isFinishedReading.value = true
            },
            enabled = book?.finishedReading == null
        ) {
            if (book?.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read")
                } else {
                    Text(text = "Finished Reading!")
                }
            } else {
                Text(text = "Finished on:  ${formatDate(book.finishedReading!!)}")
            }
        }
    }
    Text(text = "Rating", modifier = Modifier.padding(bottom = 3.dp))
    book?.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.intValue = rating
        }
    }
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row {
        val changedNotes = book?.notes != notesText.value
        val changedRating = book?.rating?.toInt() != ratingVal.intValue
        val isFinishedTimeStamp =
            if (isFinishedReading.value) Timestamp.now() else book?.finishedReading
        val isStartedTimeStamp =
            if (isStartedReading.value) Timestamp.now() else book?.startedReading
        val bookUpdate =
            changedNotes || changedRating || isStartedReading.value || isFinishedReading.value

        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTimeStamp,
            "rating" to ratingVal.intValue,
            "notes" to notesText.value
        ).toMap()
        RoundedButton(label = "Update") {
            if (bookUpdate) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book?.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener { task ->
                        ShowToast(context = context, msg = "Book Updated Successfully!")
                        navController.navigate(route = ReaderScreens.HomeScreen.name)
                        Log.d("UPDATE", "Update : ${task.result}")
                    }
                    .addOnFailureListener {
                        Log.d("UPDATE", "ON FAILURE : ${it}")
                    }
            }
        }
        Spacer(modifier = Modifier.width(100.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }
        if (openDialog.value) {
            ShowAlertDialog(
                message = stringResource(R.string.sure) + "\n" + stringResource(R.string.action),
                openDialog = openDialog
            ) {
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book?.id!!)
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            openDialog.value = false
                            navController.navigate(ReaderScreens.HomeScreen.name)
                        }
                    }

            }
        }
        RoundedButton(label = "Delete") {
            openDialog.value = true
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAlertDialog(message: String, openDialog: MutableState<Boolean> , onYesPressed: () -> Unit) {
    if (openDialog.value) {
        BasicAlertDialog(onDismissRequest = { openDialog.value = false }, content = {
            Surface(
                color = Color.White,
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = message)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        TextButton(onClick = {
                            onYesPressed.invoke()
                        }) {
                            Text(text = "Yes")
                        }
                        TextButton(onClick = {
                            openDialog.value = false
                        }) {
                            Text(text = "No")
                        }
                    }
                }
            }


        })
    }
}


@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
) {
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) {
            textFieldValue.value.trim().isNotEmpty()
        }
        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            valueState = textFieldValue,
            labelId = "Enter Your Thoughts",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyboardController?.hide()
            })
    }
}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookItemId: String) {
    Row() {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null) {
            Column(
                modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center
            ) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookItemId
                }, onPressedDetail = {})
            }
        }
    }
}

@Composable
fun CardListItem(book: MBook, onPressedDetail: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 8.dp)
            .clip(
                RoundedCornerShape(20.dp)
            )
            .clickable {

            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberAsyncImagePainter(book.photoUrl.toString()),
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 120.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authors.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                )
                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
                )
            }
        }

    }
}

