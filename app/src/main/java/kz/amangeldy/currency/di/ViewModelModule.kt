package kz.amangeldy.currency.di

import kz.amangeldy.currency.RatesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModel {
        RatesViewModel(
            coroutineContextProvider = get(),
            someUseCase = get()
        )
    }
}