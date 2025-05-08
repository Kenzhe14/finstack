package ru.startandroid.finstack.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

/**
 * Utility class for formatting different types of data.
 */
object FormatUtils {

    // Default currency code is now KZT (Kazakhstani Tenge)
    private const val DEFAULT_CURRENCY_CODE = "KZT"

    /**
     * Format amount with currency symbol.
     */
    fun formatCurrency(amount: Double, currencyCode: String = DEFAULT_CURRENCY_CODE): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        
        // Special case for KZT to display the tenge symbol correctly
        return if (currencyCode == "KZT") {
            // Format using currency instance but replace the symbol
            val formatted = format.format(amount)
            // Replace the currency symbol with the tenge symbol (₸)
            val symbol = format.currency.symbol
            formatted.replace(symbol, "₸")
        } else {
            format.format(amount)
        }
    }

    /**
     * Format amount as plain number with tenge symbol at the end.
     * This is an alternative formatting style commonly used in Kazakhstan.
     */
    fun formatTenge(amount: Double): String {
        val format = NumberFormat.getInstance(Locale("kk", "KZ"))
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 0
        return "${format.format(amount)} ₸"
    }

    /**
     * Format date as a readable string.
     */
    fun formatDate(date: Date, pattern: String = "dd MMM yyyy"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Format date as a time string.
     */
    fun formatTime(date: Date, pattern: String = "HH:mm"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Format date as a full date and time string.
     */
    fun formatDateTime(date: Date, pattern: String = "dd MMM yyyy, HH:mm"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * Format a percentage value.
     */
    fun formatPercentage(value: Double): String {
        val format = NumberFormat.getPercentInstance(Locale.getDefault())
        format.minimumFractionDigits = 1
        format.maximumFractionDigits = 1
        return format.format(value)
    }
} 