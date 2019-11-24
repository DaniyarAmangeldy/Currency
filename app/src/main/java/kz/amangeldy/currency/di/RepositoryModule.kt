package kz.amangeldy.currency.di

import android.content.Context
import kz.amangeldy.currency.domain.CurrencyRepository
import kz.amangeldy.currency.data.CurrencyRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single { get<Context>().getSharedPreferences("CurrencySharedPreferences", Context.MODE_PRIVATE) }
    single<CurrencyRepository> {
        CurrencyRepositoryImpl(
            sharedPreferences = get(),
            apiClient = get()
        )
    }
}