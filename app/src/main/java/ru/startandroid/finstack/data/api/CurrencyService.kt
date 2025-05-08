package ru.startandroid.finstack.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service for retrieving currency exchange rates.
 */
interface CurrencyService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") baseCurrency: String = "USD"
    ): CurrencyResponse
}

/**
 * Data class for currency response from the API.
 */
data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>,
    val success: Boolean
) 