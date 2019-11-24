package kz.amangeldy.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class RatesViewModel(
    private val coroutineContextProvider: CoroutineContextProvider,
    private val someUseCase: SomeUseCase
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = coroutineContextProvider.io

    private var periodicFetchRatesJob: Job = getPeriodicFetchJob()

    private var forceFetchRatesJob: Job? = null

    private var delayFetchJob: Job? = null

    val ratesLiveData = MutableLiveData<List<Rate>>()

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
        val data = someUseCase.invoke(rate)
        withContext(coroutineContextProvider.main) {
            ratesLiveData.value = data
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