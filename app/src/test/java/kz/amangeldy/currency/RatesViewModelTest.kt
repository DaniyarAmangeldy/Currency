package kz.amangeldy.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kz.amangeldy.currency.data.CurrencyApiClient
import kz.amangeldy.currency.data.CurrencyRepositoryImpl
import kz.amangeldy.currency.domain.CurrencyRepository
import kz.amangeldy.currency.domain.FetchRatesUseCase
import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.model.RatesResponse
import kz.amangeldy.currency.model.currencyCountryFlag
import kz.amangeldy.currency.model.currencyNameMap
import kz.amangeldy.currency.util.BlockingContextProvider
import kz.amangeldy.currency.util.CoroutineContextProvider
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import java.math.BigDecimal
import java.net.UnknownHostException


class RatesViewModelTest : KoinTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val apiClient: CurrencyApiClient = mock()
    private val ratesObserver: Observer<List<Rate>> = mock()
    private val connectivityObserver: Observer<Boolean> = mock()

    private val testModule = module {
        single { apiClient }
        single<CoroutineContextProvider> { BlockingContextProvider() }
        single<CurrencyRepository> { CurrencyRepositoryImpl(apiClient = get()) }
        single { FetchRatesUseCase(repository = get()) }
        single { RatesViewModel(coroutineContextProvider = get(), fetchRatesUseCase = get()) }
    }

    @Before
    fun setup() {
        startKoin { modules(testModule) }
    }

    @Test
    fun `start fetching periodically after init ViewModel`() {
        runBlocking {
            whenever(apiClient.getLatestRates(any())) doReturn RatesResponse("EUR", mapOf())

            RatesViewModel(get(), get())
            delay(6000)

            verify(apiClient, atLeast(3)).getLatestRates(any())
        }
    }

    @Test
    fun `test rates response maps to correct model`() {
        runBlocking {
            val expectedBaseRate = createRate("EUR", 1f.toBigDecimal())
            val expectedSimpleRate = createRate("USD", 100f.toBigDecimal())
            val response = RatesResponse(
                expectedBaseRate.code,
                mapOf(expectedSimpleRate.code to expectedSimpleRate.value.toFloat())
            )
            val expectedRates = listOf(
                expectedBaseRate,
                expectedSimpleRate convertTo expectedBaseRate
            )
            whenever(apiClient.getLatestRates(any())) doReturn response

            val viewModel = RatesViewModel(get(), get())
            viewModel.ratesLiveData.observeForever(ratesObserver)

            verify(ratesObserver).onChanged(expectedRates)
        }
    }

    @Test
    fun `test on base rate change should change values immediately`() {
        runBlocking {
            val simpleRate = createRate("USD", 100f.toBigDecimal())

            val baseRate = createRate("EUR", 20f.toBigDecimal())
            val response = RatesResponse(
                baseRate.code,
                mapOf(simpleRate.code to simpleRate.value.toFloat())
            )
            val expectedNewRates = listOf(baseRate, simpleRate convertTo baseRate)

            whenever(apiClient.getLatestRates(baseRate.code)) doReturn response

            val viewModel = RatesViewModel(get(), get())
            viewModel.ratesLiveData.observeForever(ratesObserver)
            viewModel.onBaseRateChanged(baseRate)

            verify(ratesObserver).onChanged(expectedNewRates)
        }
    }

    @Test
    fun `test connectivity true when no exception from fetch job`() {
        runBlocking {
            val response = RatesResponse("EUR", mapOf("USD" to 100f))
            whenever(apiClient.getLatestRates(any())) doReturn response

            val viewModel = RatesViewModel(get(), get())
            viewModel.hasConnectionLiveData.observeForever(connectivityObserver)

            verify(connectivityObserver).onChanged(true)
        }
    }

    @Test
    fun `test connectivity false when some exception from fetch job`() {
        runBlocking {
            whenever(apiClient.getLatestRates(any())) doAnswer { throw UnknownHostException() }

            val viewModel = RatesViewModel(get(), get())
            viewModel.hasConnectionLiveData.observeForever(connectivityObserver)

            verify(connectivityObserver).onChanged(false)
        }
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    private fun createRate(code: String, value: BigDecimal) =
        Rate(code, value, currencyNameMap[code], currencyCountryFlag[code])
}