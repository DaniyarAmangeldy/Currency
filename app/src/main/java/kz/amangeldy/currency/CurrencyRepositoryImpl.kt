package kz.amangeldy.currency

import android.content.SharedPreferences

class CurrencyRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val apiClient: CurrencyApiClient
): CurrencyRepository {

    override var baseRate: Rate = Rate(DEFAULT_RATE_NAME, DEFAULT_RATE_VALUE.toBigDecimal())

    override var fetchedRates: RatesModel = RatesModel(baseRate.name, emptyList())

    override suspend fun fetchRates(): RatesModel {
        fetchedRates = apiClient.getLatestRates(baseRate.name).toRatesModel()
        return fetchedRates
    }

    companion object {
        private const val DEFAULT_RATE_NAME = "EUR"
        private const val DEFAULT_RATE_VALUE = 1.0F
    }
}