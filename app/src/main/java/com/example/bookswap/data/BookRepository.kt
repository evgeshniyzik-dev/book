package com.example.bookswap.data

import java.util.UUID

/**
 * Хранилище в памяти. Данные пропадают при перезапуске — это нормально для скелета.
 * Точка замены на реальный backend: сохранить сигнатуры методов,
 * внутри вызывать Retrofit / Room. UI-слой переписывать не придётся.
 */
class BookRepository {

    private val books = mutableListOf<Book>()

    init {
        reset()
    }

    fun reset() {
        books.clear()
        books.addAll(seed())
    }

    fun getAll(): List<Book> = books.toList()

    fun getById(id: String): Book? = books.find { it.id == id }

    fun add(book: Book) {
        books.add(0, book)
    }

    fun remove(id: String) {
        books.removeAll { it.id == id }
    }

    fun restore(book: Book, position: Int) {
        val index = position.coerceIn(0, books.size)
        books.add(index, book)
    }

    fun indexOf(id: String): Int = books.indexOfFirst { it.id == id }

    private fun seed(): List<Book> {
        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L
        return listOf(
            Book(
                id = UUID.randomUUID().toString(),
                title = "Мастер и Маргарита",
                author = "Михаил Булгаков",
                description = "Читал один раз, обложка твёрдая, страницы целые.",
                price = 450,
                type = ListingType.SELL,
                condition = BookCondition.GOOD,
                city = "Москва",
                ownerName = "Игорь",
                negotiable = true,
                createdAt = now - day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "1984",
                author = "Джордж Оруэлл",
                description = "Ищу бумажное издание, любое. Готова забрать сама.",
                price = null,
                type = ListingType.BUY,
                condition = BookCondition.GOOD,
                city = "Санкт-Петербург",
                ownerName = "Анна",
                createdAt = now - 2 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Идиот",
                author = "Фёдор Достоевский",
                description = "Классическое издание, читаемое состояние.",
                price = null,
                type = ListingType.TRADE,
                condition = BookCondition.WORN,
                city = "Казань",
                ownerName = "Сергей",
                exchangeWish = "Любая научная фантастика",
                createdAt = now - 3 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Чистый код",
                author = "Роберт Мартин",
                description = "Новая, куплена по ошибке дважды.",
                price = 1200,
                type = ListingType.SELL,
                condition = BookCondition.NEW,
                city = "Москва",
                ownerName = "Дмитрий",
                createdAt = now - 4 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Солярис",
                author = "Станислав Лем",
                description = "Мягкая обложка, немного потёрта на корешке.",
                price = 250,
                type = ListingType.SELL,
                condition = BookCondition.WORN,
                city = "Новосибирск",
                ownerName = "Елена",
                negotiable = true,
                createdAt = now - 5 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Гарри Поттер и узник Азкабана",
                author = "Дж. К. Роулинг",
                description = "Меняю на другие части серии.",
                price = null,
                type = ListingType.TRADE,
                condition = BookCondition.GOOD,
                city = "Екатеринбург",
                ownerName = "Мария",
                exchangeWish = "Части 4–7",
                createdAt = now - 6 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Преступление и наказание",
                author = "Фёдор Достоевский",
                description = "Школьное издание с комментариями.",
                price = 180,
                type = ListingType.SELL,
                condition = BookCondition.GOOD,
                city = "Самара",
                ownerName = "Павел",
                createdAt = now - 7 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Дюна",
                author = "Фрэнк Герберт",
                description = "Куплю первое издание на русском, состояние неважно.",
                price = null,
                type = ListingType.BUY,
                condition = BookCondition.WORN,
                city = "Москва",
                ownerName = "Кирилл",
                createdAt = now - 8 * day
            ),
            Book(
                id = UUID.randomUUID().toString(),
                title = "Приглашение на казнь",
                author = "Владимир Набоков",
                description = "Моё объявление — тестовая запись.",
                price = 600,
                type = ListingType.SELL,
                condition = BookCondition.NEW,
                city = "Москва",
                ownerName = "Вы",
                isMine = true,
                createdAt = now - 9 * day
            )
        )
    }
}
