package kz.amangeldy.currency.di

import kz.amangeldy.currency.data.CurrencyRepositoryImpl
import kz.amangeldy.currency.domain.CurrencyRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CurrencyRepository> { CurrencyRepositoryImpl(apiClient = get()) }
}