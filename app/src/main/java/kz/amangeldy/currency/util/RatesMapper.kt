package kz.amangeldy.currency.util

import kz.amangeldy.currency.model.*

fun RatesResponse.toRatesModel(): RatesModel {
    return RatesModel(
        this.base,
        rates.map {
            Rate(
                it.key,
                it.value.toBigDecimal(),
                currencyNameMap[it.key],
                currencyCountryFlag[it.key]
            )
        }
    )
}