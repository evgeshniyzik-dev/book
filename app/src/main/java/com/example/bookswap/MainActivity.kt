package com.example.bookswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.AppViewModel
import com.example.bookswap.nav.BookSwapApp
import com.example.bookswap.ui.theme.BookSwapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            BookSwapTheme(
                darkTheme = vm.settings.darkTheme,
                dynamicColor = vm.settings.dynamicColor
            ) {
                BookSwapApp(vm = vm)
            }
        }
    }
}
