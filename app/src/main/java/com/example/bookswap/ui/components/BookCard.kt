package com.example.bookswap.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.Book
import com.example.bookswap.data.ListingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(
    book: Book,
    isFavorite: Boolean,
    compact: Boolean,
    showCity: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(if (compact) 10.dp else 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = if (compact) MaterialTheme.typography.titleSmall
                    else MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!compact) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TypeBadge(book.type)
                    AssistChip(onClick = {}, label = { Text(book.condition.label) })
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    text = buildString {
                        append(
                            when (book.type) {
                                ListingType.SELL -> "${book.price ?: 0} ₽" +
                                    if (book.negotiable) " · торг" else ""
                                ListingType.BUY -> "Ищет книгу"
                                ListingType.TRADE -> "Обмен: ${book.exchangeWish.ifBlank { "любое" }}"
                            }
                        )
                        if (showCity) append(" · ${book.city}")
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TypeBadge(type: ListingType) {
    val color = when (type) {
        ListingType.SELL -> MaterialTheme.colorScheme.primaryContainer
        ListingType.BUY -> MaterialTheme.colorScheme.tertiaryContainer
        ListingType.TRADE -> MaterialTheme.colorScheme.secondaryContainer
    }
    Surface(color = color, shape = MaterialTheme.shapes.small) {
        Text(
            text = type.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
