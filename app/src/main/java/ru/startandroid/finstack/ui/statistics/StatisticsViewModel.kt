package ru.startandroid.finstack.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.data.model.TransactionWithCategory
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // All transactions with categories
    private val allTransactionsWithCategory = transactionRepository.allTransactionsWithCategory

    // Selected period
    private val _selectedPeriod = MutableLiveData(Period.THIS_MONTH)
    val selectedPeriod: LiveData<Period> = _selectedPeriod

    // Selected chart type
    private val _selectedChartType = MutableLiveData(ChartType.PIE_CHART)
    val selectedChartType: LiveData<ChartType> = _selectedChartType

    // Selected transaction type
    private val _selectedTransactionType = MutableLiveData(TransactionType.EXPENSE)
    val selectedTransactionType: LiveData<TransactionType> = _selectedTransactionType

    // Filtered transactions
    private val _filteredTransactions = MediatorLiveData<List<TransactionWithCategory>>()
    val filteredTransactions: LiveData<List<TransactionWithCategory>> = _filteredTransactions

    // Category summary
    private val _categorySummary = MediatorLiveData<Map<Category, Double>>()
    val categorySummary: LiveData<Map<Category, Double>> = _categorySummary

    // Daily summary for line chart
    private val _dailySummary = MediatorLiveData<Map<Date, Double>>()
    val dailySummary: LiveData<Map<Date, Double>> = _dailySummary

    // Monthly summary for bar chart
    private val _monthlySummary = MediatorLiveData<Map<String, Double>>()
    val monthlySummary: LiveData<Map<String, Double>> = _monthlySummary

    // Total for the selected period and type
    private val _totalAmount = MediatorLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    init {
        // Update filtered transactions whenever allTransactions or filters change
        _filteredTransactions.addSource(allTransactionsWithCategory) { updateFilteredTransactions() }
        _filteredTransactions.addSource(_selectedPeriod) { updateFilteredTransactions() }
        _filteredTransactions.addSource(_selectedTransactionType) { updateFilteredTransactions() }

        // Update summaries whenever filtered transactions change
        _categorySummary.addSource(_filteredTransactions) { updateCategorySummary() }
        _dailySummary.addSource(_filteredTransactions) { updateDailySummary() }
        _monthlySummary.addSource(_filteredTransactions) { updateMonthlySummary() }
        _totalAmount.addSource(_filteredTransactions) { updateTotalAmount() }
    }

    fun setPeriod(period: Period) {
        if (_selectedPeriod.value != period) {
            _selectedPeriod.value = period
        }
    }

    fun setChartType(chartType: ChartType) {
        if (_selectedChartType.value != chartType) {
            _selectedChartType.value = chartType
        }
    }

    fun setTransactionType(transactionType: TransactionType) {
        if (_selectedTransactionType.value != transactionType) {
            _selectedTransactionType.value = transactionType
        }
    }

    private fun updateFilteredTransactions() {
        val transactions = allTransactionsWithCategory.value ?: emptyList()
        val period = _selectedPeriod.value ?: Period.THIS_MONTH
        val transactionType = _selectedTransactionType.value ?: TransactionType.EXPENSE

        val (startDate, endDate) = getDateRangeForPeriod(period)

        // Filter by date range and transaction type
        val isExpense = transactionType == TransactionType.EXPENSE
        val filtered = transactions.filter { transaction ->
            transaction.transaction.isExpense == isExpense &&
                    !transaction.transaction.date.before(startDate) &&
                    !transaction.transaction.date.after(endDate)
        }

        _filteredTransactions.value = filtered
    }

    private fun updateCategorySummary() {
        val transactions = _filteredTransactions.value ?: emptyList()
        val categorySummary = mutableMapOf<Category, Double>()

        for (transaction in transactions) {
            val category = transaction.category
            val amount = transaction.transaction.amount
            categorySummary[category] = (categorySummary[category] ?: 0.0) + amount
        }

        _categorySummary.value = categorySummary
    }

    private fun updateDailySummary() {
        val transactions = _filteredTransactions.value ?: emptyList()
        val dailySummary = mutableMapOf<Date, Double>()

        for (transaction in transactions) {
            val date = truncateToDay(transaction.transaction.date)
            val amount = transaction.transaction.amount
            dailySummary[date] = (dailySummary[date] ?: 0.0) + amount
        }

        _dailySummary.value = dailySummary
    }

    private fun updateMonthlySummary() {
        val transactions = _filteredTransactions.value ?: emptyList()
        val monthlySummary = mutableMapOf<String, Double>()

        for (transaction in transactions) {
            val date = transaction.transaction.date
            val calendar = Calendar.getInstance().apply { time = date }
            val yearMonth = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
            val amount = transaction.transaction.amount
            monthlySummary[yearMonth] = (monthlySummary[yearMonth] ?: 0.0) + amount
        }

        _monthlySummary.value = monthlySummary
    }

    private fun updateTotalAmount() {
        val transactions = _filteredTransactions.value ?: emptyList()
        val total = transactions.sumOf { it.transaction.amount }
        _totalAmount.value = total
    }

    private fun truncateToDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getDateRangeForPeriod(period: Period): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (period) {
            Period.TODAY -> {
                // Start date is the beginning of today
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
            Period.THIS_WEEK -> {
                // Start date is the beginning of this week
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
            Period.THIS_MONTH -> {
                // Start date is the beginning of this month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
            Period.LAST_3_MONTHS -> {
                // Start date is 3 months ago
                calendar.add(Calendar.MONTH, -3)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
            Period.THIS_YEAR -> {
                // Start date is the beginning of this year
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
            Period.ALL_TIME -> {
                // Start date is far in the past
                calendar.set(1970, Calendar.JANUARY, 1)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
        }
    }

    /**
     * Period enum for filtering transactions
     */
    enum class Period {
        TODAY, THIS_WEEK, THIS_MONTH, LAST_3_MONTHS, THIS_YEAR, ALL_TIME
    }

    /**
     * Chart type enum
     */
    enum class ChartType {
        PIE_CHART, BAR_CHART, LINE_CHART
    }

    /**
     * Transaction type enum
     */
    enum class TransactionType {
        INCOME, EXPENSE
    }

    /**
     * Factory for creating StatisticsViewModel instances with dependencies.
     */
    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StatisticsViewModel(transactionRepository, categoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 