package kz.amangeldy.currency.di

import kz.amangeldy.currency.CoroutineContextProvider
import kz.amangeldy.currency.CoroutineContextProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val coroutineModule: Module = module {
    single<CoroutineContextProvider> {
        CoroutineContextProviderImpl()
    }
}