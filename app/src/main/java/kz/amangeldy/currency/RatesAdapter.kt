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
    private val onRateFocused: (item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (rate: Rate) -> Unit
): ListAdapter<Rate, RatesViewHolder>(RateDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
        return RatesViewHolder(view, onRateFocused, onCurrencyValueChangeListener)
    }

    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewDetachedFromWindow(holder: RatesViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }
}

class RateDiffUtil: DiffUtil.ItemCallback<Rate>() {

    override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean = oldItem == newItem
}


class RatesViewHolder(
    private val view: View,
    private val onRateFocused: (item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (rate: Rate) -> Unit
): RecyclerView.ViewHolder(view) {

    private val titleTextView = view.rate_title
    private val valueEditText = view.rate_field

    private val currencyTextWatcher = valueEditText.doAfterTextChanged { s: Editable? ->
        val text = s?.toString() ?: return@doAfterTextChanged

        val currentValue = if (text.isEmpty()) {
            0.toBigDecimal()
        } else {
            text.toBigDecimalOrNull() ?: return@doAfterTextChanged
        }
        rate?.let { onCurrencyValueChangeListener.invoke(it.copy(value = currentValue)) }
    }

    private var rate: Rate? = null

    fun bind(rate: Rate) {
        this.rate = rate
        titleTextView.text = rate.name
        val rateValue = rate.value.displayString
        if (!valueEditText.isFocused) valueEditText.setText(rateValue, true)
        valueEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                return@setOnFocusChangeListener
            }
            if (!(v as EditText).isSelected) {
                v.setSelection(v.text.length)
            }
            v.openKeyboard()
            onRateFocused.invoke(rate)
        }
        view.setOnClickListener {
            valueEditText.post { it.requestFocus() }
        }
    }

    fun onDetach() {
        valueEditText.clearFocus()
        valueEditText.removeTextChangedListener(currencyTextWatcher)
        valueEditText.onFocusChangeListener = null
    }

    private fun EditText.setText(text: String, skipWatcherNotify: Boolean) {
        if (skipWatcherNotify) {
            removeTextChangedListener(currencyTextWatcher)
            setText(text)
            addTextChangedListener(currencyTextWatcher)
        }
    }
}