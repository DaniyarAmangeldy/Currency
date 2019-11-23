package kz.amangeldy.currency.di

import kz.amangeldy.currency.FetchRatesUseCase
import kz.amangeldy.currency.SetBaseCurrencyUseCase
import kz.amangeldy.currency.SetBaseCurrencyUserValue
import org.koin.dsl.module

val useCaseModule = module {
    single { FetchRatesUseCase(repository = get()) }
    single { SetBaseCurrencyUseCase(repository = get()) }
    single { SetBaseCurrencyUserValue(repository = get()) }
}