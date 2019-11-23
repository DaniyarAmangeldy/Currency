package kz.amangeldy.currency

class FetchRatesUseCase(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(): List<Rate> {
        val baseCurrencyUserValue = repository.baseCurrencyUserValue
        return repository.getLatestRates()
            .let { response ->
                response.rates
                    .map { entry -> Rate(entry.key, entry.value * baseCurrencyUserValue) }
                    .toMutableList()
                    .apply { add(0, Rate(response.base, baseCurrencyUserValue)) }
            }
    }
}