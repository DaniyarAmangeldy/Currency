package kz.amangeldy.currency.util

import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.model.RatesModel
import kz.amangeldy.currency.model.RatesResponse
import kz.amangeldy.currency.model.currencyCountryFlag
import kz.amangeldy.currency.model.currencyNameMap

fun RatesResponse.toRatesModel(): RatesModel {
    return RatesModel(
        this.base,
        rates.map {
            Rate(
                it.key,
                currencyNameMap[it.key],
                currencyCountryFlag[it.key],
                it.value.toBigDecimal()
            )
        }
    )
}