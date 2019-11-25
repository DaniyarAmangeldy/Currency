package kz.amangeldy.currency.di

import kz.amangeldy.currency.BuildConfig
import kz.amangeldy.currency.data.CurrencyApiClient
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule: Module = module {
    single {
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(CurrencyApiClient::class.java)
    }
}