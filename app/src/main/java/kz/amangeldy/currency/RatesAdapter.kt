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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_rate.view.*
import kz.amangeldy.currency.model.Rate
import kz.amangeldy.currency.util.displayString
import kz.amangeldy.currency.util.openKeyboard
import java.math.BigDecimal

class RatesAdapter(
    private val onRateFocused: (item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (rate: Rate) -> Unit
) : ListAdapter<Rate, RatesViewHolder>(RateDiffUtil()) {

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

class RateDiffUtil : DiffUtil.ItemCallback<Rate>() {

    override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean = oldItem == newItem
}


class RatesViewHolder(
    private val view: View,
    private val onRateFocused: (item: Rate) -> Unit,
    private val onCurrencyValueChangeListener: (rate: Rate) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val titleTextView = view.rate_title
    private val flagImageView = view.country_flag
    private val subtitleTextView = view.rate_subtitle
    private val valueEditText = view.rate_field

    private val currencyTextWatcher = valueEditText.doAfterTextChanged { s: Editable? ->
        val text = s?.toString() ?: return@doAfterTextChanged

        val currentValue = text.toPositiveBigDecimalOrNull() ?: return@doAfterTextChanged
        rate?.let { onCurrencyValueChangeListener.invoke(it.copy(value = currentValue)) }
    }

    private var rate: Rate? = null

    fun bind(rate: Rate) {
        this.rate = rate
        titleTextView.text = rate.code
        subtitleTextView.text = rate.currencyName
        rate.iconRes?.let { Glide.with(view).load(it).into(flagImageView) }
        val rateValue = rate.value.displayString
        view.contentDescription = contentDescriptionText(rate)

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
        view.setOnClickListener { valueEditText.post { it.requestFocus() } }
    }

    fun onDetach() {
        valueEditText.clearFocus()
        valueEditText.removeTextChangedListener(currencyTextWatcher)
        valueEditText.onFocusChangeListener = null
        flagImageView.setImageDrawable(null)
    }

    private fun EditText.setText(text: String, skipWatcherNotify: Boolean) {
        if (skipWatcherNotify) {
            removeTextChangedListener(currencyTextWatcher)
            setText(text)
            addTextChangedListener(currencyTextWatcher)
        }
    }

    private fun contentDescriptionText(rate: Rate): String {
        val name = rate.currencyName ?: view.context.getString(R.string.currency_code_fmt, rate.code)
        return "$name ${rate.value.displayString}"
    }

    private fun String.toPositiveBigDecimalOrNull(): BigDecimal? {
        if (isEmpty()) return 0.toBigDecimal()
        val decimal = toBigDecimalOrNull() ?: return null
        return if (decimal < 0.toBigDecimal()) null else decimal
    }
}