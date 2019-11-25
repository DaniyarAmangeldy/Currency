package kz.amangeldy.currency.util

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class CoroutineContextProviderImpl: CoroutineContextProvider {

    override val io: CoroutineContext = Dispatchers.IO
    override val main: CoroutineContext = Dispatchers.Main
    override val computation: CoroutineContext = Dispatchers.Default
}

class BlockingContextProvider: CoroutineContextProvider {
    override val io: CoroutineContext = Dispatchers.Unconfined
    override val main: CoroutineContext = Dispatchers.Unconfined
    override val computation: CoroutineContext = Dispatchers.Unconfined
}