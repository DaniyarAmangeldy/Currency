package kz.amangeldy.currency.custom.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import kz.amangeldy.currency.R

class OnlineStateTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): AppCompatTextView(context, attrs, defStyleAttr) {

    private var isOnline: Boolean? = null

    fun setState(isOnline: Boolean) {
        if (this.isOnline == isOnline) return
        this.isOnline = isOnline
        setText(if (isOnline) R.string.online else R.string.offline)
        val colorRes = if (isOnline) android.R.color.holo_green_light else android.R.color.holo_red_light
        setTextColor(ContextCompat.getColor(context, colorRes))
    }
}