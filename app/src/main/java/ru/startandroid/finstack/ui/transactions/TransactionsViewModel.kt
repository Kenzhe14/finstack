package ru.startandroid.finstack.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.model.TransactionWithCategory
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the transactions screen.
 */
class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // All transactions
    private val _allTransactionsWithCategory = transactionRepository.allTransactionsWithCategory
    val allTransactionsWithCategory: LiveData<List<TransactionWithCategory>> = _allTransactionsWithCategory

    // Filtered transactions
    private val _filteredTransactions = MutableLiveData<List<TransactionWithCategory>>()
    val filteredTransactions: LiveData<List<TransactionWithCategory>> = _filteredTransactions

    // Categories for filtering
    val allCategories = categoryRepository.allCategories
    val expenseCategories = categoryRepository.getCategoriesByType(true)
    val incomeCategories = categoryRepository.getCategoriesByType(false)

    // Filters
    private val _filterType = MutableLiveData<TransactionType?>(null)
    val filterType: LiveData<TransactionType?> = _filterType

    private val _filterCategory = MutableLiveData<Category?>(null)
    val filterCategory: LiveData<Category?> = _filterCategory

    private val _filterDateRange = MutableLiveData<Pair<Date, Date>?>(null)
    val filterDateRange: LiveData<Pair<Date, Date>?> = _filterDateRange

    // Search query
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    // Sorting
    private val _sortOrder = MutableLiveData(SortOrder.DATE_DESC)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    init {
        // Initial filter application
        applyFilters()

        // Observe all transactions for filter changes
        _allTransactionsWithCategory.observeForever { transactions ->
            applyFilters()
        }
    }

    fun setTypeFilter(type: TransactionType?) {
        if (_filterType.value != type) {
            _filterType.value = type
            applyFilters()
        }
    }

    fun setCategoryFilter(category: Category?) {
        if (_filterCategory.value != category) {
            _filterCategory.value = category
            applyFilters()
        }
    }

    fun setDateRangeFilter(dateRange: Pair<Date, Date>?) {
        if (_filterDateRange.value != dateRange) {
            _filterDateRange.value = dateRange
            applyFilters()
        }
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
            applyFilters()
        }
    }

    fun setSortOrder(order: SortOrder) {
        if (_sortOrder.value != order) {
            _sortOrder.value = order
            applyFilters()
        }
    }

    fun clearFilters() {
        _filterType.value = null
        _filterCategory.value = null
        _filterDateRange.value = null
        _searchQuery.value = ""
        applyFilters()
    }

    fun deleteTransaction(transaction: FinTransaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
        }
    }

    private fun applyFilters() {
        val transactions = _allTransactionsWithCategory.value ?: return
        var filtered = transactions

        // Apply type filter
        _filterType.value?.let { type ->
            filtered = filtered.filter {
                when (type) {
                    TransactionType.INCOME -> !it.transaction.isExpense
                    TransactionType.EXPENSE -> it.transaction.isExpense
                }
            }
        }

        // Apply category filter
        _filterCategory.value?.let { category ->
            filtered = filtered.filter { it.category.id == category.id }
        }

        // Apply date range filter
        _filterDateRange.value?.let { (startDate, endDate) ->
            filtered = filtered.filter {
                !it.transaction.date.before(startDate) && !it.transaction.date.after(endDate)
            }
        }

        // Apply search query
        val query = _searchQuery.value?.trim() ?: ""
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.transaction.description.contains(query, ignoreCase = true) ||
                        it.category.name.contains(query, ignoreCase = true)
            }
        }

        // Apply sorting
        filtered = when (_sortOrder.value) {
            SortOrder.DATE_DESC -> filtered.sortedByDescending { it.transaction.date }
            SortOrder.DATE_ASC -> filtered.sortedBy { it.transaction.date }
            SortOrder.AMOUNT_DESC -> filtered.sortedByDescending { it.transaction.amount }
            SortOrder.AMOUNT_ASC -> filtered.sortedBy { it.transaction.amount }
            else -> filtered.sortedByDescending { it.transaction.date }
        }

        _filteredTransactions.value = filtered
    }

    /**
     * Enum for transaction type filter
     */
    enum class TransactionType {
        INCOME, EXPENSE
    }

    /**
     * Enum for sorting order
     */
    enum class SortOrder {
        DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
    }

    /**
     * Factory for creating TransactionsViewModel instances with dependencies.
     */
    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionsViewModel(transactionRepository, categoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 