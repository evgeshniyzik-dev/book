package com.example.bookswap.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.AppViewModel

@Composable
fun SettingsScreen(vm: AppViewModel) {
    var confirmReset by remember { mutableStateOf(false) }
    val s = vm.settings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("Внешний вид")

        SettingSwitch(
            title = "Тёмная тема",
            subtitle = "Переключить оформление приложения",
            checked = s.darkTheme,
            onCheckedChange = { v -> vm.updateSettings { it.copy(darkTheme = v) } }
        )

        SettingSwitch(
            title = "Динамические цвета",
            subtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                "Палитра из обоев системы (Android 12+)"
            else "Недоступно на этой версии Android",
            checked = s.dynamicColor,
            enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            onCheckedChange = { v -> vm.updateSettings { it.copy(dynamicColor = v) } }
        )

        SettingSwitch(
            title = "Компактные карточки",
            subtitle = "Меньше текста, больше объявлений на экране",
            checked = s.compactCards,
            onCheckedChange = { v -> vm.updateSettings { it.copy(compactCards = v) } }
        )

        SettingSwitch(
            title = "Показывать город",
            subtitle = "Город в строке под карточкой",
            checked = s.showCity,
            onCheckedChange = { v -> vm.updateSettings { it.copy(showCity = v) } }
        )

        Divider(modifier = Modifier.padding(vertical = 12.dp))
        SectionTitle("Лента")

        SettingSwitch(
            title = "Только с ценой",
            subtitle = "Скрыть объявления «куплю» и «обменяю»",
            checked = s.onlyWithPrice,
            onCheckedChange = { v -> vm.updateSettings { it.copy(onlyWithPrice = v) } }
        )

        SettingSwitch(
            title = "Скрыть мои объявления",
            subtitle = "Не показывать свои записи в общей ленте",
            checked = s.hideMyListings,
            onCheckedChange = { v -> vm.updateSettings { it.copy(hideMyListings = v) } }
        )

        Divider(modifier = Modifier.padding(vertical = 12.dp))
        SectionTitle("Данные")

        Text(
            "Всего объявлений: ${vm.books.size} · в избранном: ${vm.favoriteCount}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedButton(onClick = { confirmReset = true }) {
            Text("Сбросить к демо-данным")
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "BookSwap · скелет приложения, версия 0.2",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Сбросить данные?") },
            text = { Text("Все добавленные объявления и избранное будут удалены.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetData()
                    confirmReset = false
                }) { Text("Сбросить") }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}
