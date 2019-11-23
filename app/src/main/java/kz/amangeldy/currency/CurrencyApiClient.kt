package kz.amangeldy.currency

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiClient {

    @GET("/latest")
    suspend fun getLatestCurrency(@Query("base") baseCurrency: String): CurrencyResponse
}