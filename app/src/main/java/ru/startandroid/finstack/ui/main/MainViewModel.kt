package ru.startandroid.finstack.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.model.TransactionWithCategory
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.CurrencyRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the main screen.
 */
class MainViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    // Currency data
    val currentCurrency = currencyRepository.currentCurrency
    val isLoading = currencyRepository.isLoading
    val error = currencyRepository.error

    // All transactions
    val allTransactionsWithCategory = transactionRepository.allTransactionsWithCategory

    // Balances
    private val _totalBalance = MediatorLiveData<Double>().apply { value = 0.0 }
    val totalBalance: LiveData<Double> = _totalBalance

    private val _totalIncome = MediatorLiveData<Double>().apply { value = 0.0 }
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MediatorLiveData<Double>().apply { value = 0.0 }
    val totalExpense: LiveData<Double> = _totalExpense

    // Recent transactions
    private val _recentTransactions = MediatorLiveData<List<TransactionWithCategory>>()
    val recentTransactions: LiveData<List<TransactionWithCategory>> = _recentTransactions

    // Selected period
    private val _selectedPeriod = MutableLiveData(Period.THIS_MONTH)
    val selectedPeriod: LiveData<Period> = _selectedPeriod

    // Period specific data
    private val _periodIncome = MediatorLiveData<Double>().apply { value = 0.0 }
    val periodIncome: LiveData<Double> = _periodIncome

    private val _periodExpense = MediatorLiveData<Double>().apply { value = 0.0 }
    val periodExpense: LiveData<Double> = _periodExpense

    // Category summary
    private val _expenseByCategoryMap = MediatorLiveData<Map<Category, Double>>()
    val expenseByCategoryMap: LiveData<Map<Category, Double>> = _expenseByCategoryMap

    init {
        // Load currency data
        viewModelScope.launch {
            currencyRepository.fetchLatestRates()
        }

        // Observe total income
        val incomeSource = transactionRepository.getTotalByType(false)
        _totalIncome.addSource(incomeSource) { income ->
            _totalIncome.value = income ?: 0.0
            updateTotalBalance()
        }

        // Observe total expense
        val expenseSource = transactionRepository.getTotalByType(true)
        _totalExpense.addSource(expenseSource) { expense ->
            _totalExpense.value = expense ?: 0.0
            updateTotalBalance()
        }

        // Observe transactions and update recent transactions
        _recentTransactions.addSource(allTransactionsWithCategory) { transactions ->
            _recentTransactions.value = transactions.take(5)
            updateCategorySummary(transactions)
        }

        // Set up period observers
        updatePeriodData()
    }

    private fun updateTotalBalance() {
        _totalBalance.value = (_totalIncome.value ?: 0.0) - (_totalExpense.value ?: 0.0)
    }

    private fun updateCategorySummary(transactions: List<TransactionWithCategory>) {
        val expenseByCategory = mutableMapOf<Category, Double>()
        
        transactions.filter { it.transaction.isExpense }.forEach { transaction ->
            val amount = transaction.transaction.amount
            val category = transaction.category
            expenseByCategory[category] = (expenseByCategory[category] ?: 0.0) + amount
        }
        
        _expenseByCategoryMap.value = expenseByCategory
    }

    fun setPeriod(period: Period) {
        if (_selectedPeriod.value != period) {
            _selectedPeriod.value = period
            updatePeriodData()
        }
    }

    private fun updatePeriodData() {
        val (startDate, endDate) = getDateRangeForPeriod(_selectedPeriod.value ?: Period.THIS_MONTH)
        
        // Observe period income
        val periodIncomeSource = transactionRepository.getTotalByTypeInDateRange(false, startDate, endDate)
        _periodIncome.addSource(periodIncomeSource) { income ->
            _periodIncome.value = income ?: 0.0
        }
        
        // Observe period expense
        val periodExpenseSource = transactionRepository.getTotalByTypeInDateRange(true, startDate, endDate)
        _periodExpense.addSource(periodExpenseSource) { expense ->
            _periodExpense.value = expense ?: 0.0
        }
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
            Period.ALL_TIME -> {
                // Start date is far in the past
                calendar.set(1970, Calendar.JANUARY, 1)
                val startDate = calendar.time
                return Pair(startDate, endDate)
            }
        }
    }

    /**
     * Period enum for filtering transactions.
     */
    enum class Period {
        TODAY, THIS_WEEK, THIS_MONTH, ALL_TIME
    }

    /**
     * Factory for creating MainViewModel instances with dependencies.
     */
    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository,
        private val currencyRepository: CurrencyRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(transactionRepository, categoryRepository, currencyRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 