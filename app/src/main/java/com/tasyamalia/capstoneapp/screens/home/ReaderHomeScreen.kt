package com.tasyamalia.capstoneapp.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.tasyamalia.capstoneapp.components.FABContent
import com.tasyamalia.capstoneapp.components.ListCard
import com.tasyamalia.capstoneapp.components.ReaderAppBar
import com.tasyamalia.capstoneapp.components.TitleSection
import com.tasyamalia.capstoneapp.model.MBook
import com.tasyamalia.capstoneapp.navigation.ReaderScreens

@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    BackHandler {}
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            ReaderAppBar(title = "Capstone App", navController = navController)
        },
        floatingActionButton = {
            FABContent {
                navController.navigate(route = ReaderScreens.SearchScreen.name)
            }
        }) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HomeContent(navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()) {
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }
    }


    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName =
        if (!email.isNullOrEmpty()) email.split(
            "@"
        )[0] else "N/A"
    Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your reading \n " + " activity right now...")
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(route = ReaderScreens.StatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currentUserName,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
                HorizontalDivider()

            }
        }

        ReadingRightNowArea(
            listOfBooks = listOfBooks,
            navController = navController,
            viewModel = viewModel
        )
        TitleSection(label = "Reading List")
        BookListArea(
            listOfBooks = listOfBooks,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun BookListArea(
    listOfBooks: List<MBook>,
    navController: NavController,
    viewModel: HomeScreenViewModel
) {
    val addedBook = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(addedBook, viewModel = viewModel) {
        navController.navigate(route = ReaderScreens.UpdateScreen.name + "/$it")
    }

}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if (viewModel.data.value.loading == true) {
            LinearProgressIndicator()
        } else {
            if (listOfBooks.isEmpty()) {
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(
                        text = "No books found. Add a book",
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                for (book in listOfBooks) {
                    ListCard(book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }

    }
}

@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController: NavController,
    viewModel: HomeScreenViewModel
) {
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(readingNowList, viewModel = viewModel) {
        navController.navigate(route = ReaderScreens.UpdateScreen.name + "/$it")
    }
}

