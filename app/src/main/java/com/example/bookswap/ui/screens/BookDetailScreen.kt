package com.example.bookswap.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.AppViewModel
import com.example.bookswap.data.ListingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    vm: AppViewModel,
    bookId: String?,
    onBack: () -> Unit
) {
    val book = bookId?.let { vm.getBook(it) }
    val context = LocalContext.current
    var contactShown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "Объявление", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (book != null) {
                        IconButton(onClick = { vm.toggleFavorite(book.id) }) {
                            Icon(
                                if (vm.isFavorite(book.id)) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                                contentDescription = "Избранное"
                            )
                        }
                        IconButton(onClick = {
                            val text = "${book.title} — ${book.author}, ${book.city}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Поделиться"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Поделиться")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (book == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Объявление не найдено") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(book.title, style = MaterialTheme.typography.headlineSmall)
            Text(book.author, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))
            InfoRow("Тип", book.type.label)
            InfoRow("Состояние", book.condition.label)
            InfoRow("Город", book.city)
            when (book.type) {
                ListingType.SELL -> {
                    InfoRow("Цена", "${book.price ?: 0} ₽")
                    InfoRow("Торг", if (book.negotiable) "Уместен" else "Без торга")
                }
                ListingType.TRADE -> InfoRow("Хочет взамен", book.exchangeWish.ifBlank { "Любое" })
                ListingType.BUY -> InfoRow("Статус", "Ищет книгу")
            }

            Spacer(Modifier.height(16.dp))
            Text("Описание", style = MaterialTheme.typography.titleSmall)
            Text(book.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))
            Text("Автор объявления: ${book.ownerName}", style = MaterialTheme.typography.labelLarge)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { contactShown = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Показать контакт") }

            if (contactShown) {
                Spacer(Modifier.height(12.dp))
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text("Контакт (заглушка)", style = MaterialTheme.typography.titleSmall)
                        Text("+7 900 000-00-00", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "В реальном приложении здесь будет чат или подтянутый с сервера контакт.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(140.dp)
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
