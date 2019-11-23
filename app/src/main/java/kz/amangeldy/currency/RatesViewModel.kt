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
    private val fetchRatesUseCase: FetchRatesUseCase,
    private val setBaseCurrencyUseCase: SetBaseCurrencyUseCase,
    private val setBaseCurrencyUserValue: SetBaseCurrencyUserValue
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = coroutineContextProvider.io

    private var fetchRatesJob: Job = startFetchRatesJob()

    private var uiIdleStateAwaitJob: Job? = null

    val ratesLiveData = MutableLiveData<List<Rate>>()

    fun onBaseRateValueChanged(value: String) {
        launch {
            value.toFloatOrNull()?.let { setBaseCurrencyUserValue.invoke(it) }
        }
    }

    fun onUserInteract() {
        if (uiIdleStateAwaitJob?.isActive == true) {
            uiIdleStateAwaitJob?.cancel()
        }
        uiIdleStateAwaitJob = startUiIdleStateAwaitJob()
    }

    fun onBaseCurrencyChanged(currency: String) {
        launch { setBaseCurrencyUseCase.invoke(currency) }
    }

    private fun startFetchRatesJob(): Job = launch {
        while (true) {
            val data = fetchRatesUseCase.invoke()
            withContext(coroutineContextProvider.main) {
                ratesLiveData.value = data
            }
            delay(DELAY_BETWEEN_FETCHES_RATES_MS)
        }
    }

    private fun startUiIdleStateAwaitJob(): Job =
        launch {
            if (fetchRatesJob.isActive) {
                fetchRatesJob.cancel()
            }
            delay(DELAY_AFTER_UI_IDLE_STATE_MS)
            fetchRatesJob = startFetchRatesJob()
        }

    override fun onCleared() {
        super.onCleared()
        fetchRatesJob.cancel()
        uiIdleStateAwaitJob?.cancel()
    }

    companion object {
        private const val DELAY_BETWEEN_FETCHES_RATES_MS = 1000L
        private const val DELAY_AFTER_UI_IDLE_STATE_MS = 5000L
    }
}