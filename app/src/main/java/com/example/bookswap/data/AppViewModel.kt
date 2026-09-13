package com.example.bookswap.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AppViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    var books by mutableStateOf(repository.getAll())
        private set

    var settings by mutableStateOf(AppSettings())
        private set

    var query by mutableStateOf("")
    var typeFilter by mutableStateOf<ListingType?>(null)
        private set
    var conditionFilter by mutableStateOf<BookCondition?>(null)
        private set
    var sortOrder by mutableStateOf(SortOrder.NEWEST)
        private set
    var viewMode by mutableStateOf(ViewMode.LIST)
        private set
    var maxPrice by mutableStateOf(2000f)
        private set

    private val favorites = mutableStateMapOf<String, Boolean>()

    // --- фильтры -------------------------------------------------------

    fun applyTypeFilter(type: ListingType?) { typeFilter = type }
    fun applyConditionFilter(c: BookCondition?) { conditionFilter = c }
    fun setSort(order: SortOrder) { sortOrder = order }
    fun applyMaxPrice(value: Float) { maxPrice = value }
    fun toggleViewMode() {
        viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
    }

    fun resetFilters() {
        query = ""
        typeFilter = null
        conditionFilter = null
        sortOrder = SortOrder.NEWEST
        maxPrice = 2000f
    }

    val visibleBooks: List<Book>
        get() {
            var result = books

            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                result = result.filter {
                    it.title.lowercase().contains(q) ||
                        it.author.lowercase().contains(q) ||
                        it.city.lowercase().contains(q)
                }
            }
            typeFilter?.let { t -> result = result.filter { it.type == t } }
            conditionFilter?.let { c -> result = result.filter { it.condition == c } }
            if (settings.onlyWithPrice) result = result.filter { it.price != null }
            if (settings.hideMyListings) result = result.filter { !it.isMine }
            result = result.filter { it.price == null || it.price <= maxPrice.toInt() }

            return when (sortOrder) {
                SortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
                SortOrder.PRICE_ASC -> result.sortedBy { it.price ?: Int.MAX_VALUE }
                SortOrder.PRICE_DESC -> result.sortedByDescending { it.price ?: -1 }
                SortOrder.TITLE -> result.sortedBy { it.title }
            }
        }

    val myBooks: List<Book> get() = books.filter { it.isMine }
    val favoriteBooks: List<Book> get() = books.filter { favorites[it.id] == true }

    // --- избранное -----------------------------------------------------

    fun isFavorite(id: String): Boolean = favorites[id] == true

    fun toggleFavorite(id: String) {
        favorites[id] = !(favorites[id] ?: false)
    }

    val favoriteCount: Int get() = favorites.count { it.value }

    // --- CRUD ----------------------------------------------------------

    fun getBook(id: String): Book? = repository.getById(id)

    fun addBook(book: Book) {
        repository.add(book)
        books = repository.getAll()
    }

    /** Возвращает позицию удалённого элемента — нужна для «Отменить». */
    fun removeBook(id: String): Pair<Book, Int>? {
        val book = repository.getById(id) ?: return null
        val index = repository.indexOf(id)
        repository.remove(id)
        favorites.remove(id)
        books = repository.getAll()
        return book to index
    }

    fun restoreBook(book: Book, position: Int) {
        repository.restore(book, position)
        books = repository.getAll()
    }

    fun resetData() {
        repository.reset()
        favorites.clear()
        books = repository.getAll()
        resetFilters()
    }

    // --- настройки -----------------------------------------------------

    fun updateSettings(block: (AppSettings) -> AppSettings) {
        settings = block(settings)
    }
}
