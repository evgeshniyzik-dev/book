package com.example.bookswap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.AppViewModel
import com.example.bookswap.ui.components.BookCard

@Composable
fun FavoritesScreen(vm: AppViewModel, onBookClick: (String) -> Unit) {
    val items = vm.favoriteBooks
    if (items.isEmpty()) {
        CenterMessage("Пока пусто", "Отмечайте объявления сердечком в ленте")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { book ->
            BookCard(
                book = book,
                isFavorite = true,
                compact = vm.settings.compactCards,
                showCity = vm.settings.showCity,
                onClick = { onBookClick(book.id) },
                onFavoriteClick = { vm.toggleFavorite(book.id) }
            )
        }
    }
}

@Composable
fun MyListingsScreen(
    vm: AppViewModel,
    onBookClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val items = vm.myBooks
    if (items.isEmpty()) {
        CenterMessage("Нет своих объявлений", "Нажмите «+», чтобы создать первое")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { book ->
            Column {
                BookCard(
                    book = book,
                    isFavorite = vm.isFavorite(book.id),
                    compact = vm.settings.compactCards,
                    showCity = vm.settings.showCity,
                    onClick = { onBookClick(book.id) },
                    onFavoriteClick = { vm.toggleFavorite(book.id) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDelete(book.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Удалить")
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterMessage(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}
