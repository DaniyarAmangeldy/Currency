package kz.amangeldy.currency

class SetBaseCurrencyUserValue(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(value: Float) {
        repository.baseCurrencyUserValue = value
    }
}