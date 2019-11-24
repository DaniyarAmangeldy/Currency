package kz.amangeldy.currency

data class RatesResponse(
    val base: String,
    val rates: Map<String, Float>
)

data class RatesModel(
    val base: String,
    val rates: List<Rate>
)
