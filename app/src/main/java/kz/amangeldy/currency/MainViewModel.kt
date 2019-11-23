package kz.amangeldy.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MainViewModel: ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val job: Job

    val currencyLiveData = MutableLiveData<CurrencyResponse>()


    init {
        job = launch {
            val client = Retrofit
                .Builder()
                .baseUrl("https://revolut.duckdns.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrencyApiClient::class.java)

            while (true) {
                val data = client.getLatestCurrency("EUR")
                delay(1000)
                currencyLiveData.postValue(data)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}