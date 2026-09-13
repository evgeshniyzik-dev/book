package com.example.bookswap.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.*
import com.example.bookswap.ui.components.BookCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    vm: AppViewModel,
    onBookClick: (String) -> Unit
) {
    var filtersExpanded by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Поиск
        OutlinedTextField(
            value = vm.query,
            onValueChange = { vm.query = it },
            placeholder = { Text("Название, автор или город") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (vm.query.isNotEmpty()) {
                    IconButton(onClick = { vm.query = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Чипсы типа объявления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = vm.typeFilter == null,
                onClick = { vm.applyTypeFilter(null) },
                label = { Text("Все") }
            )
            ListingType.values().forEach { t ->
                FilterChip(
                    selected = vm.typeFilter == t,
                    onClick = { vm.applyTypeFilter(if (vm.typeFilter == t) null else t) },
                    label = { Text(t.label) }
                )
            }
        }

        // Панель управления: сортировка, вид, доп. фильтры
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                TextButton(onClick = { sortMenuOpen = true }) {
                    Text(vm.sortOrder.label)
                }
                DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                    SortOrder.values().forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.label) },
                            onClick = {
                                vm.setSort(order)
                                sortMenuOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                Icon(Icons.Default.Tune, contentDescription = "Фильтры")
            }
            IconButton(onClick = { vm.toggleViewMode() }) {
                Icon(
                    Icons.Default.List,
                    contentDescription = if (vm.viewMode == ViewMode.LIST) "Сетка" else "Список"
                )
            }
        }

        // Раскрывающиеся фильтры
        if (filtersExpanded) {
            ExtraFilters(vm)
        }

        Text(
            text = "Найдено: ${vm.visibleBooks.size}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        if (vm.visibleBooks.isEmpty()) {
            EmptyState(onReset = { vm.resetFilters() })
            return@Column
        }

        when (vm.viewMode) {
            ViewMode.LIST -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vm.visibleBooks, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        isFavorite = vm.isFavorite(book.id),
                        compact = vm.settings.compactCards,
                        showCity = vm.settings.showCity,
                        onClick = { onBookClick(book.id) },
                        onFavoriteClick = { vm.toggleFavorite(book.id) }
                    )
                }
            }

            ViewMode.GRID -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vm.visibleBooks, key = { it.id }) { book ->
                    BookCard(
                        book = book,
                        isFavorite = vm.isFavorite(book.id),
                        compact = true,
                        showCity = vm.settings.showCity,
                        onClick = { onBookClick(book.id) },
                        onFavoriteClick = { vm.toggleFavorite(book.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtraFilters(vm: AppViewModel) {
    Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Состояние", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = vm.conditionFilter == null,
                    onClick = { vm.applyConditionFilter(null) },
                    label = { Text("Любое") }
                )
                BookCondition.values().forEach { c ->
                    FilterChip(
                        selected = vm.conditionFilter == c,
                        onClick = { vm.applyConditionFilter(if (vm.conditionFilter == c) null else c) },
                        label = { Text(c.label) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Цена до: ${vm.maxPrice.toInt()} ₽", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = vm.maxPrice,
                onValueChange = { vm.applyMaxPrice(it) },
                valueRange = 0f..2000f,
                steps = 19
            )

            TextButton(onClick = { vm.resetFilters() }) {
                Text("Сбросить фильтры")
            }
        }
    }
}

@Composable
private fun EmptyState(onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Ничего не найдено", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Попробуйте изменить запрос или сбросить фильтры",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReset) { Text("Сбросить всё") }
    }
}
