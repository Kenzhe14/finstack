package ru.startandroid.finstack.ui.addtransaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.startandroid.finstack.data.model.Category
import ru.startandroid.finstack.data.model.Transaction as FinTransaction
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import java.util.Date

/**
 * ViewModel for adding or editing transactions.
 */
class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionId: Long = 0L
) : ViewModel() {

    // Indicates if we're editing an existing transaction
    private val isEditMode = transactionId > 0

    // Categories for selection
    val expenseCategories = categoryRepository.getCategoriesByType(true)
    val incomeCategories = categoryRepository.getCategoriesByType(false)

    // Transaction data
    private val _amount = MutableLiveData(0.0)
    val amount: LiveData<Double> = _amount

    private val _description = MutableLiveData("")
    val description: LiveData<String> = _description

    private val _date = MutableLiveData(Date())
    val date: LiveData<Date> = _date

    private val _selectedCategory = MutableLiveData<Category?>()
    val selectedCategory: LiveData<Category?> = _selectedCategory

    private val _isExpense = MutableLiveData(true)
    val isExpense: LiveData<Boolean> = _isExpense

    // Status and events
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _transactionSaved = MutableLiveData<Boolean>(false)
    val transactionSaved: LiveData<Boolean> = _transactionSaved

    // Original transaction for edit mode
    private var originalTransaction: FinTransaction? = null

    init {
        if (isEditMode) {
            loadTransaction()
        }
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            _isLoading.value = true
            transactionRepository.getTransactionById(transactionId).observeForever { transaction ->
                transaction?.let {
                    originalTransaction = it
                    _amount.value = it.amount
                    _description.value = it.description
                    _date.value = it.date
                    _isExpense.value = it.isExpense

                    // Load the category
                    categoryRepository.getCategoryById(it.categoryId).observeForever { category ->
                        _selectedCategory.value = category
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun setAmount(amount: Double) {
        _amount.value = amount
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setDate(date: Date) {
        _date.value = date
    }

    fun setCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun setExpenseType(isExpense: Boolean) {
        if (_isExpense.value != isExpense) {
            _isExpense.value = isExpense
            // Reset category when changing transaction type
            _selectedCategory.value = null
        }
    }

    fun saveTransaction() {
        val amount = _amount.value
        val description = _description.value?.trim()
        val date = _date.value
        val category = _selectedCategory.value
        val isExpense = _isExpense.value ?: true

        // Validate inputs
        if (amount == null || amount <= 0) {
            _errorMessage.value = "Please enter a valid amount"
            return
        }

        if (description.isNullOrBlank()) {
            _errorMessage.value = "Please enter a description"
            return
        }

        if (date == null) {
            _errorMessage.value = "Please select a date"
            return
        }

        if (category == null) {
            _errorMessage.value = "Please select a category"
            return
        }

        // Create or update transaction
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = if (isEditMode && originalTransaction != null) {
                    originalTransaction!!.copy(
                        amount = amount,
                        description = description,
                        date = date,
                        categoryId = category.id,
                        isExpense = isExpense
                    )
                } else {
                    FinTransaction(
                        amount = amount,
                        description = description,
                        date = date,
                        categoryId = category.id,
                        isExpense = isExpense
                    )
                }

                if (isEditMode) {
                    transactionRepository.update(transaction)
                } else {
                    transactionRepository.insert(transaction)
                }

                _transactionSaved.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error saving transaction: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Factory for creating AddTransactionViewModel instances with dependencies.
     */
    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository,
        private val transactionId: Long = 0L
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddTransactionViewModel(
                    transactionRepository,
                    categoryRepository,
                    transactionId
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 