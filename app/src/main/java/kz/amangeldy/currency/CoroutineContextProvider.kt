package kz.amangeldy.currency

import kotlin.coroutines.CoroutineContext

interface CoroutineContextProvider {
    val io: CoroutineContext
    val main: CoroutineContext
    val computation: CoroutineContext
}