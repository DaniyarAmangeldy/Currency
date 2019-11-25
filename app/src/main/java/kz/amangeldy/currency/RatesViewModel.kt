package kz.amangeldy.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.amangeldy.currency.domain.FetchRatesUseCase
import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.util.CoroutineContextProvider
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

class RatesViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val fetchRatesUseCase: FetchRatesUseCase
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = coroutineContextProvider.io

    val ratesLiveData: LiveData<List<Rate>>
        get() = ratesLiveDataMutable
    val hasConnectionLiveData: LiveData<Boolean>
        get() = hasConnectionLiveDataMutable

    private val ratesLiveDataMutable = MutableLiveData<List<Rate>>()
    private val hasConnectionLiveDataMutable = MutableLiveData<Boolean>()

    private var periodicFetchRatesJob: Job = getPeriodicFetchJob()

    private var forceFetchRatesJob: Job? = null

    private var delayFetchJob: Job? = null

    fun onBaseRateChanged(rate: Rate) {
        stopAllJob()
        forceFetchRatesJob = getForceFetchJob(rate)
        delayFetch()
    }

    private fun getForceFetchJob(rate: Rate? = null): Job {
        return launch { fetchData(rate) }
    }

    private fun getPeriodicFetchJob(): Job = launch {
        while (true) {
            fetchData()
            delay(DELAY_BETWEEN_FETCHES_RATES_MS)
        }
    }

    private suspend fun fetchData(rate: Rate? = null) {
        try {
            val data = fetchRatesUseCase.invoke(rate)
//            withContext(coroutineContextProvider.main) {
                ratesLiveDataMutable.postValue(data)
                hasConnectionLiveDataMutable.postValue(true)
//            }
        } catch (e: Throwable) {
            when (e) {
                is IOException,
                is UnknownHostException,
                is HttpException -> {
                    hasConnectionLiveDataMutable.postValue(false)
                    println(e)
                }
                else -> throw e
            }
        }
    }

    private fun delayFetch() {
        if (periodicFetchRatesJob.isActive) {
            periodicFetchRatesJob.cancel()
        }
        if (delayFetchJob?.isActive == true) {
            delayFetchJob?.cancel()
        }
        delayFetchJob = launch {
            delay(DELAY_AFTER_UI_IDLE_STATE_MS)
            periodicFetchRatesJob = getPeriodicFetchJob()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllJob()
    }

    private fun stopAllJob() {
        periodicFetchRatesJob.cancel()
        delayFetchJob?.cancel()
        forceFetchRatesJob?.cancel()
    }

    companion object {
        private const val DELAY_BETWEEN_FETCHES_RATES_MS = 2000L
        private const val DELAY_AFTER_UI_IDLE_STATE_MS = 5000L
    }
}