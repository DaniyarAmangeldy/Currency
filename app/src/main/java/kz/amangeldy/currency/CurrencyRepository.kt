package kz.amangeldy.currency

interface CurrencyRepository {

    var fetchedRates: RatesModel

    var baseRate: Rate

    suspend fun fetchRates(): RatesModel
}