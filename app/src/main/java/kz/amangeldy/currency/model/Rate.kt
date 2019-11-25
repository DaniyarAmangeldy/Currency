package kz.amangeldy.currency.model

import androidx.annotation.DrawableRes
import java.math.BigDecimal
import java.math.RoundingMode

data class Rate(
    val code: String,
    val value: BigDecimal,
    val currencyName: String?,
    @DrawableRes val iconRes: Int?
) {

    infix fun convertTo(other: Rate): Rate {
        return copy(value = (value * other.value).setScale(2, RoundingMode.FLOOR))
    }
}