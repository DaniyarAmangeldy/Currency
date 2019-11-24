package kz.amangeldy.currency.model

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class Rate(
    val code: String,
    val currencyName: String?,
    @DrawableRes val iconRes: Int?,
    val value: BigDecimal
) {

    infix fun convertTo(other: Rate): Rate {
        return copy(value = value * other.value)
    }
}