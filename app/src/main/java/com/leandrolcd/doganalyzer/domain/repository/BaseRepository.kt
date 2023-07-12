package com.leandrolcd.doganalyzer.domain.repository

import com.leandrolcd.doganalyzer.domain.model.ResultType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class BaseRepository @Inject constructor(private val dispatcher: CoroutineDispatcher) {
    suspend fun <T> makeNetworkCall(call: suspend () -> T): ResultType<T> {
        return withContext(dispatcher) {
            try {
                ResultType.Success(call())
            } catch (e: Exception) {
                ResultType.Error(message = e.message.toString())
            }

        }
    }

}