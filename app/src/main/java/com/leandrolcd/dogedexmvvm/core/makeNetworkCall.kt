package com.leandrolcd.dogedexmvvm.core

import com.leandrolcd.dogedexmvvm.ui.model.UiStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T> makeNetworkCall(dispatcher: CoroutineDispatcher = Dispatchers.IO,call: suspend () -> T): UiStatus<T> {
    return withContext(dispatcher) {
        var msjError: String
        try {
            UiStatus.Success(call())
        } catch (e: Exception){
            msjError = e.message.toString()
            UiStatus.Error(message = msjError)
        }


    }
}

