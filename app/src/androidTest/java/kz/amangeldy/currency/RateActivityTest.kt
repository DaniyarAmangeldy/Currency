package kz.amangeldy.currency

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import io.mockk.every
import io.mockk.mockk
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

    private val eurRate = Rate("EUR", 100.0.toBigDecimal(), null, null)
    private val usdRate = Rate("USD", 80.12.toBigDecimal(), null, null)

    @Before
    fun setup() {
        loadKoinModules(testModule)
        every { viewModel.ratesLiveData } returns ratesLiveData
        every { viewModel.hasConnectionLiveData } returns hasConnectionLiveData
    }

    @Test
    fun recyclerViewShowViewsCorrectly() {
        testRule.launchActivity(null)
        ratesLiveData.postValue(listOf(eurRate, usdRate))

        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(0))
            .check(matches(
                allOf(
                    ViewMatchers.isDisplayed(),
                    hasDescendant(withText(eurRate.code)),
                    hasDescendant(withText(eurRate.value.displayString))
                )
            ))
        onView(RecyclerViewMatcher(R.id.rate_list).atPosition(1))
            .check(matches(
                allOf(
                    ViewMatchers.isDisplayed(),
                    hasDescendant(withText(usdRate.code)),
                    hasDescendant(withText(usdRate.value.displayString))
                )
            ))
    }

    @Test
    fun statusShowsOnlineCorrectly() {
        testRule.launchActivity(null)
        hasConnectionLiveData.postValue(true)

        onView(withText("Online")).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun statusShowsOfflineCorrectly() {
        testRule.launchActivity(null)
        hasConnectionLiveData.postValue(false)

        onView(withText("Offline")).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickToRateChangesBase() {
        val targetRate = usdRate
        val baseRate = eurRate
        every { viewModel.onBaseRateChanged(targetRate) } answers { ratesLiveData.postValue(listOf(targetRate, baseRate)) }

        testRule.launchActivity(null)
        ratesLiveData.postValue(listOf(eurRate, usdRate))
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
        val expectedRate = usdRate.copy(value = newBaseRate.value * usdRate.value)
        every { viewModel.onBaseRateChanged(newBaseRate) } answers { ratesLiveData.postValue(listOf(newBaseRate, expectedRate)) }

        testRule.launchActivity(null)
        ratesLiveData.postValue(listOf(eurRate, usdRate))

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

    @After
    fun cleanUp() {
        unloadKoinModules(testModule)
    }
}