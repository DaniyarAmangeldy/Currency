package kz.amangeldy.currency

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class RatesActivity : AppCompatActivity() {

    private val ratesViewModel: RatesViewModel by viewModel()
    private val rateAdapter: RatesAdapter = RatesAdapter(::onRateItemClicked, ::onBaseRateValueChanged)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ratesViewModel.ratesLiveData.observe(this, Observer { onTextComes(it) })
        rate_list.adapter = rateAdapter
        (rate_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun onRateItemClicked(view: View, item: Rate) {
        ratesViewModel.onUserInteract()
        ratesViewModel.onBaseRateValueChanged(item.value.toString())
        ratesViewModel.onBaseCurrencyChanged(item.name)
        moveItemToTop(item)
        view.requestFocus()
    }
    
    private fun onBaseRateValueChanged(value: String) {
        ratesViewModel.onBaseRateValueChanged(value)
    }

    private fun onTextComes(rates: List<Rate>) {
        rateAdapter.submitList(rates)
    }

    private fun moveItemToTop(rate: Rate) {
        val newList = rateAdapter.currentList.sortedBy { it.name }.toMutableList()
        val index = newList.indexOf(rate)
        if (index == NO_ITEM) return
        newList.removeAt(index)
        newList.add(0, rate)
        rateAdapter.submitList(newList) { rate_list.scrollToPosition(0) }
    }

    companion object {
        private const val NO_ITEM = -1
    }
}