package com.example.bookswap.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookswap.data.AppViewModel
import com.example.bookswap.ui.screens.*
import kotlinx.coroutines.launch

private const val ROUTE_FEED = "feed"
private const val ROUTE_FAVORITES = "favorites"
private const val ROUTE_MINE = "mine"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_ADD = "add"
private const val ROUTE_DETAIL = "detail/{bookId}"

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(ROUTE_FEED, "Лента", Icons.Default.Home),
    Tab(ROUTE_FAVORITES, "Избранное", Icons.Default.Favorite),
    Tab(ROUTE_MINE, "Мои", Icons.Default.Person),
    Tab(ROUTE_SETTINGS, "Настройки", Icons.Default.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSwapApp(vm: AppViewModel = viewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBars = currentRoute in tabs.map { it.route }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        Text(tabs.firstOrNull { it.route == currentRoute }?.label ?: "BookSwap")
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (tab.route == ROUTE_FAVORITES && vm.favoriteCount > 0) {
                                    BadgedBox(badge = { Badge { Text("${vm.favoriteCount}") } }) {
                                        Icon(tab.icon, contentDescription = tab.label)
                                    }
                                } else {
                                    Icon(tab.icon, contentDescription = tab.label)
                                }
                            },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == ROUTE_FEED || currentRoute == ROUTE_MINE) {
                FloatingActionButton(onClick = { navController.navigate(ROUTE_ADD) }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить объявление")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_FEED,
            modifier = Modifier.padding(padding)
        ) {
            composable(ROUTE_FEED) {
                FeedScreen(vm = vm, onBookClick = { id -> navController.navigate("detail/$id") })
            }
            composable(ROUTE_FAVORITES) {
                FavoritesScreen(vm = vm, onBookClick = { id -> navController.navigate("detail/$id") })
            }
            composable(ROUTE_MINE) {
                MyListingsScreen(
                    vm = vm,
                    onBookClick = { id -> navController.navigate("detail/$id") },
                    onDelete = { id ->
                        val removed = vm.removeBook(id)
                        if (removed != null) {
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Объявление удалено",
                                    actionLabel = "Отменить",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    vm.restoreBook(removed.first, removed.second)
                                }
                            }
                        }
                    }
                )
            }
            composable(ROUTE_SETTINGS) {
                SettingsScreen(vm = vm)
            }
            composable(ROUTE_ADD) {
                AddBookScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { book ->
                        vm.addBook(book)
                        scope.launch { snackbarHostState.showSnackbar("Объявление опубликовано") }
                    }
                )
            }
            composable(ROUTE_DETAIL) { entry ->
                BookDetailScreen(
                    vm = vm,
                    bookId = entry.arguments?.getString("bookId"),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
