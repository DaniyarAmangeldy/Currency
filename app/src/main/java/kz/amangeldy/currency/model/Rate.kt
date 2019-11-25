package kz.amangeldy.currency.model

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class Rate(
    val code: String,
    val value: BigDecimal,
    val currencyName: String?,
    @DrawableRes val iconRes: Int?
) {

    infix fun convertTo(other: Rate): Rate {
        return copy(value = value * other.value)
    }
}