package kz.amangeldy.currency.data

import kz.amangeldy.currency.domain.CurrencyRepository
import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.model.RatesModel
import kz.amangeldy.currency.model.currencyCountryFlag
import kz.amangeldy.currency.model.currencyNameMap
import kz.amangeldy.currency.util.toRatesModel

class CurrencyRepositoryImpl(
    private val apiClient: CurrencyApiClient
): CurrencyRepository {

    override var baseRate: Rate = Rate(
        DEFAULT_RATE_NAME,
        DEFAULT_RATE_VALUE.toBigDecimal(),
        currencyNameMap[DEFAULT_RATE_NAME],
        currencyCountryFlag[DEFAULT_RATE_NAME]
    )

    override var fetchedRates: RatesModel =
        RatesModel(baseRate.code, emptyList())

    override suspend fun fetchRates(): RatesModel {
        fetchedRates = apiClient.getLatestRates(baseRate.code).toRatesModel()
        return fetchedRates
    }

    companion object {
        private const val DEFAULT_RATE_NAME = "EUR"
        private const val DEFAULT_RATE_VALUE = 1.0F
    }
}