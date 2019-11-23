package kz.amangeldy.currency

interface CurrencyRepository {

    var baseCurrencyUserValue: Float

    suspend fun setBaseCurrency(currency: String)

    suspend fun getLatestRates(): RatesResponse
}