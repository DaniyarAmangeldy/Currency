package kz.amangeldy.currency.domain

import kz.amangeldy.currency.model.Rate

class FetchRatesUseCase(
    private val repository: CurrencyRepository
) {

    suspend operator fun invoke(userRate: Rate? = null): List<Rate> {
        if (repository.baseRate == userRate) {
            return repository.fetchedRates.rates.convertRates(userRate)
        }
        userRate?.let { repository.baseRate = it }
        return fetch(repository.baseRate)
    }

    private suspend fun fetch(baseRate: Rate): List<Rate> {
        return repository
            .fetchRates()
            .rates
            .convertRates(baseRate)
    }

    private fun List<Rate>.convertRates(baseRate: Rate): List<Rate> {
        return this
            .map { it convertTo baseRate }
            .sortedBy { it.code }
            .toMutableList()
            .also { it.add(0, baseRate) }
    }
}