package kz.amangeldy.currency.util

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class CoroutineContextProviderImpl: CoroutineContextProvider {

    override val io: CoroutineContext = Dispatchers.IO
    override val main: CoroutineContext = Dispatchers.Main
    override val computation: CoroutineContext = Dispatchers.Default
}