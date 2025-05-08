package ru.startandroid.finstack.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.startandroid.finstack.data.api.CurrencyResponse
import ru.startandroid.finstack.data.api.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for currency exchange rates.
 */
class CurrencyRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "currency_prefs", Context.MODE_PRIVATE
    )

    private val _currencies = MutableLiveData<List<String>>()
    val currencies: LiveData<List<String>> = _currencies

    private val _currentCurrency = MutableLiveData<String>()
    val currentCurrency: LiveData<String> = _currentCurrency

    private val _lastUpdated = MutableLiveData<Date>()
    val lastUpdated: LiveData<Date> = _lastUpdated

    private val _exchangeRates = MutableLiveData<Map<String, Double>>()
    val exchangeRates: LiveData<Map<String, Double>> = _exchangeRates

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        // Load saved currency from preferences
        val savedCurrency = sharedPreferences.getString(PREF_CURRENCY, DEFAULT_CURRENCY)
        _currentCurrency.value = savedCurrency ?: DEFAULT_CURRENCY

        // Load saved last updated date
        val savedLastUpdated = sharedPreferences.getLong(PREF_LAST_UPDATED, 0)
        if (savedLastUpdated > 0) {
            _lastUpdated.value = Date(savedLastUpdated)
        }

        // Load saved currencies
        val savedCurrencies = sharedPreferences.getStringSet(PREF_CURRENCIES, null)
        if (!savedCurrencies.isNullOrEmpty()) {
            _currencies.value = savedCurrencies.toList().sorted()
        } else {
            _currencies.value = DEFAULT_CURRENCIES
        }
    }

    suspend fun fetchLatestRates(baseCurrency: String = currentCurrency.value ?: DEFAULT_CURRENCY) {
        try {
            _isLoading.value = true
            _error.value = null

            val response = RetrofitClient.currencyService.getLatestRates(baseCurrency)
            if (response.success) {
                _exchangeRates.value = response.rates
                _lastUpdated.value = parseApiDate(response.date)

                // Save the last updated date
                sharedPreferences.edit().putLong(PREF_LAST_UPDATED, _lastUpdated.value?.time ?: 0).apply()

                // Update currencies list if needed
                updateCurrenciesList(response)
            } else {
                _error.value = "API returned unsuccessful response"
            }
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error occurred"
        } finally {
            _isLoading.value = false
        }
    }

    fun setCurrentCurrency(currency: String) {
        _currentCurrency.value = currency
        sharedPreferences.edit().putString(PREF_CURRENCY, currency).apply()
    }

    private fun updateCurrenciesList(response: CurrencyResponse) {
        val currenciesList = mutableListOf(response.base)
        currenciesList.addAll(response.rates.keys)
        
        val uniqueCurrencies = currenciesList.distinct().sorted()
        _currencies.value = uniqueCurrencies
        
        sharedPreferences.edit()
            .putStringSet(PREF_CURRENCIES, uniqueCurrencies.toSet())
            .apply()
    }

    private fun parseApiDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    companion object {
        private const val DEFAULT_CURRENCY = "KZT"
        private const val PREF_CURRENCY = "current_currency"
        private const val PREF_LAST_UPDATED = "last_updated"
        private const val PREF_CURRENCIES = "available_currencies"
        
        private val DEFAULT_CURRENCIES = listOf(
            "KZT", "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "RUB"
        )
    }
} 