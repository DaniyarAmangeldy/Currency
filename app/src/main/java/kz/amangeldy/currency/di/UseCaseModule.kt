package kz.amangeldy.currency.di

import kz.amangeldy.currency.domain.SomeUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { SomeUseCase(repository = get()) }
}