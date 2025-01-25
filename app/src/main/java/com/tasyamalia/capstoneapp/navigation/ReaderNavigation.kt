package com.tasyamalia.capstoneapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tasyamalia.capstoneapp.screens.SplashScreen
import com.tasyamalia.capstoneapp.screens.details.BookDetailScreen
import com.tasyamalia.capstoneapp.screens.details.DetailsViewModel
import com.tasyamalia.capstoneapp.screens.home.HomeScreen
import com.tasyamalia.capstoneapp.screens.home.HomeScreenViewModel
import com.tasyamalia.capstoneapp.screens.login.LoginScreen
import com.tasyamalia.capstoneapp.screens.search.BookSearchViewModel
import com.tasyamalia.capstoneapp.screens.search.SearchScreen
import com.tasyamalia.capstoneapp.screens.stats.StatsScreen
import com.tasyamalia.capstoneapp.screens.update.UpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(ReaderScreens.HomeScreen.name) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(ReaderScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(ReaderScreens.StatsScreen.name) {
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            StatsScreen(navController = navController, viewModel = viewModel)
        }
        composable(ReaderScreens.SearchScreen.name) {
            val viewModel = hiltViewModel<BookSearchViewModel>()
            SearchScreen(navController = navController, viewModel = viewModel)
        }
        val detailName = ReaderScreens.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument("bookId") {
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {
                val viewModel = hiltViewModel<DetailsViewModel>()
                BookDetailScreen(
                    navController = navController,
                    bookId = it.toString(),
                    viewModel = viewModel
                )
            }

        }
        val updateName = ReaderScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}", arguments = listOf(navArgument("bookItemId") {
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookItemId").let {
                val viewModel = hiltViewModel<DetailsViewModel>()
                UpdateScreen(
                    navController = navController,
                    bookItemId = it.toString()
                )
            }

        }
    }
}