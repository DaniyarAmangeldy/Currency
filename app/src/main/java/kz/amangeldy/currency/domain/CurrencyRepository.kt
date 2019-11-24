package kz.amangeldy.currency.domain

import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.model.RatesModel

interface CurrencyRepository {

    var fetchedRates: RatesModel

    var baseRate: Rate

    suspend fun fetchRates(): RatesModel
}