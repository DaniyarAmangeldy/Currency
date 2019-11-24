package kz.amangeldy.currency.util

import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val io: CoroutineContext
    val main: CoroutineContext
    val computation: CoroutineContext
}