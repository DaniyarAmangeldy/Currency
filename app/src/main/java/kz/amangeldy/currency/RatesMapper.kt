package kz.amangeldy.currency

fun RatesResponse.toRatesModel(): RatesModel {
    return RatesModel(this.base, rates.map { Rate(it.key, it.value.toBigDecimal()) })
}