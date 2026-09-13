package com.example.bookswap.data

enum class ListingType(val label: String) {
    SELL("Продать"),
    BUY("Купить"),
    TRADE("Обменять")
}

enum class BookCondition(val label: String) {
    NEW("Новая"),
    GOOD("Хорошее"),
    WORN("Потрёпанная")
}

enum class SortOrder(val label: String) {
    NEWEST("Сначала новые"),
    PRICE_ASC("Дешевле"),
    PRICE_DESC("Дороже"),
    TITLE("По названию")
}

enum class ViewMode { LIST, GRID }

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val price: Int?,
    val type: ListingType,
    val condition: BookCondition,
    val city: String,
    val ownerName: String,
    val negotiable: Boolean = false,
    val exchangeWish: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isMine: Boolean = false
)

/** Настройки приложения — всё, что переключается на экране «Настройки». */
data class AppSettings(
    val darkTheme: Boolean = false,
    val dynamicColor: Boolean = true,
    val compactCards: Boolean = false,
    val onlyWithPrice: Boolean = false,
    val hideMyListings: Boolean = false,
    val showCity: Boolean = true
)
