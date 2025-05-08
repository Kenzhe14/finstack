package ru.startandroid.finstack.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Utility class for managing app preferences.
 */
class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    fun getThemeMode(): Int {
        return sharedPreferences.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setThemeMode(themeMode: Int) {
        sharedPreferences.edit().putInt(KEY_THEME_MODE, themeMode).apply()
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    fun setCurrency(currencyCode: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currencyCode).apply()
    }

    companion object {
        private const val PREFS_NAME = "finstack_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_CURRENCY = "currency"
        private const val DEFAULT_CURRENCY = "KZT"
    }
} 