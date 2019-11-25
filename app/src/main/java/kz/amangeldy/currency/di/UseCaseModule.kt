package kz.amangeldy.currency.di

import kz.amangeldy.currency.domain.FetchRatesUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { FetchRatesUseCase(repository = get()) }
}