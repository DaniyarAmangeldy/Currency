package kz.amangeldy.currency

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.util.displayString
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

class RateActivityTest: KoinTest {

    @get:Rule val testRule = ActivityTestRule(RatesActivity::class.java, true, false)
    @get:Rule val idlingRule = TaskExecutorWithIdlingResourceRule()

    private val viewModel: RatesViewModel = mockk(relaxed = true)

    private val ratesLiveData = MutableLiveData<List<Rate>>()
    private val hasConnectionLiveData = MutableLiveData<Boolean>()

    private val testModule = module { viewModel { viewModel } }

    private val eurRate = Rate("EUR", 100.0.toBigDecimal(), "Euro", null)
    private val usdRate = Rate("USD", 80.12.toBigDecimal(), null, null)

    @Before
    fun setup() {
        loadKoinModules(testModule)
        every { viewModel.ratesLiveData } returns ratesLiveData
        every { viewModel.hasConnectionLiveData } returns hasConnectionLiveData
    }

    @Test
    fun recyclerViewShowViewsCorrectly() {
        launchActivity(rates = listOf(eurRate, usdRate))

        val expectedContentDescriptionForEuro = "${eurRate.currencyName} ${eurRate.value.displayString}"
        val expectedContentDescriptionForUsd = "currency code: ${usdRate.code} ${usdRate.value.displayString}"
        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(0))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withContentDescription(expectedContentDescriptionForEuro),
                    hasDescendant(withText(eurRate.code)),
                    hasDescendant(withText(eurRate.value.displayString))

                )
            ))
        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(1))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withContentDescription(expectedContentDescriptionForUsd),
                    hasDescendant(withText(usdRate.code)),
                    hasDescendant(withText(usdRate.value.displayString))
                )
            ))
    }

    @Test
    fun statusShowsOnlineCorrectly() {
        launchActivity(hasConnection = true)

        onView(withText("Online")).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun statusShowsOfflineCorrectly() {
        launchActivity(hasConnection = false)

        onView(withText("Offline")).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickToRateChangesBase() {
        val targetRate = usdRate
        val baseRate = eurRate
        every { viewModel.onBaseRateChanged(targetRate) } answers { ratesLiveData.postValue(listOf(targetRate, baseRate)) }

        launchActivity(rates = listOf(eurRate, usdRate))
        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(1)).perform(click())

        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(0))
            .check(matches(
                allOf(
                    ViewMatchers.isDisplayed(),
                    hasDescendant(withText(usdRate.code)),
                    hasDescendant(withText(usdRate.value.displayString))
                )
            ))
    }

    @Test
    fun enterTextToBaseRateChangesValues() {
        val newBaseRate = eurRate.copy(value = 200.toBigDecimal())
        val expectedRate = usdRate convertTo newBaseRate
        every { viewModel.onBaseRateChanged(newBaseRate) } answers { ratesLiveData.postValue(listOf(newBaseRate, expectedRate)) }

        launchActivity(rates = listOf(eurRate, usdRate))

        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(0, R.id.rate_field))
            .perform(replaceText(newBaseRate.value.displayString))

        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(1))
            .check(matches(
                allOf(
                    ViewMatchers.isDisplayed(),
                    hasDescendant(withText(expectedRate.code)),
                    hasDescendant(withText(expectedRate.value.displayString))
                )
            ))
    }

    @Test
    fun enterEmptyTextShouldChangeRateValueAsZero() {
        launchActivity(rates = listOf(eurRate, usdRate))

        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(0, R.id.rate_field))
            .perform(replaceText(""))

        verify { viewModel.onBaseRateChanged(eurRate.copy(value = 0.toBigDecimal())) }
    }

    private fun launchActivity(rates: List<Rate>? = null, hasConnection: Boolean? = null) {
        testRule.launchActivity(null)
        rates?.let { ratesLiveData.postValue(it) }
        hasConnection?.let { hasConnectionLiveData.postValue(it) }
    }

    @After
    fun cleanUp() {
        unloadKoinModules(testModule)
    }
}