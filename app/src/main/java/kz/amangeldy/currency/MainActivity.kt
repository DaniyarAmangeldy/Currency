package kz.amangeldy.currency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.currencyLiveData.observe(this, Observer { onTextComes(it) })
    }

    private fun onTextComes(response: CurrencyResponse) {
        text.text = response.toString()
    }
}