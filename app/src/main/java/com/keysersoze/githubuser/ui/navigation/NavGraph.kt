package com.keysersoze.githubuser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keysersoze.githubuser.ui.profile.ProfileScreen
import com.keysersoze.githubuser.ui.search.SearchScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val searchVm = hiltViewModel<com.keysersoze.githubuser.ui.search.SearchViewModel>()

    NavHost(
        navController = navController,
        startDestination = "search",
        modifier = modifier
    ) {
        composable("search") {
            SearchScreen(
                viewModel = searchVm,
                onNavigateToProfile = { username ->
                    navController.navigate("profile/${username.trim()}")
                }
            )
        }

        composable(
            route = "profile/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm = hiltViewModel<com.keysersoze.githubuser.ui.profile.ProfileViewModel>(backStackEntry)
            ProfileScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}