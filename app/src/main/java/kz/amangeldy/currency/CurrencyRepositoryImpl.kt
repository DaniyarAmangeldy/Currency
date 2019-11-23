package kz.amangeldy.currency

import android.content.SharedPreferences
import androidx.core.content.edit

class CurrencyRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val apiClient: CurrencyApiClient
): CurrencyRepository {

    override var baseCurrencyUserValue = 1.0F

    override suspend fun setBaseCurrency(currency: String) {
        sharedPreferences.edit {
            putString(DEFAULT_CURRENCY_KEY, currency)
        }
    }

    override suspend fun getLatestRates(): RatesResponse {
        val base = sharedPreferences.getString(DEFAULT_CURRENCY_KEY, null)
        return apiClient.getLatestRates(base)
    }

    companion object {
        private const val DEFAULT_CURRENCY_KEY = "default_currency_key"
    }
}