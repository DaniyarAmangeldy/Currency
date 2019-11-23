package kz.amangeldy.currency

class SetBaseCurrencyUseCase(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(base: String) {
        repository.setBaseCurrency(base)
    }
}