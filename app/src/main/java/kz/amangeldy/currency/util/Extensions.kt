package kz.amangeldy.currency.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.text.DecimalFormat

val BigDecimal.displayString
    get() = DecimalFormat("0.##").format(this) ?: toString()

fun View.openKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}