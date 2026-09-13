package com.example.bookswap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.Book
import com.example.bookswap.data.BookCondition
import com.example.bookswap.data.ListingType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBack: () -> Unit,
    onSave: (Book) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var exchangeWish by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ListingType.SELL) }
    var condition by remember { mutableStateOf(BookCondition.GOOD) }
    var negotiable by remember { mutableStateOf(false) }
    var conditionMenu by remember { mutableStateOf(false) }

    val titleError = title.isNotEmpty() && title.length < 2
    val canSave = title.isNotBlank() && author.isNotBlank() && city.isNotBlank() &&
        (type != ListingType.SELL || price.isNotBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новое объявление") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Тип объявления", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ListingType.values().forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { type = t },
                        label = { Text(t.label) }
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название книги *") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Минимум 2 символа") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Автор *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Выпадающий список состояния
            ExposedDropdownMenuBox(
                expanded = conditionMenu,
                onExpandedChange = { conditionMenu = !conditionMenu }
            ) {
                OutlinedTextField(
                    value = condition.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Состояние") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = conditionMenu) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = conditionMenu,
                    onDismissRequest = { conditionMenu = false }
                ) {
                    BookCondition.values().forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.label) },
                            onClick = {
                                condition = c
                                conditionMenu = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (type == ListingType.SELL) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter(Char::isDigit).take(6) },
                    label = { Text("Цена, ₽ *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = negotiable, onCheckedChange = { negotiable = it })
                    Spacer(Modifier.width(12.dp))
                    Text("Торг уместен")
                }
            }

            if (type == ListingType.TRADE) {
                OutlinedTextField(
                    value = exchangeWish,
                    onValueChange = { exchangeWish = it },
                    label = { Text("Что хотите взамен") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onSave(
                        Book(
                            id = UUID.randomUUID().toString(),
                            title = title.trim(),
                            author = author.trim(),
                            description = description.trim(),
                            price = price.toIntOrNull(),
                            type = type,
                            condition = condition,
                            city = city.trim(),
                            ownerName = "Вы",
                            negotiable = negotiable,
                            exchangeWish = exchangeWish.trim(),
                            isMine = true
                        )
                    )
                    onBack()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Опубликовать") }

            if (!canSave) {
                Text(
                    "Заполните поля со звёздочкой",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
