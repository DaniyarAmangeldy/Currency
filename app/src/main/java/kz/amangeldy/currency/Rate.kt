package kz.amangeldy.currency

import java.math.BigDecimal

data class Rate(
    val name: String,
    val value: BigDecimal
) {

    infix fun convertTo(other: Rate): Rate {
        return Rate(name, value * other.value)
    }
}