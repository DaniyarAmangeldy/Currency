package kz.amangeldy.currency.di

import kz.amangeldy.currency.util.CoroutineContextProvider
import kz.amangeldy.currency.util.CoroutineContextProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val coroutineModule: Module = module {
    single<CoroutineContextProvider> {
        CoroutineContextProviderImpl()
    }
}