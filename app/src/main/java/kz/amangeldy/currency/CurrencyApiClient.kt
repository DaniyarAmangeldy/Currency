package kz.amangeldy.currency

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiClient {

    @GET("/latest")
    suspend fun getLatestRates(@Query("base") baseCurrency: String? = null): RatesResponse
}