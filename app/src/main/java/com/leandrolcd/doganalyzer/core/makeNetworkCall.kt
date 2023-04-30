package com.leandrolcd.doganalyzer.core

import com.leandrolcd.doganalyzer.ui.model.UiStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T> makeNetworkCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, call: suspend () -> T): UiStatus<T> {
    return withContext(dispatcher) {
        try {
            UiStatus.Success(call())
        } catch (e: Exception){
            UiStatus.Error(message = e.message.toString())
        }


    }
}

