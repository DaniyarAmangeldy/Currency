package kz.amangeldy.currency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kz.amangeldy.currency.model.Rate
import org.koin.android.viewmodel.ext.android.viewModel

class RatesActivity : AppCompatActivity() {

    private val ratesViewModel: RatesViewModel by viewModel()
    private val rateAdapter: RatesAdapter = RatesAdapter(::onBaseRateValueChanged, ::onBaseRateValueChanged)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ratesViewModel.ratesLiveData.observe(this, Observer { onTextComes(it) })
        rate_list.adapter = rateAdapter
        (rate_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun onBaseRateValueChanged(item: Rate) {
        ratesViewModel.onBaseRateChanged(item)
    }

    private fun onTextComes(rates: List<Rate>) {
        rateAdapter.submitList(rates)
    }
}