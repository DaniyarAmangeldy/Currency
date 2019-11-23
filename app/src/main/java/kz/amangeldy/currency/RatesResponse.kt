package kz.amangeldy.currency

data class RatesResponse(
    val base: String,
    val rates: Map<String, Float>
)

