package kz.amangeldy.currency.di

import kz.amangeldy.currency.SomeUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { SomeUseCase(repository = get()) }
}