package kz.amangeldy.currency

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rate.view.*

class RatesAdapter(
    private val onClickListener: (view: View, item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (value: String) -> Unit
): ListAdapter<Rate, RatesViewHolder>(RateDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
        return RatesViewHolder(view, onClickListener, onCurrencyValueChangeListener)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: RatesViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onUnbind()
    }
}

class RateDiffUtil: DiffUtil.ItemCallback<Rate>() {

    override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean = oldItem == newItem
}


class RatesViewHolder(
    private val view: View,
    private val onClickListener: (view: View, item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (value: String) -> Unit
): RecyclerView.ViewHolder(view) {

    private val currencyTextWatcher = view.rate_field.doAfterTextChanged { s: Editable? ->
        val currentValue = s?.toString() ?: return@doAfterTextChanged
        onCurrencyValueChangeListener.invoke(currentValue)
    }

    fun bind(rate: Rate) {
        view.rate_title.text = rate.name
        val rateValue = rate.value.toString()
        view.rate_field.setText(rateValue, true)
        if (view.rate_field.hasFocus()) view.rate_field.setSelection(rateValue.length)
        view.rate_field.setOnTouchListener(null)
        view.setOnClickListener { onClickListener(view, rate) }
        view.rate_field.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            (v as EditText).setSelection(v.text.length)
        }
    }

    fun onUnbind() {
        view.rate_field.removeTextChangedListener(currencyTextWatcher)
        view.rate_field.onFocusChangeListener = null
        view.setOnClickListener(null)
    }

    private fun EditText.setText(text: String, skipWatcherNotify: Boolean) {
        if (skipWatcherNotify) {
            view.rate_field.removeTextChangedListener(currencyTextWatcher)
            view.rate_field.setText(text)
            view.rate_field.addTextChangedListener(currencyTextWatcher)
        }
    }
}