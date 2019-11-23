package kz.amangeldy.currency

import android.app.Application
import kz.amangeldy.currency.di.apiModule
import kz.amangeldy.currency.di.coroutineModule
import kz.amangeldy.currency.di.repositoryModule
import kz.amangeldy.currency.di.useCaseModule
import kz.amangeldy.currency.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CurrencyApplication)
            modules(
                listOf(
                    apiModule,
                    coroutineModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}