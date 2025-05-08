package ru.startandroid.finstack.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.startandroid.finstack.data.repository.CategoryRepository
import ru.startandroid.finstack.data.repository.CurrencyRepository
import ru.startandroid.finstack.data.repository.TransactionRepository
import ru.startandroid.finstack.util.PreferenceManager

/**
 * ViewModel for the settings screen.
 */
class SettingsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    // Theme
    private val _themeMode = MutableLiveData<Int>()
    val themeMode: LiveData<Int> = _themeMode

    // Currency
    val availableCurrencies = currencyRepository.currencies
    val currentCurrency = currencyRepository.currentCurrency
    val isLoading = currencyRepository.isLoading
    val error = currencyRepository.error

    // Reset data
    private val _dataResetComplete = MutableLiveData<Boolean>(false)
    val dataResetComplete: LiveData<Boolean> = _dataResetComplete

    init {
        // Load current theme from preferences
        _themeMode.value = preferenceManager.getThemeMode()

        // Load currency data if needed
        viewModelScope.launch {
            currencyRepository.fetchLatestRates()
        }
    }

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        preferenceManager.setThemeMode(mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun setCurrency(currencyCode: String) {
        currencyRepository.setCurrentCurrency(currencyCode)
        preferenceManager.setCurrency(currencyCode)
    }

    fun resetAllData() {
        viewModelScope.launch {
            // Delete all transactions
            transactionRepository.deleteAll()

            // Reset completed flag
            _dataResetComplete.value = true
        }
    }

    fun resetCompleted() {
        _dataResetComplete.value = false
    }

    /**
     * Factory for creating SettingsViewModel instances with dependencies.
     */
    class Factory(
        private val transactionRepository: TransactionRepository,
        private val categoryRepository: CategoryRepository,
        private val currencyRepository: CurrencyRepository,
        private val preferenceManager: PreferenceManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(
                    transactionRepository,
                    categoryRepository,
                    currencyRepository,
                    preferenceManager
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 